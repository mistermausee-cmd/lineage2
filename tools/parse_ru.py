#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Parse decrypted L2 client RU .bin files into id->name / name lists.
Handles NpcName, ZoneName, HuntingZone binary structures.
"""
import sys
import struct
import json


def read_l2str(data, pos):
    """Read one L2 client string: 1-byte length (low7 = code units incl null),
    then that many UTF-16LE code units. Returns (text_without_null, newpos)."""
    L = data[pos]
    pos += 1
    n = L & 0x7f
    if n == 0:
        return "", pos
    raw = data[pos:pos + n * 2]
    pos += n * 2
    s = raw.decode("utf-16-le", errors="replace")
    return s.rstrip("\x00"), pos


CONST = b"\x9c\xe8\xa9\xff"


def parse_npcname(data):
    """Header 4 bytes, then records: id u32, name str, desc str, const 4b."""
    pos = 4
    out = {}
    n = len(data)
    while pos + 4 <= n:
        npc_id = struct.unpack_from("<I", data, pos)[0]
        pos += 4
        end = data.find(CONST, pos)
        if end == -1:
            break
        name, _ = read_l2str(data, pos)  # name = first string after id
        out[npc_id] = {"name": name}
        pos = end + 4
    return out


def dump_strings(data):
    """Extract all readable cyrillic/latin UTF-16 runs for inspection."""
    res = []
    i = 0
    n = len(data)
    cur = []
    while i + 1 < n:
        ch = data[i] | (data[i + 1] << 8)
        i += 2
        if 0x0400 <= ch <= 0x04FF or 0x20 <= ch < 0x7f:
            cur.append(chr(ch))
        else:
            if len(cur) >= 2:
                res.append("".join(cur))
            cur = []
    if len(cur) >= 2:
        res.append("".join(cur))
    return res


if __name__ == "__main__":
    mode = sys.argv[1]
    path = sys.argv[2]
    data = open(path, "rb").read()
    if mode == "npc":
        recs = parse_npcname(data)
        outp = sys.argv[3] if len(sys.argv) > 3 else None
        if outp:
            json.dump(recs, open(outp, "w", encoding="utf-8"),
                      ensure_ascii=False, indent=0)
            print("parsed", len(recs), "npcs ->", outp)
        # print some known boss ids
        for bid in (29068, 29028, 29020, 29001, 29014, 29006, 29022,
                    29045, 29118, 29196, 29240, 20001, 20002):
            if bid in recs:
                print(bid, recs[bid]["name"])
    elif mode == "strings":
        for s in dump_strings(data):
            print(s)
