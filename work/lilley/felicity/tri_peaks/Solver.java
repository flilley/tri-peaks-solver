package work.lilley.felicity.tri_peaks;

import java.util.Collection;

interface Solver {
  Move calculateNextMove(Collection<Move> potentialMoves);
}
