"""Effective_Stat_Value and Stat_Rank (Task 3.3).

Effective_Stat_Value(item) = S_base + S_upgrade + S_enchant

- S_base    : weighted convolution of the item's base <stats>.
- S_upgrade : stat gain at the endpoint of the item's Upgrade_Chain
              (endpoint stats - chain-base stats); 0 when the item is not in a chain.
- S_enchant : stat gain achievable by enchanting to ``enchant_target_level``
              relative to +0 (modelled as a per-level increment of combat stats).

The score is normalised inside a grade/category to yield
Stat_Rank = 1.0 + k * normalized_score. When an item has no stat data, the
value is a fallback flagged for the Price_Report (Requirement 4.5).

Requirements: 3.6, 4.1, 5.6, 4.5.
"""

# Weights for the base-stat convolution. Unknown stats get a modest default so
# any stat still contributes monotonically. Absolute value is used because some
# stats (e.g. accCombat) may be negative on trade-off items.
STAT_WEIGHTS = {
    "pAtk": 1.0,
    "mAtk": 1.0,
    "pDef": 1.0,
    "mDef": 1.0,
    "maxHp": 0.5,
    "maxMp": 0.5,
    "critRate": 0.3,
    "pCritRate": 0.3,
    "pAtkSpd": 0.2,
    "mAtkSpd": 0.2,
    "pCritDmg": 0.3,
    "accCombat": 0.2,
    "evasion": 0.2,
    "pAtkRange": 0.1,
    "speed": 0.3,
    "regHp": 0.2,
    "regMp": 0.2,
}
DEFAULT_STAT_WEIGHT = 0.5

# Combat stats used to estimate the enchant contribution.
_WEAPON_COMBAT = ("pAtk", "mAtk")
_ARMOR_COMBAT = ("pDef", "mDef")

# Per-level fraction of the combat base that a single enchant level adds.
ENCHANT_PER_LEVEL_FRACTION = 0.03


def s_base(stats):
    """Weighted convolution of base stats -> non-negative score."""
    if not stats:
        return 0.0
    total = 0.0
    for stype, val in stats.items():
        w = STAT_WEIGHTS.get(stype, DEFAULT_STAT_WEIGHT)
        total += w * abs(float(val))
    return total


def _combat_base(item):
    """Sum of the item's main combat stats used for the enchant estimate."""
    stats = item.stats
    itype = (item.item_type or "").lower()
    if itype == "weapon":
        keys = _WEAPON_COMBAT
    elif itype == "armor":
        keys = _ARMOR_COMBAT
    else:
        # Jewelry / etc: use whatever offensive/defensive stats exist.
        keys = _WEAPON_COMBAT + _ARMOR_COMBAT
    return sum(abs(float(stats.get(k, 0.0))) for k in keys)


def s_enchant(item, enchant_target_level):
    """Estimated stat gain achievable by enchanting to the target level."""
    if not item.stats or enchant_target_level <= 0:
        return 0.0
    return _combat_base(item) * ENCHANT_PER_LEVEL_FRACTION * enchant_target_level


def s_upgrade(item, chain_resolver=None, catalog=None):
    """Stat gain from base -> endpoint of the item's Upgrade_Chain (0 if none)."""
    if chain_resolver is None or catalog is None:
        return 0.0
    pid = item.id
    if not chain_resolver.is_chain_link(pid) and chain_resolver.is_endpoint(pid):
        # Not part of a resolvable chain.
        pass
    # Find the chain base (root) and endpoint for this production.
    try:
        base_id = _chain_root(chain_resolver, pid)
        endpoint_id = _chain_endpoint(chain_resolver, pid)
    except Exception:
        return 0.0
    if base_id is None or endpoint_id is None or base_id == endpoint_id:
        return 0.0
    base_item = catalog.get(base_id)
    end_item = catalog.get(endpoint_id)
    if base_item is None or end_item is None:
        return 0.0
    gain = s_base(end_item.stats) - s_base(base_item.stats)
    return max(0.0, gain)


def _chain_root(cr, production_id):
    """Walk predecessor links down to the chain base (root produced item)."""
    current = production_id
    seen = set()
    while current in cr.predecessor:
        if current in seen:
            return None
        seen.add(current)
        base = cr.predecessor[current]
        if base in cr.by_production and base != current:
            current = base
        else:
            # base is external (not produced here) -> current is the root link.
            return current
    return current


def _chain_endpoint(cr, production_id):
    """Follow successor links up to the endpoint of the line."""
    current = production_id
    seen = set()
    while True:
        succ = cr.successors.get(current)
        if not succ:
            return current
        # Follow the successor that is produced in this multisell.
        nxt = None
        for s in sorted(succ):
            if s in cr.by_production and s != current:
                nxt = s
                break
        if nxt is None or nxt in seen:
            return current
        seen.add(nxt)
        current = nxt


def effective_stat_value(item, chain_resolver=None, catalog=None, enchant_target_level=6):
    """Total Effective_Stat_Value = S_base + S_upgrade + S_enchant.

    Returns a tuple (value, used_fallback) where used_fallback is True when the
    item has no stat data (Requirement 4.5).
    """
    if not item.stats:
        return 0.0, True
    base = s_base(item.stats)
    up = s_upgrade(item, chain_resolver, catalog)
    ench = s_enchant(item, enchant_target_level)
    return base + up + ench, False


def stat_rank(normalized_score, k=1.0):
    """Stat_Rank = 1.0 + k * normalized_score, normalized_score in [0, 1]."""
    return 1.0 + k * normalized_score


def normalize(value, lo, hi):
    """Normalize value into [0, 1] given a [lo, hi] range (safe for lo==hi)."""
    if hi <= lo:
        return 0.0
    x = (value - lo) / (hi - lo)
    if x < 0.0:
        return 0.0
    if x > 1.0:
        return 1.0
    return x
