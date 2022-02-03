package work.lilley.felicity.tri_peaks;

import java.util.List;
import java.util.stream.Collectors;

class TriPeaksSolverApp {
  private static final int ATTEMPT_LIMIT = 100;
  public static void main(String[] args) {
    System.out.println("Welcome to the Tri Peaks Solver\n");
    boolean playSingle = false;

    List<String> stockInputs = List.of("3A2889TTQ4KQ54864T3AJ4KA", "8J9QK8A636K497Q537TJ63K9");
    List<String> boardInputs = List.of("758 7Q3JQ9 KAT693772 6K592652JJ", "472 TJ4AT3 Q5Q685AT4 22J87A2K59");
    int gameToPlay = 0;

    try {      
      Board board = Board.from(stockInputs.get(gameToPlay), boardInputs.get(gameToPlay));
      Solver smartSolver = new SmartSelectionSolver(board);
      if(playSingle) {
        smartSolver.play(true);
      } else {
        playUntilWon(board, List.of(smartSolver, new RandomSelectionSolver(board)));
      }
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      throw e;
     // return;
    }
  }

  private static void playUntilWon(Board board, List<Solver> solvers) {
    for(Solver solver : solvers) {
      System.out.println(String.format("Using %s", solver.getClass().getSimpleName()));
      System.out.println(String.format("Solver took %d attemps and %s\n", solver.playUntilWon(ATTEMPT_LIMIT), solver.getResult()));
      if (solver.gameWon()) {
        String formattedMoves = solver.getMoves().stream()
          .map(Move::toString)
          .collect(Collectors.joining("\n -> "));
        System.out.println(String.format("The last moves were:\nSTART\n -> %s\n", formattedMoves));
        board.reset();
        System.out.println("Visually:");
        solver.replay(true);
        return;
      }
    }
  }
}