package work.lilley.felicity.tri_peaks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class Position {
  private static final Map<Position, Set<Position>> BLOCKED_POSITIONS;
  private final int level;
  private final int slot;

  static {
    Map<Position, Set<Position>> blockedPositions = new HashMap<>();

    for(int i = 0; i < Peaks.SLOT_COUNT_BY_LEVEL.get(0); i++ ) {
      blockedPositions.put(new Position(0, i), Set.of(new Position(1, 2*i), new Position(1, 2*i + 1)));
    }

    int first = 0;
    for(int i = 0; i < Peaks.SLOT_COUNT_BY_LEVEL.get(1); i++ ) {
      blockedPositions.put(new Position(1, i), Set.of(new Position(2, first), new Position(2, first + 1)));
      first += i % 2 == 1?  2 : 1;
    }

    for(int i = 0; i < Peaks.SLOT_COUNT_BY_LEVEL.get(2); i++ ) {
      blockedPositions.put(new Position(2, i), Set.of(new Position(3, i), new Position(3, i + 1)));
    }

    BLOCKED_POSITIONS = Collections.unmodifiableMap(blockedPositions);
  }

  private Position(int level, int slot) {
    this.level = level;
    this.slot = slot;
  }

  protected static Position from(int level, int slot) {
    if (level > Peaks.MAX_LEVEL || slot >= Peaks.SLOT_COUNT_BY_LEVEL.get(level)) {
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

  @Override
  public String toString() {
    return String.format("(%d, %d)", level, slot);
  }
}