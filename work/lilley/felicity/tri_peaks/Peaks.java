package work.lilley.felicity.tri_peaks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class Peaks {
  public static final int MAX_LEVEL = 3;
  public static final Map<Integer, Integer> SLOT_COUNT_BY_LEVEL = Map.of(0, 3, 1, 6, 2, 9, 3, 10);

  private final Map<Position, Card> map;

  private Peaks(Map<Position, Card> map, Set<Position> open) {
    this.map = map;
  }

  public static Peaks from(String peaksInput) {
    Collection<Card> cards = Card.from(peaksInput);
    Map<Position, Card> map = new HashMap<>();
    Set<Position> open = getOriginallyOpenCards();

    int currentLevel = 0;
    int currentSlot = 0;
    for(var card : cards) {
      if(currentSlot >= SLOT_COUNT_BY_LEVEL.get(currentLevel)) {
        currentLevel++;
        currentSlot = 0;
      }
      
      Position position = Position.from(currentLevel, currentSlot);
      card.setStatus(open.contains(position) ? Card.Status.OPEN : Card.Status.HIDDEN);
      map.put(position, card);

      currentSlot++;
    }

    return new Peaks(map, open);
  }

  private static Set<Position> getOriginallyOpenCards() {
    return IntStream.range(0, SLOT_COUNT_BY_LEVEL.get(3)).boxed()
    .map(i -> Position.from(3, i))
    .collect(Collectors.toSet());
  }

  public void reset() {
    Set<Position> open = getOriginallyOpenCards();
    map.entrySet().forEach(entry -> {
      var positionIsOpen = open.contains(entry.getKey());
      var status = positionIsOpen ? Card.Status.OPEN : Card.Status.HIDDEN;
      entry.getValue().setStatus(status);
    });
  }

  public String getDisplayValue(boolean showHidden) {
    StringBuilder displayValue = new StringBuilder();
    for(int level = 0; level <= MAX_LEVEL; level++) {
      displayValue.append(" ".repeat(3-level));
      for(int slot = 0; slot < SLOT_COUNT_BY_LEVEL.get(level); slot++) {
        Position currentPosition = Position.from(level, slot);
        if (map.containsKey(currentPosition)) {
          Character cardDisplayValue = map.get(currentPosition).getDisplayValue(showHidden);
          displayValue.append(cardDisplayValue);
          displayValue.append(level == 0 ? "    " : (level == 1 && slot % 2 == 1 ? "  ": " "));
        }
      }
      displayValue.append('\n');
    }
    return displayValue.toString();
  }

  public Set<Entry<Position, Card>> getAllowedMoves(Card currentDiscardCard) {
    return map.entrySet().stream()
      .filter(entry -> currentDiscardCard.isAllowedMatch(entry.getValue()))
      .collect(Collectors.toSet());
  }
}