"""ChainResolver — builds and topologically sorts Upgrade_Chain lines.

A chain link relation: the ``production`` of link N-1 is the non-currency
(id != 57) ``ingredient`` of link N. Provides topological ordering, endpoint
(final link) detection and cycle/unresolvability detection.

Requirements: 6.1, 6.4, 6.5.
"""

from .constants import ADENA_ID


class ChainError(Exception):
    """Raised when a chain line is unresolvable (e.g. contains a cycle)."""


class ChainResolver:
    """Resolves Upgrade_Chain lines within a single multisell.

    Each position is identified by its primary production id. A directed edge
    base_item -> production is added when a position consumes a single
    non-currency ingredient (the base item) to produce the production item.
    """

    def __init__(self, positions):
        self.positions = list(positions)
        # production_id -> position
        self.by_production = {}
        for pos in self.positions:
            if pos.productions:
                self.by_production[pos.production_id] = pos
        # edges: base_item_id -> set(production_id) built from chain positions
        self.successors = {}
        self.predecessor = {}  # production_id -> base_item_id (its chain parent)
        self._build_edges()

    def _build_edges(self):
        for pos in self.positions:
            non_currency = [i for i in pos.ingredients if i.id != ADENA_ID]
            # A chain link consumes exactly one non-currency base item.
            if len(non_currency) == 1 and pos.productions:
                base = non_currency[0].id
                prod = pos.production_id
                self.successors.setdefault(base, set()).add(prod)
                # Record parent only if base is itself produced somewhere (a real
                # chain), otherwise it is still a link with an external base.
                self.predecessor[prod] = base

    # ---- queries -------------------------------------------------------
    def is_chain_link(self, production_id):
        return production_id in self.predecessor

    def is_endpoint(self, production_id):
        """A production that is not consumed as a base by any other position."""
        succ = self.successors.get(production_id)
        return not succ

    def chain_depth(self, production_id):
        """0-based depth of a production within its chain line.

        Depth = number of chain ancestors reachable via the predecessor links
        that are themselves produced in this multisell. Raises ChainError on a
        cycle.
        """
        depth = 0
        seen = set()
        current = production_id
        while current in self.predecessor:
            if current in seen:
                raise ChainError(
                    "cycle detected in upgrade chain at production %r" % current
                )
            seen.add(current)
            base = self.predecessor[current]
            # Only follow the link if the base is itself produced here (in-chain).
            if base in self.by_production and base != current:
                depth += 1
                current = base
            else:
                break
        return depth

    def topological_order(self):
        """Return production ids ordered so that bases precede their upgrades.

        Raises ChainError if a cycle is present among in-multisell productions.
        """
        # Build graph limited to productions present in this multisell.
        nodes = set(self.by_production.keys())
        indeg = {n: 0 for n in nodes}
        adj = {n: [] for n in nodes}
        for base, prods in self.successors.items():
            if base not in nodes:
                continue
            for p in prods:
                if p in nodes and p != base:
                    adj[base].append(p)
                    indeg[p] += 1
        # Kahn's algorithm.
        queue = sorted([n for n in nodes if indeg[n] == 0])
        order = []
        while queue:
            n = queue.pop(0)
            order.append(n)
            for m in sorted(adj[n]):
                indeg[m] -= 1
                if indeg[m] == 0:
                    queue.append(m)
            queue.sort()
        if len(order) != len(nodes):
            raise ChainError("cycle detected: upgrade chain is not a DAG")
        return order

    def has_cycle(self):
        try:
            self.topological_order()
            return False
        except ChainError:
            return True

    def endpoints(self):
        """All chain-link productions that are endpoints (final links)."""
        return [
            p for p in self.predecessor if self.is_endpoint(p)
        ]
