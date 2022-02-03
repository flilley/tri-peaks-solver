package work.lilley.felicity.tri_peaks;

import java.util.List;
import java.util.stream.Collectors;

class TriPeaksSolverApp {
  public static void main(String[] args) {
    System.out.println("Welcome to the Tri Peaks Solver\n");

    Board board1 = Board.from("8J9QK8A636K497Q537TJ63K9", "472 TJ4AT3 Q5Q685AT4 22J87A2K59");
    Board board2 = Board.from("3A2889TTQ4KQ54864T3AJ4KA", "758 7Q3JQ9 KAT693772 6K592652JJ");
    
    for (Board board : List.of(board1, board2)) {
      try {
        Solver randomSelectionSolver = new RandomSelectionSolver(board);
        System.out.println(String.format("Game took %d attemps to win\n", randomSelectionSolver.playUntilWon()));
        String formattedMoves = randomSelectionSolver.getMoves().stream()
          .map(Move::toString)
          .collect(Collectors.joining("\n -> "));
        System.out.println(String.format("The winning moves were:\nSTART\n -> %s\n", formattedMoves));
        board.reset();
        System.out.println("Visually:");
        randomSelectionSolver.replay(true);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
      }
    }
  }
}