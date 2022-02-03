package work.lilley.felicity.tri_peaks;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomSelectionSolver extends Solver {
  private final static double FLIP_THRESHOLD = 0.1;
  private final Random random = new Random();

  public RandomSelectionSolver(Board board) {
    super(board);
  }

  @Override
  public Move calculateNextMove(Collection<Move> potentialMoves) {
    Optional<Move> flip = potentialMoves.stream()
      .filter(move -> move.getType() == Move.Type.FLIP)
      .findFirst();

    if(flip.isPresent() && (potentialMoves.size() == 1 || random.nextDouble() <= FLIP_THRESHOLD)) {
      return flip.get();
    }

    List<Move> nonFlipMoves = potentialMoves.stream()
      .filter(move -> move.getType() != Move.Type.FLIP)
      .toList();

    if (nonFlipMoves.isEmpty()) {
      throw new RuntimeException("Ran out of moves unexpectedly");
    }
    
    return nonFlipMoves.get(random.nextInt(nonFlipMoves.size()));
  }
}
