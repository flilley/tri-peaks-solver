package work.lilley.felicity.tri_peaks;

import java.util.Collection;

class TriPeaksSolverApp {
  private final Board board;
  private final Solver solver;

  private TriPeaksSolverApp(Board board, Solver solver) {
    this.board = board;
    this.solver = solver;
  } 

  private Board.GameState play(boolean showOutput) {
    if (showOutput) {
      board.print(true);
    }

    while(board.calculateGameState() == Board.GameState.PLAYING) {
      Collection<Move> potentialMoves = board.getPotentialMoves();
      Move selectedMove = solver.calculateNextMove(potentialMoves);
      board.play(selectedMove);
      if (showOutput) {
        board.print(false);
      }
    }
    return board.calculateGameState();
  }

  private int playUntilWon() {
    int attempts = 1;

    board.print(true);
    while(true) {
      Board.GameState result = play(false);
      System.out.println(String.format("Result of attempt %d is %s", attempts, result));
      if (result == Board.GameState.WON) {
        return attempts;
      }
      board.reset();
      attempts++;
    }
  }

  public static void main(String[] args) {
    System.out.println("Welcome to the Tri Peaks Solver");
    
    Board board;
    Solver solver = new RandomSelectionSolver();

    try {
      board = Board.from("8J9QK8A636K497Q537TJ63K9", "472 TJ4AT3 Q5Q685AT4 22J87A2K59");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      return;
    }

    TriPeaksSolverApp app = new TriPeaksSolverApp(board, solver);
    System.out.println(String.format("Game took %d attemps to win", app.playUntilWon()));
    System.out.println(app.board.getMoves());
  }
}