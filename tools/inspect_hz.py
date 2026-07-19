#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Inspect HuntingZone.dat structure: locate each name string, then dump the
int32/float fields that follow it, to reverse-engineer the record layout."""
import sys
import struct

data = open(sys.argv[1], "rb").read()
n = len(data)


def try_str(pos):
    """If pos points at an L2 string prefix (0x80|len) + valid cyrillic UTF16,
    return (text, endpos). Else None."""
    L = data[pos]
    if L < 0x81 or L > 0xbf:
        return None
    nch = L & 0x7f
    end = pos + 1 + nch * 2
    if end > n:
        return None
    raw = data[pos + 1:end]
    try:
        s = raw.decode("utf-16-le")
    except Exception:
        return None
    if not s.endswith("\x00"):
        return None
    s = s[:-1]
    # must be mostly cyrillic/latin/space
    ok = sum(1 for c in s if (0x0400 <= ord(c) <= 0x4ff) or c in " -:'()." or ('0' <= c <= '9') or ('A' <= c <= 'z'))
    if len(s) == 0 or ok < len(s) * 0.7:
        return None
    return s, end


i = 4
count = 0
while i < n and count < 12:
    r = try_str(i)
    if r:
        name, end = r
        # dump 40 bytes after the string as ints and floats
        tail = data[end:end + 40]
        ints = [struct.unpack_from("<i", tail, k)[0] for k in range(0, 32, 4)]
        flts = [struct.unpack_from("<f", tail, k)[0] for k in range(0, 32, 4)]
        # also show preceding 8 bytes
        pre = data[max(0, i - 8):i]
        print("off=%d name=%r" % (i, name))
        print("   pre=%s" % pre.hex())
        print("   ints=%s" % ints)
        print("   flts=%s" % [round(f, 1) for f in flts])
        count += 1
        i = end
    else:
        i += 1
