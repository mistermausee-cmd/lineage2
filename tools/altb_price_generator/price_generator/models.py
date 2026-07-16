"""Core data models for the Price_Generator (design: Data Models section).

Types: CatalogItem, Ingredient, MultisellPosition, PriceReportEntry, plus the
supporting Multisell container.
"""

from dataclasses import dataclass, field
from typing import Dict, List, Optional

from .constants import ADENA_ID


@dataclass
class CatalogItem:
    """An item definition from the Item_Catalog (stats/items/*.xml)."""

    id: int
    grade: str  # crystal_type, e.g. "R99"; "NG" when absent
    price: Optional[int]  # NPC <set name="price">; None when absent
    stats: Dict[str, float] = field(default_factory=dict)
    name: str = ""
    item_type: str = ""  # Weapon / Armor / EtcItem

    @property
    def has_stats(self) -> bool:
        return bool(self.stats)


@dataclass
class Ingredient:
    """A single ``<ingredient>`` node of a multisell item (order preserved)."""

    id: int
    count: int

    @property
    def is_currency(self) -> bool:
        return self.id == ADENA_ID


@dataclass
class Production:
    """A single ``<production>`` node of a multisell item."""

    id: int
    count: int


@dataclass
class MultisellPosition:
    """A single ``<item>`` block of a multisell."""

    productions: List[Production]
    ingredients: List[Ingredient]

    @property
    def production_id(self) -> int:
        """Primary production id (first production of the position)."""
        return self.productions[0].id if self.productions else -1

    @property
    def production_count(self) -> int:
        return self.productions[0].count if self.productions else 0

    @property
    def adena_ingredient_index(self) -> Optional[int]:
        """Index of the adena (id=57) ingredient, or None if absent."""
        for i, ing in enumerate(self.ingredients):
            if ing.id == ADENA_ID:
                return i
        return None

    @property
    def non_currency_ingredients(self) -> List[Ingredient]:
        return [ing for ing in self.ingredients if not ing.is_currency]


@dataclass
class Multisell:
    """A loaded multisell file, preserving node order."""

    multisell_id: int
    path: str
    positions: List[MultisellPosition]
    is_custom: bool = False


@dataclass
class PriceReportEntry:
    """Per-category (per-multisell) reporting record (Requirement 11)."""

    multisell_id: int
    total: int = 0
    covered: int = 0
    uncovered: int = 0
    min_price: Optional[int] = None
    max_price: Optional[int] = None
    applied_overrides: List[dict] = field(default_factory=list)
    anti_exploit_violations: List[dict] = field(default_factory=list)
    unused_overrides: List[dict] = field(default_factory=list)
    unresolved_ids: List[int] = field(default_factory=list)
    unread_files: List[str] = field(default_factory=list)
    notes: List[str] = field(default_factory=list)
