"""PriceModel and its YAML loader (Task 1.2).

Holds the reproducible pricing parameters (Farm_Rate, Grade_Floor, Role_Weight,
Category_Factor, Manual_Override, anti-exploit multiplier, sell-price basis and
economy variant) and exposes helpers used by the PriceEngine.

Requirements: 1.1, 1.3, 7.6, 8.5.
"""

import math
from dataclasses import dataclass, field
from typing import Dict

import yaml

from .constants import GRADE_ORDER, PROGRESSION_STAGES


class PriceModelError(ValueError):
    """Raised when a PriceModel fails validation."""


@dataclass
class PriceModel:
    farm_rate: Dict[str, int] = field(default_factory=dict)
    grade_floor: Dict[str, int] = field(default_factory=dict)
    role_weight: Dict[str, float] = field(default_factory=dict)
    category_factor: Dict[str, float] = field(default_factory=dict)
    target_farm_hours: Dict[str, float] = field(default_factory=dict)
    manual_override: Dict[int, int] = field(default_factory=dict)
    anti_exploit_mult: float = 2.0
    sell_price_basis: str = "half"  # "half" | "full"
    variant: str = "balanced"
    variant_multiplier: float = 1.0
    enchant_target_level: int = 6

    # ---- helpers -------------------------------------------------------
    def npc_sell_price(self, price, basis=None):
        """NPC sell price given the catalog ``price`` and the sell-price basis.

        basis="half" -> price / 2 (real L2 buyback); basis="full" -> price.
        Returns 0 when price is missing/None/<=0 (Requirement 7.4 fallback).
        """
        if price is None or price <= 0:
            return 0.0
        b = basis or self.sell_price_basis
        if b == "half":
            return price / 2.0
        return float(price)

    def anti_exploit_floor(self, price, basis=None):
        """Anti-exploit lower bound = anti_exploit_mult * npc_sell_price."""
        return self.anti_exploit_mult * self.npc_sell_price(price, basis)

    def grade_floor_of(self, grade):
        """Grade_Floor for a grade, falling back to nearest defined lower grade."""
        if grade in self.grade_floor:
            return self.grade_floor[grade]
        # Fallback: walk down the grade order to the nearest defined floor.
        if grade in GRADE_ORDER:
            idx = GRADE_ORDER.index(grade)
            for i in range(idx, -1, -1):
                g = GRADE_ORDER[i]
                if g in self.grade_floor:
                    return self.grade_floor[g]
        # Last resort: smallest defined floor or 1.
        return min(self.grade_floor.values()) if self.grade_floor else 1

    # ---- validation ----------------------------------------------------
    def validate(self):
        """Validate model invariants (Requirement 1.1, 1.3)."""
        # Farm_Rate: all defined values must be positive integers.
        for stage, rate in self.farm_rate.items():
            if not isinstance(rate, int) or rate <= 0:
                raise PriceModelError(
                    "Farm_Rate for stage %r must be a positive integer, got %r"
                    % (stage, rate)
                )
        # Grade_Floor must be non-decreasing along GRADE_ORDER (Requirement 1.3).
        defined = [g for g in GRADE_ORDER if g in self.grade_floor]
        prev = None
        for g in defined:
            val = self.grade_floor[g]
            if not isinstance(val, int) or val <= 0:
                raise PriceModelError(
                    "Grade_Floor for %r must be a positive integer, got %r"
                    % (g, val)
                )
            if prev is not None and val < prev:
                raise PriceModelError(
                    "Grade_Floor must be non-decreasing: %r=%d < previous %d"
                    % (g, val, prev)
                )
            prev = val
        # anti_exploit_mult sanity.
        if self.anti_exploit_mult <= 0:
            raise PriceModelError("anti_exploit_mult must be > 0")
        if self.sell_price_basis not in ("half", "full"):
            raise PriceModelError(
                "sell_price_basis must be 'half' or 'full', got %r"
                % (self.sell_price_basis,)
            )
        return self


def _coerce_int_keys(d):
    return {int(k): int(v) for k, v in (d or {}).items()}


def load_price_model(path):
    """Load a PriceModel from a YAML file and validate it."""
    with open(path, "r", encoding="utf-8") as fh:
        data = yaml.safe_load(fh) or {}
    model = _model_from_dict(data)
    return model.validate()


def _model_from_dict(data):
    return PriceModel(
        farm_rate={str(k): int(v) for k, v in (data.get("farm_rate") or {}).items()},
        grade_floor={str(k): int(v) for k, v in (data.get("grade_floor") or {}).items()},
        role_weight={str(k): float(v) for k, v in (data.get("role_weight") or {}).items()},
        category_factor={
            str(k): float(v) for k, v in (data.get("category_factor") or {}).items()
        },
        target_farm_hours={
            str(k): float(v) for k, v in (data.get("target_farm_hours") or {}).items()
        },
        manual_override=_coerce_int_keys(data.get("manual_override")),
        anti_exploit_mult=float(data.get("anti_exploit_mult", 2.0)),
        sell_price_basis=str(data.get("sell_price_basis", "half")),
        variant=str(data.get("variant", "balanced")),
        variant_multiplier=float(data.get("variant_multiplier", 1.0)),
        enchant_target_level=int(data.get("enchant_target_level", 6)),
    )
