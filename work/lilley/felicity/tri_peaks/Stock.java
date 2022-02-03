package work.lilley.felicity.tri_peaks;

import java.util.LinkedList;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Collectors;

final class Stock {
  private static final int EXPECTED_STOCK_SIZE = 24;

  private final List<Card> stock;
  // Boolean represents whether the card was added from the stock (true) or the peaks (false)
  private final List<SimpleImmutableEntry<Card, Boolean>> waste;

  private Stock(List<Card> stock) {
    this.stock = stock;
    this.waste = new LinkedList<SimpleImmutableEntry<Card, Boolean>>();
  }

  public static Stock from(String stockInput) {
    List<Card> stockCards = Card.from(stockInput);

    if (stockCards.size() != EXPECTED_STOCK_SIZE) {
      throw new IllegalArgumentException(String.format("Expected %d stock cards but got %d", EXPECTED_STOCK_SIZE, stockCards.size()));
    }

    return new Stock(stockCards);
  }

  public Card discard() {
    return this.discard(stock.remove(0), true);
  }

  public Card discard(Card cardToDiscard) {
    return this.discard(cardToDiscard, false);
  }

  private Card discard(Card cardToDiscard, Boolean fromStock) {
    this.waste.add(0, new SimpleImmutableEntry<>(cardToDiscard, fromStock));
    return cardToDiscard;
  }

  public void undo() {
    SimpleImmutableEntry<Card, Boolean> lastDiscarded = this.waste.remove(0);
    if(lastDiscarded.getValue()) {
      this.stock.add(0, lastDiscarded.getKey());
    }
  }

  public void reset() {
    while(!waste.isEmpty()) {
      SimpleImmutableEntry<Card, Boolean> wasteTop = this.waste.remove(0);
      if (wasteTop.getValue()) {
        this.stock.add(0, wasteTop.getKey());
      }
    }
  }

  public Card getCurrentMatchCard() {
    return this.waste.get(0).getKey();
  }

  public boolean hasCardsRemaining() {
    return !this.stock.isEmpty();
  }

  public String getDisplayValue(boolean showHidden) {
    return showHidden ? getFullStockString() : getSummaryStockString();
  }

  private String getFullStockString() {
    String stockString = stock.stream()
      .map(c -> c.getValue().toString())
      .collect(Collectors.joining("->"));
    return String.format("[%s] %s", getCurrentMatchCard(), stockString);
  }

  private String getSummaryStockString() {
    return String.format("%s (%d)", getCurrentMatchCard(), stock.size());
  }
}
