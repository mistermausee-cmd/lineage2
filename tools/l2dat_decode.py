#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Decoder for Lineage2Ver413 .dat client files (RSA + zlib).
Ported from the PHP decoder by Hint aka Ilya.
Usage: python3 l2dat_decode.py <input.dat> <output.txt>
Output is written as UTF-16LE decoded to UTF-8 text.
"""
import sys
import base64
import zlib
import struct

# RSA modulus + exponent for Ver413. Two known key pairs:
#  - "original" NCSoft key (exp 0x35)
#  - "custom"/alt key used by some servers (exp 0x1d)
_MOD_ORIG = ("l985hHLd9zfvCgzRfo0XLw/vFmGjiorh1ugpvBxuTDz8GSkt2p75AXXkbnOUoYhQ"
             "tkF9A75u6idNPtHd5bXXvecswKC3HQNghlVjOIF5OgLJpn2e8rRet8CNS+MpCDz"
             "kUOaPeGe2dJMU1AUR0JvFdEVRuqhqidw4Ej3BZo/XLYM=")
_MOD_ALT = ("dbTW3lwBZUQGihrPElhp9D0uCfxVuLHiiVVtr5uHV2NVk0RiiLNlPaHOkch7saXB"
            "jxYyNJXFXX1ywIkKg/ab/R/ZQ06xwC8+Rnnt+kMwkxkHASnCZ8hWBNh7tluuIF3"
            "jcHrx0hCIgau1Z8Oz0GmuZ8OkxqOqk9JkE9TGYJSuIDk=")

_MODE = __import__("os").environ.get("L2KEY", "orig")
if _MODE == "alt":
    MODULUS = int.from_bytes(base64.b64decode(_MOD_ALT), "big")
    EXP = 0x1d
else:
    MODULUS = int.from_bytes(base64.b64decode(_MOD_ORIG), "big")
    EXP = 0x35


def decode(path: str) -> bytes:
    with open(path, "rb") as f:
        raw = f.read()
    if len(raw) < 28 + 128:
        raise ValueError("file too small")
    head = raw[:28].decode("utf-16-le")
    if head != "Lineage2Ver413":
        raise ValueError("bad header: %r" % head)

    blocks = (len(raw) - 28) // 128
    body = raw[28:28 + blocks * 128]

    out = bytearray()
    for i in range(blocks):
        block = body[i * 128:(i + 1) * 128]
        val = int.from_bytes(block, "big")
        res = pow(val, EXP, MODULUS)
        hexs = format(res, "x").zfill(250)
        if len(hexs) != 250:
            raise ValueError("block %d hex len %d" % (i, len(hexs)))
        s = bytes.fromhex(hexs)  # 125 bytes
        size = s[0]
        if size > len(s) - 1:
            raise ValueError("block %d size %d" % (i, size))
        if size != 0x7c:
            p = len(s) - size
            while p > 2 and s[p - 1] != 0:
                p -= 1
            s = s[p:p + size]
        else:
            s = s[-size:]
        out += s

    uncompressed_size = struct.unpack("<I", out[:4])[0]
    data = bytes(out[4:])
    result = zlib.decompress(data)
    if len(result) != uncompressed_size:
        raise ValueError("size mismatch %d != %d" % (len(result), uncompressed_size))
    return result


if __name__ == "__main__":
    inp = sys.argv[1]
    outp = sys.argv[2] if len(sys.argv) > 2 else None
    data = decode(inp)
    # L2 dat text content is UTF-16LE
    try:
        text = data.decode("utf-16-le")
    except UnicodeDecodeError:
        text = data.decode("utf-16-le", errors="replace")
    if outp:
        with open(outp, "w", encoding="utf-8") as f:
            f.write(text)
        print("wrote", outp, len(text), "chars")
    else:
        sys.stdout.write(text[:2000])
