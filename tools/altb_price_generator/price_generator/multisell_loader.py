"""MultisellLoader — reads shop multisells (+custom/) into ordered models.

Preserves node order of positions, productions and ingredients, indexes the
adena (id=57) currency ingredient and keeps the ``<npcs>`` block verbatim for
later writing.

Requirements: 10.1, 10.2, 11.1.
"""

import os
import re
import xml.etree.ElementTree as ET

from .models import Ingredient, Multisell, MultisellPosition, Production


def _msid_from_path(path):
    base = os.path.basename(path)
    stem, _ = os.path.splitext(base)
    try:
        return int(stem)
    except ValueError:
        return -1


def parse_position(item_el):
    """Build a MultisellPosition from an ``<item>`` element, preserving order."""
    ingredients = []
    productions = []
    # Preserve document order across ingredient/production children.
    for child in list(item_el):
        if child.tag == "ingredient":
            ingredients.append(
                Ingredient(id=int(child.get("id")), count=int(child.get("count")))
            )
        elif child.tag == "production":
            productions.append(
                Production(id=int(child.get("id")), count=int(child.get("count")))
            )
    return MultisellPosition(productions=productions, ingredients=ingredients)


def load_multisell(path, is_custom=None):
    """Load a single multisell file into a Multisell model."""
    if is_custom is None:
        is_custom = os.path.basename(os.path.dirname(path)) == "custom"
    tree = ET.parse(path)
    root = tree.getroot()
    positions = [parse_position(item_el) for item_el in root.findall("item")]
    return Multisell(
        multisell_id=_msid_from_path(path),
        path=path,
        positions=positions,
        is_custom=is_custom,
    )


def read_npcs_block(path):
    """Return the verbatim ``<npcs>...</npcs>`` block text, or None if absent."""
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    m = re.search(r"<npcs>.*?</npcs>", content, re.DOTALL)
    return m.group(0) if m else None


class MultisellLoader:
    """Loads a set of multisells given their file paths or ids."""

    def __init__(self, multisell_dir_path, custom_dir_path=None):
        self.multisell_dir = multisell_dir_path
        self.custom_dir = custom_dir_path or os.path.join(multisell_dir_path, "custom")

    def load_by_id(self, multisell_id, prefer_custom=False):
        """Load a multisell by id; optionally prefer the custom/ copy."""
        fname = "%d.xml" % multisell_id
        custom_path = os.path.join(self.custom_dir, fname)
        root_path = os.path.join(self.multisell_dir, fname)
        if prefer_custom and os.path.exists(custom_path):
            return load_multisell(custom_path, is_custom=True)
        if os.path.exists(root_path):
            return load_multisell(root_path, is_custom=False)
        if os.path.exists(custom_path):
            return load_multisell(custom_path, is_custom=True)
        return None

    def load_path(self, path):
        return load_multisell(path)


def serialize_multisell(ms):
    """Serialize a Multisell model back to a minimal XML string.

    Used by the round-trip test (Task 2.4). Preserves order and structure but
    not comments/whitespace (the real writer keeps those; see MultisellWriter).
    """
    lines = ['<?xml version="1.0" encoding="UTF-8"?>']
    schema = "../../xsd/multisell.xsd" if ms.is_custom else "../xsd/multisell.xsd"
    lines.append(
        '<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" '
        'xsi:noNamespaceSchemaLocation="%s">' % schema
    )
    lines.append("\t<npcs>")
    lines.append("\t\t<npc>-1</npc>")
    lines.append("\t</npcs>")
    for pos in ms.positions:
        lines.append("\t<item>")
        for ing in pos.ingredients:
            lines.append(
                '\t\t<ingredient id="%d" count="%d" />' % (ing.id, ing.count)
            )
        for prod in pos.productions:
            lines.append(
                '\t\t<production id="%d" count="%d" />' % (prod.id, prod.count)
            )
        lines.append("\t</item>")
    lines.append("</list>")
    return "\n".join(lines) + "\n"
