"""Differentiation helpers (Task 4.6).

Maps concrete departmental roles to the three conceptual role classes
(consumable < key_item < prestige), classifies enchant scrolls/stones by kind
(common < blessed < sacred/ancient) and equipment target (weapon vs armor), and
derives the Category_Factor for enchant positions so that:

  - common < blessed < sacred/ancient for the same grade (Requirement 5.1);
  - weapon enchant >= armor enchant of the same kind/grade (Requirement 5.3).

Requirements: 1.5, 2.5, 2.6, 3.1-3.5, 5.1-5.5.
"""

# Concrete role -> conceptual role class.
ROLE_CLASS = {
    "consumable": "consumable",
    "armor": "key_item",
    "jewelry": "key_item",
    "weapon": "key_item",
    "accessory": "key_item",
    "enchant": "key_item",
    "prestige": "prestige",
}

# Strict order of the conceptual role classes (Requirement 2.5, 2.6).
ROLE_CLASS_ORDER = ["consumable", "key_item", "prestige"]


def role_class(role):
    return ROLE_CLASS.get(role, "key_item")


def role_class_rank(role):
    return ROLE_CLASS_ORDER.index(role_class(role))


# Enchant scroll/stone kinds in strict price order.
ENCHANT_COMMON = "common"
ENCHANT_BLESSED = "blessed"
ENCHANT_SACRED = "sacred"
ENCHANT_ORDER = [ENCHANT_COMMON, ENCHANT_BLESSED, ENCHANT_SACRED]

# Keyword sets (English + Russian) for scroll/stone classification.
_BLESSED_KW = ("blessed", "благослов")
_SACRED_KW = ("sacred", "ancient", "священ", "древн")
_WEAPON_KW = ("weapon", "оруж")
_ARMOR_KW = ("armor", "armour", "брон", "доспех")


def _has_any(text, keywords):
    t = text.lower()
    return any(k in t for k in keywords)


def classify_enchant_kind(name):
    """Classify an enchant scroll/stone by its item name."""
    if _has_any(name, _SACRED_KW):
        return ENCHANT_SACRED
    if _has_any(name, _BLESSED_KW):
        return ENCHANT_BLESSED
    return ENCHANT_COMMON


def classify_enchant_target(name):
    """Return 'weapon', 'armor' or 'other' for an enchant scroll/stone name."""
    if _has_any(name, _WEAPON_KW):
        return "weapon"
    if _has_any(name, _ARMOR_KW):
        return "armor"
    return "other"


def enchant_category_factor(model, name):
    """Category_Factor for an enchant position based on kind and target.

    common < blessed < sacred, and weapon variant is scaled up so it is never
    cheaper than the armor variant of the same kind/grade (Requirement 5.3).
    """
    kind = classify_enchant_kind(name)
    cf = model.category_factor.get("enchant_%s" % kind, 1.0)
    if classify_enchant_target(name) == "weapon":
        cf *= model.category_factor.get("enchant_weapon", 1.0)
    return cf


def enchant_kind_rank(name):
    return ENCHANT_ORDER.index(classify_enchant_kind(name))
