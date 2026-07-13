#!/usr/bin/env python3
"""
Convert the L2Scripts reference GM-shop multisells into Mobius admin buylist
files, so the L2Scripts shop can be offered as a SECOND admin GM-shop next to
the native Mobius one, using the exact same native mechanism (admin_buy <id>).

Source (read from git, branch `alt-b-shop-overhaul`, NOT checked out):
    l2scripts/dist/gameserver/data/multisell/gmshop/1000001..1000020.xml
    (20 categories, all priced in Adena, ingredient id=57)

Target (new, additive):
    server/game/data/buylists/custom/1000001..1000020.xml
    -> opened in game via `admin_buy 1000001` .. `admin_buy 1000020`

Conversion rules:
  * A multisell entry  (N adena -> C units of item X)  becomes a buylist entry
    `<item id="X" price="P"/>` where P = ceil(N / C) is the per-unit Adena price.
  * Anti-exploit for Adena purchases: a player must never be able to buy an item
    here and re-sell it to a regular NPC for profit. NPC buy-back price is
    referencePrice/2, so the final price is clamped to at least (refPrice//2)+1
    for sellable items. L2Scripts prices are kept whenever they are already safe.
  * Duplicate production ids inside a category are de-duplicated (buylist ids
    must be unique); the first occurrence wins.
  * Every production item id is validated against server/game/data/stats/items.
"""
import math
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

REPO = "/projects/sandbox/lineage2"
GAME = os.path.join(REPO, "server", "game")
ITEMS_DIR = os.path.join(GAME, "data", "stats", "items")
OUT_DIR = os.path.join(GAME, "data", "buylists", "custom")
SRC_BRANCH = "alt-b-shop-overhaul"
SRC_TMPL = "l2scripts/dist/gameserver/data/multisell/gmshop/{id}.xml"
FIRST_ID = 1000001
LAST_ID = 1000020

CATEGORY_TITLES = {
    1000001: "Shots",
    1000002: "Blessed Spiritshots",
    1000003: "Consumables",
    1000004: "Forgotten Scrolls",
    1000005: "Accessory",
    1000006: "Weapons B-grade",
    1000007: "Weapons A-grade",
    1000008: "Weapons S-grade",
    1000009: "Weapons S80-grade",
    1000010: "Weapons SA (Special Ability)",
    1000011: "Armors B-grade",
    1000012: "Armors A-grade",
    1000013: "Armors S-grade",
    1000014: "Armors S80-grade",
    1000015: "Armors (Enchanted)",
    1000016: "Dyes",
    1000017: "Pets",
    1000018: "Accessories",
    1000019: "Armors R-grade",
    1000020: "Weapons R-grade",
}


def load_item_index():
    """Return {itemId: (referencePrice:int, isSellable:bool)} from stats/items."""
    index = {}
    for fn in sorted(os.listdir(ITEMS_DIR)):
        if not fn.endswith(".xml"):
            continue
        tree = ET.parse(os.path.join(ITEMS_DIR, fn))
        root = tree.getroot()
        for item in root.iter("item"):
            iid = item.get("id")
            if iid is None:
                continue
            iid = int(iid)
            price = 0
            sellable = True
            for s in item.findall("set"):
                name = s.get("name")
                if name == "price":
                    try:
                        price = int(s.get("val"))
                    except (TypeError, ValueError):
                        price = 0
                elif name == "is_sellable":
                    sellable = str(s.get("val")).lower() == "true"
            index[iid] = (price, sellable)
    return index


def read_source(list_id):
    path = SRC_TMPL.format(id=list_id)
    return subprocess.check_output(
        ["git", "show", "{}:{}".format(SRC_BRANCH, path)],
        cwd=REPO, text=True,
    )


# Matches, in document order, either an XML comment or a full <item>..</item>.
TOKEN_RE = re.compile(r"<!--(?P<comment>.*?)-->|<item\b(?P<item>.*?)</item>", re.DOTALL)
ING_RE = re.compile(r'<ingredient\b[^>]*\bid="(?P<id>\d+)"[^>]*\bcount="(?P<count>\d+)"')
PROD_RE = re.compile(r'<production\b[^>]*\bid="(?P<id>\d+)"[^>]*\bcount="(?P<count>\d+)"')
INNER_COMMENT_RE = re.compile(r"<!--(?P<comment>.*?)-->", re.DOTALL)


def clean(text):
    return " ".join(text.split()).strip()


def parse_source(xml_text):
    """Yield dicts: {name, ing_id, ing_count, prod_id, prod_count} in order."""
    entries = []
    pending_comment = None
    for m in TOKEN_RE.finditer(xml_text):
        if m.group("comment") is not None:
            pending_comment = clean(m.group("comment"))
            continue
        body = m.group("item")
        ing = ING_RE.search(body)
        prod = PROD_RE.search(body)
        if not ing or not prod:
            pending_comment = None
            continue
        name = pending_comment
        inner = INNER_COMMENT_RE.search(body)
        if inner:
            name = clean(inner.group("comment"))
        entries.append({
            "name": name,
            "ing_id": int(ing.group("id")),
            "ing_count": int(ing.group("count")),
            "prod_id": int(prod.group("id")),
            "prod_count": int(prod.group("count")),
        })
        pending_comment = None
    return entries


def xml_escape(text):
    return (text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))


def convert(list_id, item_index, problems):
    entries = parse_source(read_source(list_id))
    lines = []
    seen = set()
    title = CATEGORY_TITLES.get(list_id, str(list_id))
    for e in entries:
        pid = e["prod_id"]
        if e["ing_id"] != 57:
            problems.append("{}: non-adena ingredient {} for item {}".format(
                list_id, e["ing_id"], pid))
        if pid not in item_index:
            problems.append("{}: production item {} not found in stats/items".format(
                list_id, pid))
            continue
        if pid in seen:
            continue
        seen.add(pid)
        unit = int(math.ceil(e["ing_count"] / max(1, e["prod_count"])))
        if unit < 1:
            unit = 1
        ref_price, sellable = item_index[pid]
        floor = 0
        if sellable and ref_price > 0:
            floor = ref_price // 2 + 1
        price = max(unit, floor)
        comment = ""
        if e["name"]:
            comment = "  <!-- {} -->".format(xml_escape(e["name"]))
        lines.append('\t<item id="{}" price="{}" />{}'.format(pid, price, comment))
    header = (
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        "<!-- L2Scripts reference GM shop (second admin GM-shop) - {} -->\n"
        "<!-- Source: L2Scripts multisell/gmshop/{}.xml. Prices in Adena "
        "(anti-exploit floor: NPC sell-back + 1). -->\n"
        "<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        "xsi:noNamespaceSchemaLocation=\"../../xsd/buylist.xsd\">\n"
    ).format(title, list_id)
    body = "\n".join(lines)
    out = header + body + "\n</list>\n"
    # Match the repo convention for data files (CRLF line terminators).
    out = out.replace("\n", "\r\n")
    out_path = os.path.join(OUT_DIR, "{}.xml".format(list_id))
    with open(out_path, "w", encoding="utf-8", newline="") as fh:
        fh.write(out)
    return len(lines)


def main():
    item_index = load_item_index()
    print("Loaded {} items from stats/items".format(len(item_index)))
    problems = []
    total = 0
    for list_id in range(FIRST_ID, LAST_ID + 1):
        n = convert(list_id, item_index, problems)
        total += n
        print("  {}.xml  ({} entries)  {}".format(
            list_id, n, CATEGORY_TITLES.get(list_id, "")))
    print("Total buylist entries written: {}".format(total))
    if problems:
        print("\nPROBLEMS:")
        for p in problems:
            print("  - " + p)
        sys.exit(1)
    print("\nAll production item ids resolved OK.")


if __name__ == "__main__":
    main()
