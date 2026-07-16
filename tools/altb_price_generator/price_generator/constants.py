"""Shared constants for the Price_Generator tool.

Paths are resolved relative to the repository root so the tool can run from
anywhere. The tool lives in ``tools/altb_price_generator/`` (outside game data);
game data lives under ``server/game/data/``.
"""

import os

# Adena is the single currency ingredient of the Alt+B shop.
ADENA_ID = 57

# Grade order used everywhere for monotonicity checks (Requirement 1.3, 1.5).
GRADE_ORDER = ["NG", "D", "C", "B", "A", "S", "S80", "R", "R95", "R99"]

# Progression stages that carry a Farm_Rate (Requirement 1.1).
PROGRESSION_STAGES = ["B", "A", "S", "S80", "R", "R95", "R99", "epic"]

# Roles (Requirement 2.1).
ROLE_CONSUMABLE = "consumable"
ROLE_KEY_ITEM = "key_item"
ROLE_PRESTIGE = "prestige"
ROLES = [ROLE_CONSUMABLE, ROLE_KEY_ITEM, ROLE_PRESTIGE]

# Departments that exist both in data/multisell/ and data/multisell/custom/;
# the custom/ copy overrides the root one, so writes go to custom/ only
# (Requirement 9.1/9.2). Custom_Override set.
CUSTOM_OVERRIDE_DEPARTMENTS = {600008, 600011, 600025, 600026}


def repo_root():
    """Absolute path to the lineage2 repository root."""
    # constants.py -> price_generator -> altb_price_generator -> tools -> <repo>
    here = os.path.abspath(__file__)
    return os.path.normpath(os.path.join(here, "..", "..", "..", ".."))


def game_data_dir():
    return os.path.join(repo_root(), "server", "game", "data")


def multisell_dir():
    return os.path.join(game_data_dir(), "multisell")


def custom_multisell_dir():
    return os.path.join(multisell_dir(), "custom")


def items_dir():
    return os.path.join(game_data_dir(), "stats", "items")


def schema_path():
    """Absolute path to the multisell XSD schema."""
    return os.path.join(game_data_dir(), "xsd", "multisell.xsd")


# Schema location string stored inside each written file, by file location
# (Requirement 9.3).
SCHEMA_LOCATION_ROOT = "../xsd/multisell.xsd"
SCHEMA_LOCATION_CUSTOM = "../../xsd/multisell.xsd"
