package work.lilley.felicity.tri_peaks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import work.lilley.felicity.tri_peaks.Move.Type;

abstract class Solver {
  private final List<Move> finalMoves;
  protected final Board board;

  protected Solver(Board board) {
    this.board = board;
    this.finalMoves = new LinkedList<>();
  }

  List<Move> getMoves() {
    return board.getMoves();
  }

  int playUntilWon(int attemptLimit) {
    int attempts = 0;

    board.print(true);
    while(attempts < attemptLimit && board.calculateGameState() != Board.GameState.WON) {
      attempts++;
      board.reset();
      Board.GameState result = play(false);
      System.out.println(String.format("Result of attempt %d is %s", attempts, result));
    }

    this.finalMoves.clear();
    this.finalMoves.addAll(board.getMoves());
    
    return attempts;
  }

  Board.GameState play(boolean showOutput) {
    if (showOutput) {
      board.print(true);
    }

    while(board.calculateGameState() == Board.GameState.PLAYING) {
      Collection<Move> potentialMoves = board.getPotentialMoves();
      Move selectedMove = calculateNextMove(potentialMoves, showOutput);
      board.play(selectedMove);
      if (showOutput) {
        board.print(false);
      }
    }
    return board.calculateGameState();
  }

  Move calculateNextMove(Collection<Move> potentialMoves, boolean showOutput) {
    if (potentialMoves.isEmpty()) {
      throw new RuntimeException("Ran out of moves unexpectedly");
    }

    Map<Move.Type, List<Move>> groupedMoves = getMovesGroupedByType(potentialMoves);
    List<Move> matchMoves = groupedMoves.get(Move.Type.MATCH);

    doPrework(groupedMoves);
    if(matchMoves == null || (groupedMoves.containsKey(Move.Type.FLIP) && shouldDoOptionalFlip(matchMoves))) {
      if (matchMoves != null && showOutput) {
        System.out.println("Doing optional FLIP");
      }
      return groupedMoves.get(Move.Type.FLIP).get(0);
    }
    
    Move bestMove = this.getBestMatch(matchMoves);

    doCleanup();

    return bestMove;
  }

  private Map<Type, List<Move>> getMovesGroupedByType(Collection<Move> potentialMoves) {
    return potentialMoves.stream()
      .collect(Collectors.groupingBy(Move::getType, Collectors.toList()));
  }

  void doPrework(Map<Type, List<Move>> groupedMoves) {
  }

  abstract boolean shouldDoOptionalFlip(List<Move> matchMoves);

  abstract Move getBestMatch(List<Move> matchMoves);

  void doCleanup() {
  }

  public void replay(boolean showOutput) {
    Consumer<Move> handleMove = move -> board.play(move);
    if (showOutput) {
      handleMove = handleMove.andThen(move -> board.print(false));
    }
    
    this.finalMoves.forEach(handleMove);
  }

  public boolean gameWon() {
    return getResult() == Board.GameState.WON;
  }

  public Board.GameState getResult() {
    return this.board.calculateGameState();
  }
}
