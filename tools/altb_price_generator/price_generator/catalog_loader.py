"""CatalogLoader — parses stats/items/*.xml into an id -> CatalogItem index.

Extracts grade (crystal_type), NPC price and the ``<stats>`` block used later
for Effective_Stat_Value. Handles missing/zero price (anti-exploit fallback 7.4)
and missing stats (fallback 4.5).

Requirements: 8.5, 12.3, 4.5, 7.4.
"""

import glob
import os
import xml.etree.ElementTree as ET

from .constants import items_dir
from .models import CatalogItem


def _to_number(text):
    try:
        if "." in text:
            return float(text)
        return int(text)
    except (TypeError, ValueError):
        return None


def parse_item_element(elem):
    """Build a CatalogItem from a ``<item>`` XML element."""
    item_id = int(elem.get("id"))
    name = elem.get("name", "")
    item_type = elem.get("type", "")
    grade = "NG"
    price = None
    stats = {}

    for s in elem.findall("set"):
        n = s.get("name")
        if n == "crystal_type":
            grade = s.get("val", "NG")
        elif n == "price":
            price = _to_number(s.get("val"))

    stats_el = elem.find("stats")
    if stats_el is not None:
        for st in stats_el.findall("stat"):
            stype = st.get("type")
            val = _to_number((st.text or "").strip())
            if stype is not None and val is not None:
                stats[stype] = float(val)

    return CatalogItem(
        id=item_id,
        grade=grade or "NG",
        price=price,
        stats=stats,
        name=name,
        item_type=item_type,
    )


class CatalogLoader:
    """Loads and indexes the Item_Catalog."""

    def __init__(self, directory=None):
        self.directory = directory or items_dir()
        self.items = {}  # id -> CatalogItem

    def load(self):
        pattern = os.path.join(self.directory, "*.xml")
        for path in sorted(glob.glob(pattern)):
            self._load_file(path)
        return self.items

    def _load_file(self, path):
        try:
            tree = ET.parse(path)
        except ET.ParseError:
            return
        root = tree.getroot()
        for elem in root.findall("item"):
            try:
                ci = parse_item_element(elem)
            except (TypeError, ValueError):
                continue
            self.items[ci.id] = ci

    # convenience accessors
    def get(self, item_id):
        return self.items.get(item_id)

    def grade_of(self, item_id):
        ci = self.items.get(item_id)
        return ci.grade if ci else None

    def price_of(self, item_id):
        ci = self.items.get(item_id)
        return ci.price if ci else None
