package work.lilley.felicity.tri_peaks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

abstract class Solver {
  private final Board board;
  private final List<Move> winningMoves;

  protected Solver(Board board) {
    this.board = board;
    this.winningMoves = new LinkedList<>();
  }

  abstract Move calculateNextMove(Collection<Move> potentialMoves);

  List<Move> getMoves() {
    return board.getMoves();
  }

  Board.GameState play(boolean showOutput) {
    if (showOutput) {
      board.print(true);
    }

    while(board.calculateGameState() == Board.GameState.PLAYING) {
      Collection<Move> potentialMoves = board.getPotentialMoves();
      Move selectedMove = calculateNextMove(potentialMoves);
      board.play(selectedMove);
      if (showOutput) {
        board.print(false);
      }
    }
    return board.calculateGameState();
  }

  int playUntilWon() {
    int attempts = 1;

    board.print(true);
    while(true) {
      Board.GameState result = play(false);
      System.out.println(String.format("Result of attempt %d is %s", attempts, result));
      if (result == Board.GameState.WON) {
        this.winningMoves.clear();
        this.winningMoves.addAll(board.getMoves());
        return attempts;
      }
      board.reset();
      attempts++;
    }
  }

  public void replay(boolean showOutput) {
    Consumer<Move> handleMove = move -> board.play(move);
    if (showOutput) {
      handleMove = handleMove.andThen(move -> board.print(false));
    }
    
    this.winningMoves.forEach(handleMove);
  }
}
