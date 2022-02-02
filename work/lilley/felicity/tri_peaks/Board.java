package work.lilley.felicity.tri_peaks;

class Board {
  private final Stock stock;
  private final Peaks peaks;

  private Board(Stock stock, Peaks peaks) {
    this.stock = stock;
    this.peaks = peaks;
  }

  public static Board from(String stockInput, String peaksInput) {
    return new Board(Stock.from(stockInput), Peaks.from(peaksInput));
  }

  public void reset() {
    this.stock.reset();
    this.peaks.reset();
  }

  public void print(boolean showHidden) {
    System.out.println(String.format("The cards in the stock are: %s", this.stock.getDisplayValue(showHidden)));
    System.out.println(String.format("The table is:\n%s", this.peaks.getDisplayValue(showHidden)));
  }
}
