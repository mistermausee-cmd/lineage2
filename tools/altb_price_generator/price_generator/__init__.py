"""Price_Generator — offline, XML-only balance pass for the Alt+B shop.

Deterministic tool that reads the Item_Catalog (stats/items/*.xml) and the shop
multisells (data/multisell/*.xml + custom/), applies a reproducible Price_Model
and rewrites ONLY the ``count`` attribute of ``ingredient id="57"`` (adena),
preserving structure and assortment. No jar recompilation required.
"""

__version__ = "0.1.0"
