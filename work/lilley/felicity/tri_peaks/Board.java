package work.lilley.felicity.tri_peaks;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Board {
  private static final int EXPECTED_STOCK_SIZE = 24;
  private final List<Card> stock;
  private final List<Card> waste;

  private Board(List<Card> stock) {
    this.stock = stock;
    this.waste = new LinkedList<>();
  }

  public static Board from(String stock) {
    List<Card> stockCards = Card.from(stock);

    if (stockCards.size() != EXPECTED_STOCK_SIZE) {
      throw new IllegalArgumentException(String.format("Expected %d stock cards but got %d", EXPECTED_STOCK_SIZE, stockCards.size()));
    }

    return new Board(stockCards);
  }

  public Card discard() {
    this.waste.add(0, this.stock.remove(0));
    return this.waste.get(0);
  }

  public Card undoDiscard() {
    this.stock.add(0, this.waste.remove(0));
    return this.waste.get(0);
  }

  public void reset() {
    while(!waste.isEmpty()) {
      this.stock.add(0, this.waste.remove(0));
    }
  }

  public void print(boolean showHidden) {
    String cardString = showHidden ? getFullStockString() : getSummaryStockString();
    System.out.println(String.format("The cards in the stock are: %s", cardString));
  }

  private String getFullStockString() {
    return stock.stream()
    .map(c -> c.getValue().toString())
    .collect(Collectors.joining("->"));
  }

  private String getSummaryStockString() {
    return String.format("%s (%d)", stock.get(0).getValue(), stock.size() - 1);
  }
}
