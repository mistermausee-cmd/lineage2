"""PriceEngine — the Buy_Price formula core (Task 4.1) plus overrides (4.4),
grade/role/enchant differentiation (4.6) and Upgrade_Chain pricing (4.12).

Formula (design):

    Buy_Price = ceil( max( Base, AntiExploit ) ),  Buy_Price >= 1
    Base      = Grade_Floor(grade) * Role_Weight(role) * Category_Factor(cat)
                * Stat_Rank(item) * variant_multiplier
    AntiExploit = anti_exploit_mult * npc_sell_price(price, basis)

All inputs (grade, price, stats) come from the Item_Catalog; nothing is
hard-coded (Requirement 8.5).

Requirements: 8.1, 7.1, 7.2, 7.3, 12.5, 8.5, 8.2, 8.3, 8.6, 8.7, 6.x.
"""

import math
from dataclasses import dataclass, field
from typing import Optional

from .constants import ADENA_ID


@dataclass
class PriceResult:
    production_id: int
    buy_price: int
    base: float
    anti_exploit_floor: float
    npc_sell_price: float
    override_applied: bool = False
    override_value: Optional[int] = None
    override_raised: bool = False        # override provided but bumped by anti-exploit
    anti_exploit_bound: bool = False     # anti-exploit floor determined the price
    fallback_no_stats: bool = False
    notes: list = field(default_factory=list)


class PriceEngine:
    def __init__(self, model, catalog):
        self.model = model
        self.catalog = catalog

    # ---- core formula (Task 4.1) --------------------------------------
    def compute_price(
        self,
        production_id,
        role,
        category_factor=1.0,
        stat_rank=1.0,
        override=None,
        fallback_no_stats=False,
    ):
        """Compute the Buy_Price for one position.

        ``override`` (Manual_Override) replaces Base but still passes through the
        max() with the anti-exploit floor (Requirement 8.3/8.6).
        """
        item = self.catalog.get(production_id)
        grade = item.grade if item else "NG"
        price = item.price if item else None

        grade_floor = self.model.grade_floor_of(grade)
        role_weight = self.model.role_weight.get(role, 1.0)

        base = (
            grade_floor
            * role_weight
            * category_factor
            * stat_rank
            * self.model.variant_multiplier
        )

        npc_sell = self.model.npc_sell_price(price)
        floor = self.model.anti_exploit_floor(price)

        base_for_max = base
        override_applied = False
        override_raised = False
        if override is not None:
            base_for_max = float(override)

        raw = max(base_for_max, floor)
        buy_price = int(math.ceil(raw))
        if buy_price < 1:
            buy_price = 1

        anti_exploit_bound = floor > base_for_max

        if override is not None:
            if buy_price == override:
                override_applied = True
            else:
                override_raised = True

        return PriceResult(
            production_id=production_id,
            buy_price=buy_price,
            base=base,
            anti_exploit_floor=floor,
            npc_sell_price=npc_sell,
            override_applied=override_applied,
            override_value=override,
            override_raised=override_raised,
            anti_exploit_bound=anti_exploit_bound,
            fallback_no_stats=fallback_no_stats,
        )

    # ---- stat-aware pricing (Tasks 4.6 / 4.9) -------------------------
    def compute_with_stats(
        self,
        production_id,
        role,
        category_factor=1.0,
        esv=None,
        esv_range=None,
        k=1.0,
        override=None,
        fallback_no_stats=False,
    ):
        """Compute price deriving Stat_Rank from an Effective_Stat_Value.

        Stat_Rank = 1.0 + k * normalize(esv, lo, hi); monotonically increasing in
        esv, so higher Effective_Stat_Value yields a higher (or equal) Base
        (Requirement 4.1, 4.2, 3.6).
        """
        from .stat_value import normalize, stat_rank

        if esv is None:
            sr = 1.0
        else:
            lo, hi = esv_range if esv_range else (0.0, esv)
            sr = stat_rank(normalize(esv, lo, hi), k)
        return self.compute_price(
            production_id,
            role=role,
            category_factor=category_factor,
            stat_rank=sr,
            override=override,
            fallback_no_stats=fallback_no_stats,
        )

    # ---- Upgrade_Chain pricing (Task 4.12) ----------------------------
    def price_upgrade_chain(
        self, chain_resolver, root_role="weapon", category_factor=1.0, step=None,
    ):
        """Price every link of every chain line in a resolver.

        Link N price = link N-1 price * step (strictly increasing); the step
        defaults to Category_Factor['upgrade_chain_step'] (1.4). Root links are
        priced by the base formula. Endpoints take the prestige role. All prices
        pass through the anti-exploit floor and stay >= 1.

        Returns a dict production_id -> Buy_Price, or None if the chain is
        unresolvable/cyclic (the caller must roll back — Requirement 6.5).

        Requirements: 6.1, 6.2, 6.3, 6.4, 3.6, 4.3.
        """
        from .chain_resolver import ChainError

        if step is None:
            step = self.model.category_factor.get("upgrade_chain_step", 1.4)
        try:
            order = chain_resolver.topological_order()
        except ChainError:
            return None

        prices = {}
        for pid in order:
            parent = chain_resolver.predecessor.get(pid)
            internal_parent = parent is not None and parent in chain_resolver.by_production
            is_endpoint = chain_resolver.is_endpoint(pid)
            role = "prestige" if is_endpoint else root_role

            item = self.catalog.get(pid)
            floor = self.model.anti_exploit_floor(item.price if item else None)

            if internal_parent:
                candidate = prices[parent] * step
            else:
                # Root link: base formula.
                candidate = self.compute_price(
                    pid, role=role, category_factor=category_factor
                ).base

            buy = int(math.ceil(max(candidate, floor)))
            if buy < 1:
                buy = 1
            # Guarantee strict increase over the internal parent.
            if internal_parent and buy <= prices[parent]:
                buy = prices[parent] + 1
            prices[pid] = buy
        return prices

    def rollback_chain_prices(self, chain_resolver, role="weapon"):
        """Fallback pricing for an unresolvable/cyclic chain (Requirement 6.5).

        Each position is priced independently at its Grade_Floor, still passing
        the anti-exploit floor and staying > 0.
        """
        prices = {}
        for pid in chain_resolver.by_production:
            prices[pid] = self.compute_price(pid, role=role).buy_price
        return prices

    # ---- Manual_Override (Task 4.4) -----------------------------------
    def resolve_override(self, production_id):
        """Return the Manual_Override value for a production id, or None.

        A record referencing an id absent from the catalog is treated as unused
        (Requirement 8.7 — the caller/report records it separately).
        """
        return self.model.manual_override.get(production_id)

    def compute_with_override(
        self, production_id, role, category_factor=1.0, stat_rank=1.0,
        fallback_no_stats=False,
    ):
        """Compute price pulling the Manual_Override (if any) from the model.

        - A valid override (>= anti-exploit floor) becomes the Buy_Price
          (Requirement 8.3).
        - An override that violates the anti-exploit rule is NOT applied; the
          price is raised above NPC_Sell_Price and the fact is reported
          (Requirement 8.6) via ``override_raised``.
        """
        override = self.resolve_override(production_id)
        return self.compute_price(
            production_id,
            role=role,
            category_factor=category_factor,
            stat_rank=stat_rank,
            override=override,
            fallback_no_stats=fallback_no_stats,
        )
