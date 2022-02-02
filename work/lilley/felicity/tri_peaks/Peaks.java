package work.lilley.felicity.tri_peaks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Peaks {
  private static final int MAX_LEVEL = 3;
  private static final Map<Integer, Integer> SLOT_COUNT_BY_LEVEL = Map.of(0, 3, 1, 6, 2, 9, 3, 10);

  private final Map<Position, Card> map;
  // TODO: are these unnecessary given card status
  private final Set<Position> open; 
  private final Set<Position> discarded;

  private Peaks(Map<Position, Card> map, Set<Position> open) {
    this.map = map;
    this.open = open;
    this.discarded = new HashSet<>();
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
    .map(i -> new Position(3, i))
    .collect(Collectors.toSet());
  }

  public void reset() {
    this.open.clear();
    this.open.addAll(getOriginallyOpenCards());
    this.discarded.clear();
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

  private static final class Position {
    private static final Map<Position, Set<Position>> BLOCKED_POSITIONS;
    private final int level;
    private final int slot;

    static {
      Map<Position, Set<Position>> blockedPositions = new HashMap<>();

      for(int i = 0; i < SLOT_COUNT_BY_LEVEL.get(0); i++ ) {
        blockedPositions.put(new Position(0, i), Set.of(new Position(1, 2*i), new Position(1, 2*i + 1)));
      }

      int first = 0;
      for(int i = 0; i < SLOT_COUNT_BY_LEVEL.get(1); i++ ) {
        blockedPositions.put(new Position(1, i), Set.of(new Position(2, first), new Position(2, first + 1)));
        first += i % 2 == 1?  2 : 1;
      }

      for(int i = 0; i < SLOT_COUNT_BY_LEVEL.get(2); i++ ) {
        blockedPositions.put(new Position(2, i), Set.of(new Position(3, i), new Position(3, i + 1)));
      }

      BLOCKED_POSITIONS = Collections.unmodifiableMap(blockedPositions);
    }

    private Position(int level, int slot) {
      this.level = level;
      this.slot = slot;
    }

    protected static Position from(int level, int slot) {
      if (level > MAX_LEVEL || slot >= SLOT_COUNT_BY_LEVEL.get(level)) {
        throw new RuntimeException(String.format("(%d, %s) is not a valid position, level, slot"));
      }
      return new Position(level, slot);
    }

    protected Set<Position> getPositionsToUnblock() {
      return BLOCKED_POSITIONS.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Position)) {
            return false;
        }
        
        Position p = (Position) o;

        return this.level== p.level && this.slot == p.slot;
    }

    @Override
    public int hashCode() {
      return level * 31 + slot;
    }
  }
}