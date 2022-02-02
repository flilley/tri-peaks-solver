package work.lilley.felicity.tri_peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class Board {
  private final Stock stock;
  private final Peaks peaks;
  private final List<Move> moves;

  private Board(Stock stock, Peaks peaks) {
    this.stock = stock;
    this.peaks = peaks;
    this.moves = new LinkedList<Move>();
  }

  public static Board from(String stockInput, String peaksInput) {
    Board board = new Board(Stock.from(stockInput), Peaks.from(peaksInput));
    board.stock.discard();
    return board;
  }

  public void reset() {
    this.stock.reset();
    this.peaks.reset();
    this.stock.discard();
    this.moves.clear();
  }
  
  public List<Move> getMoves() {
    return this.moves;
  }

  public void print(boolean showHidden) {
    System.out.println(String.format("The cards in the stock are: %s", this.stock.getDisplayValue(showHidden)));
    System.out.println(String.format("The table is:\n%s", this.peaks.getDisplayValue(showHidden)));
  }

  public Collection<Move> getPotentialMoves() {
    List<Move> moves = new ArrayList<>();
    if (this.stock.hasCardsRemaining()) {
      moves.add(Move.flip());
    }
    moves.addAll(this.peaks.getAllowedMoves(this.stock.getCurrentMatchCard()));
    return moves;
  }

  public GameState calculateGameState() {
    if (!peaks.hasCardsRemaining()) {
      return GameState.WON;
    }
    return getPotentialMoves().isEmpty() ? GameState.LOST : GameState.PLAYING;
  }

  enum GameState {
    WON,
    LOST,
    PLAYING
  }

  public void play(Move selectedMove) {
    this.moves.add(selectedMove);
    if (selectedMove.getType() == Move.Type.FLIP) {
      this.stock.discard();
    } else  {
      Card card = selectedMove.getCard().orElseThrow();
      Position position = selectedMove.getPosition().orElseThrow();
      this.peaks.match(card, position);
      this.stock.discard(card);
    }
  }
}
