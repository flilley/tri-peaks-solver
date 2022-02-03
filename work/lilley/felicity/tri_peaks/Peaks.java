package work.lilley.felicity.tri_peaks;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class Peaks {
  public static final int MAX_LEVEL = 3;
  public static final Map<Integer, Integer> SLOT_COUNT_BY_LEVEL = Map.of(0, 3, 1, 6, 2, 9, 3, 10);

  private final Map<Position, Card> map;

  private Peaks(Map<Position, Card> map) {
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

    return new Peaks(map);
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

  public boolean hasCardsRemaining() {
    return map.values().stream().anyMatch(card -> card.getStatus() != Card.Status.REMOVED);
  }

  public List<Move> getAllowedMoves(Card currentDiscardCard) {
    return map.entrySet().stream()
      .filter(entry -> entry.getValue().isAllowedMatch(currentDiscardCard))
      .sorted((e1, e2) -> e1.getKey().getLevel() - e2.getKey().getLevel())
      .map(entry -> Move.createMatchCard(entry.getValue(), entry.getKey()))
      .collect(Collectors.toList());
  }

  public void match(Card card, Position position) {
    card.setStatus(Card.Status.REMOVED);
    position.getPotentiallyUnblockedPositions()
      .forEach(p -> {
        if(p.getBlockingPositions().stream().allMatch(b -> map.get(b).getStatus() == Card.Status.REMOVED)) {
          map.get(p).setStatus(Card.Status.OPEN);
        }
      });
  }

  public String getDisplayValue(boolean showHidden) {
    if (!this.hasCardsRemaining()) {
      return "<EMPTY>";
    }

    StringBuilder displayValue = new StringBuilder();
    IntStream.rangeClosed(0, MAX_LEVEL)
      .forEach(level -> getDisplayValueForLevel(level, showHidden).ifPresent(v -> displayValue.append(v)));
    return displayValue.toString();
  }

  private Optional<StringBuilder> getDisplayValueForLevel(int level, boolean showHidden) {
    final StringBuilder levelValue = new StringBuilder(" ".repeat(3-level));

    List<String> displayValues = IntStream.range(0, SLOT_COUNT_BY_LEVEL.get(level)).boxed()
      .map(slot -> Position.from(level, slot))
      .filter(position -> map.containsKey(position))
      .map(position -> getDisplayValueForPosition(position, showHidden))
      .toList();

    if(displayValues.stream().anyMatch(c -> !c.isBlank())) {
      displayValues.forEach(levelValue::append);
      levelValue.append('\n');
      return Optional.of(levelValue);
    }

    return Optional.empty();
  }

  private String getDisplayValueForPosition(Position position, boolean showHidden) {
    Character displayValue = map.get(position).getDisplayValue(showHidden);
    String spaceValue = position.getLevel() == 0 ? "    " : 
      (position.getLevel() == 1 && position.getSlot() % 2 == 1 ? "  ": " ");
    return String.format("%s%s", displayValue, spaceValue);
  }
}