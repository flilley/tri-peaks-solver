package work.lilley.felicity.tri_peaks;

import java.util.Set;
import java.util.Map.Entry;

final class Board {
  private final Stock stock;
  private final Peaks peaks;

  private Board(Stock stock, Peaks peaks) {
    this.stock = stock;
    this.peaks = peaks;
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
  }

  public void print(boolean showHidden) {
    System.out.println(String.format("The cards in the stock are: %s", this.stock.getDisplayValue(showHidden)));
    System.out.println(String.format("The table is:\n%s", this.peaks.getDisplayValue(showHidden)));
  }

  public Set<Entry<Position, Card>> getPotentialMoves() {
    return this.peaks.getAllowedMoves(this.stock.getCurrentMatchCard());
  }
}
