package work.lilley.felicity.tri_peaks;

import java.util.List;
import java.util.Random;

public class RandomSelectionSolver extends Solver {
  private final static double FLIP_THRESHOLD = 0.1;
  private final Random random = new Random();

  public RandomSelectionSolver(Board board) {
    super(board);
  }

  @Override
  boolean shouldDoOptionalFlip(List<Move> matchMoves) {
    return random.nextDouble() <= FLIP_THRESHOLD;
  }

  @Override
  Move getBestMatch(List<Move> matchMoves) {
    return matchMoves.get(random.nextInt(matchMoves.size()));
  }
}
