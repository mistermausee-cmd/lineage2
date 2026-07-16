"""MultisellWriter — writes new Buy_Price values while preserving structure.

The writer operates on the *raw file text* so that comments, whitespace,
attribute order and the ``<npcs>`` block are preserved byte-for-byte. The only
thing it changes is the numeric ``count`` value of the currency ``ingredient
id="57"`` of each position (Requirement 10.1, 13.1).

It also:
  - routes writes for the Custom_Override departments {600008, 600011, 600025,
    600026} to ``data/multisell/custom/`` only, never touching the root file
    (Requirement 9.1/9.2); a missing custom file means "uncovered" and no write
    to the root (Requirement 9.5);
  - knows the schema-location string expected by file location
    (``../../xsd/multisell.xsd`` for custom/, ``../xsd/multisell.xsd`` for root)
    (Requirement 9.3);
  - verifies the produced text against the original structure and refuses to
    apply an output that would change anything but the adena count
    (Requirement 10.6/13.6).

Requirements: 9.1-9.5, 10.1-10.6, 13.1, 13.5, 13.6.
"""

import os
import re
import xml.etree.ElementTree as ET

from .constants import (
    ADENA_ID,
    CUSTOM_OVERRIDE_DEPARTMENTS,
    SCHEMA_LOCATION_CUSTOM,
    SCHEMA_LOCATION_ROOT,
)


class WriterError(Exception):
    """Raised when a write would violate the structure-preservation contract."""


_ITEM_RE = re.compile(r"<item>.*?</item>", re.DOTALL)
_INGREDIENT_TAG_RE = re.compile(r"<ingredient\b[^>]*/>")
_COUNT_ATTR_RE = re.compile(r'\bcount="\d+"')
_ID57_RE = re.compile(r'\bid="57"')


def expected_schema_location(is_custom):
    """Schema-location string that must appear in a file by its location."""
    return SCHEMA_LOCATION_CUSTOM if is_custom else SCHEMA_LOCATION_ROOT


def resolve_target_path(multisell_id, root_dir, custom_dir):
    """Resolve the physical file to write for a department id.

    - For Custom_Override departments {600008, 600011, 600025, 600026} the
      write goes to ``custom/`` only. If the custom file is missing, returns
      None (the caller records the department as uncovered and does NOT write
      the root file — Requirement 9.5).
    - For every other department the root file is used.
    """
    fname = "%d.xml" % multisell_id
    if multisell_id in CUSTOM_OVERRIDE_DEPARTMENTS:
        cpath = os.path.join(custom_dir, fname)
        return cpath if os.path.exists(cpath) else None
    return os.path.join(root_dir, fname)


class MultisellWriter:
    """Produces new file text changing only the adena ingredient count."""

    def _set_adena_count(self, item_block, new_count):
        """Replace the count of the id=57 ingredient inside one ``<item>``."""
        replaced = {"done": False}

        def repl(m):
            tag = m.group(0)
            if not _ID57_RE.search(tag):
                return tag
            replaced["done"] = True
            return _COUNT_ATTR_RE.sub('count="%d"' % int(new_count), tag, count=1)

        new_block = _INGREDIENT_TAG_RE.sub(repl, item_block)
        return new_block, replaced["done"]

    def build_text(self, original_text, prices):
        """Return new file text applying ``prices`` (one per ``<item>`` in order).

        ``prices[i]`` may be None to leave position *i* unchanged. Raises
        WriterError if the number of prices does not match the number of items.
        """
        matches = list(_ITEM_RE.finditer(original_text))
        if len(matches) != len(prices):
            raise WriterError(
                "price count %d != item count %d" % (len(prices), len(matches))
            )
        out = []
        last = 0
        for m, price in zip(matches, prices):
            out.append(original_text[last:m.start()])
            block = m.group(0)
            if price is not None:
                block, done = self._set_adena_count(block, price)
                if not done:
                    # No adena ingredient in this position — cannot price it.
                    raise WriterError(
                        "position has no id=57 ingredient to set a price on"
                    )
            out.append(block)
            last = m.end()
        out.append(original_text[last:])
        return "".join(out)

    # ---- structure verification (Requirement 10.6 / 13.6) --------------
    @staticmethod
    def _structure_signature(text):
        """Return a comparable signature of everything that must be preserved."""
        root = ET.fromstring(text)
        npcs = root.find("npcs")
        npc_vals = (
            [n.text for n in npcs.findall("npc")] if npcs is not None else None
        )
        items = []
        for item in root.findall("item"):
            prods = [
                (int(p.get("id")), int(p.get("count")))
                for p in item.findall("production")
            ]
            # Non-adena ingredients keep their id+count; adena keeps only its id.
            ings = []
            for ing in item.findall("ingredient"):
                iid = int(ing.get("id"))
                if iid == ADENA_ID:
                    ings.append((iid, None))
                else:
                    ings.append((iid, int(ing.get("count"))))
            items.append((tuple(prods), tuple(ings)))
        return (npc_vals, tuple(items))

    def verify_structure(self, original_text, new_text):
        """Return a list of violation strings; empty means the write is safe."""
        violations = []
        try:
            before = self._structure_signature(original_text)
            after = self._structure_signature(new_text)
        except ET.ParseError as exc:
            return ["xml parse error: %s" % exc]
        if before[0] != after[0]:
            violations.append("<npcs> block changed")
        if len(before[1]) != len(after[1]):
            violations.append(
                "item count changed: %d -> %d" % (len(before[1]), len(after[1]))
            )
        else:
            for idx, (b, a) in enumerate(zip(before[1], after[1])):
                if b != a:
                    violations.append(
                        "position %d structure changed (production/ingredient "
                        "set/order/count)" % idx
                    )
        return violations
