#!/usr/bin/env python3
import re, os, glob

BASE = "/projects/sandbox/lineage2/server/game/data/multisell"
CUSTOM = os.path.join(BASE, "custom")

# active multisell ids referenced in the shop
ACTIVE = [600008,600011,600025,600026,600028,600029,600030,600032,600033,600034,600035,
          600041,600043,600044,600045,600047,600048,600052,600053,600054,600055,600057,
          600060,600061,600062,600063,600064,600065,600066,600067,600068,600069,600070,600071,
          600090,600091,600100,600101,600102,600103,600104,600105,600106,600107,600108,600109,
          600110,600111,600112,600113]

def find_file(mid):
    # custom overrides main for 600001-600029
    c = os.path.join(CUSTOM, f"{mid}.xml")
    m = os.path.join(BASE, f"{mid}.xml")
    if os.path.exists(c):
        return c, "custom"
    if os.path.exists(m):
        return m, "main"
    return None, None

item_re = re.compile(r"<item\b(.*?)</item>", re.S)
ing_re = re.compile(r'<ingredient\s+id="(\d+)"\s+count="(\d+)"')
prod_re = re.compile(r'<production\s+id="(\d+)"\s+count="(\d+)"\s*/>\s*(?:<!--\s*(.*?)\s*-->)?')

for mid in ACTIVE:
    path, kind = find_file(mid)
    if not path:
        print(f"### {mid}: FILE NOT FOUND")
        continue
    with open(path, encoding="utf-8") as f:
        data = f.read()
    items = item_re.findall(data)
    print(f"\n### {mid} ({kind})  [{len(items)} позиций]")
    for it in items:
        ings = ing_re.findall(it)
        prods = prod_re.findall(it)
        adena = 0
        other_ing = []
        for iid, cnt in ings:
            if iid == "57":
                adena = int(cnt)
            else:
                other_ing.append(f"{iid}x{cnt}")
        for pid, pcnt, name in prods:
            name = (name or "").strip()
            extra = f"  <= {'+'.join(other_ing)}" if other_ing else ""
            print(f"  {adena:>15,}  ->  {pid} x{pcnt}  {name}{extra}")
