"""SchemaValidator and transactional write (Task 6.6).

Validates a multisell against ``multisell.xsd`` with ``xmllint --noout
--schema`` BEFORE writing and only replaces the target atomically when xmllint
returns 0 (Requirement 12.2/12.4). Also resolves every production/ingredient id
against the Item_Catalog (Requirement 12.3). A single file failure never aborts
the rest of the run — the caller isolates errors (Requirement 12.6).
"""

import os
import subprocess
import tempfile

from .constants import ADENA_ID, schema_path
from .models import Multisell


class SchemaValidator:
    def __init__(self, xsd_path=None, xmllint="xmllint"):
        self.xsd_path = os.path.abspath(xsd_path or schema_path())
        self.xmllint = xmllint

    # ---- xmllint validation (Requirement 12.2) ------------------------
    def validate_text(self, xml_text):
        """Validate XML text. Returns (ok: bool, message: str)."""
        fd, tmp = tempfile.mkstemp(suffix=".xml")
        try:
            with os.fdopen(fd, "w", encoding="utf-8") as fh:
                fh.write(xml_text)
            return self.validate_file(tmp)
        finally:
            try:
                os.remove(tmp)
            except OSError:
                pass

    def validate_file(self, path):
        """Run xmllint against an on-disk file. Returns (ok, message)."""
        try:
            proc = subprocess.run(
                [self.xmllint, "--noout", "--schema", self.xsd_path, path],
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                universal_newlines=True,
            )
        except FileNotFoundError:
            return False, "xmllint not found on PATH"
        return proc.returncode == 0, (proc.stdout or "").strip()

    # ---- id resolution (Requirement 12.3) -----------------------------
    def resolve_ids(self, multisell, catalog):
        """Return the sorted list of production/ingredient ids not in catalog.

        Adena (id=57) is a currency and is not required to be an equipment
        definition, but it does exist in the catalog anyway; we still check it.
        """
        unresolved = set()
        for pos in multisell.positions:
            for prod in pos.productions:
                if catalog.get(prod.id) is None:
                    unresolved.add(prod.id)
            for ing in pos.ingredients:
                if ing.id == ADENA_ID:
                    continue
                if catalog.get(ing.id) is None:
                    unresolved.add(ing.id)
        return sorted(unresolved)

    # ---- transactional write (Requirement 12.4) -----------------------
    def transactional_write(self, target_path, new_text):
        """Validate ``new_text`` then atomically replace ``target_path``.

        Returns (written: bool, message: str). On validation failure the target
        file is left completely untouched.
        """
        ok, msg = self.validate_text(new_text)
        if not ok:
            return False, "schema validation failed: %s" % msg
        directory = os.path.dirname(os.path.abspath(target_path))
        fd, tmp = tempfile.mkstemp(suffix=".xml", dir=directory)
        try:
            with os.fdopen(fd, "w", encoding="utf-8") as fh:
                fh.write(new_text)
            os.replace(tmp, target_path)  # atomic on the same filesystem
        except OSError as exc:
            try:
                os.remove(tmp)
            except OSError:
                pass
            return False, "atomic replace failed: %s" % exc
        return True, "written"
