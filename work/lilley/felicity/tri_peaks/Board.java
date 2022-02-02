package work.lilley.felicity.tri_peaks;

class Board {
  private final Stock stock;

  private Board(Stock stock) {
    this.stock = stock;
  }

  public static Board from(String stockInput) {
    return new Board(Stock.from(stockInput));
  }

  public void reset() {
    this.stock.reset();
  }

  public void print(boolean showHidden) {
    System.out.println(String.format("The cards in the stock are: %s", stock.getDisplayValue(showHidden)));
  }
}
