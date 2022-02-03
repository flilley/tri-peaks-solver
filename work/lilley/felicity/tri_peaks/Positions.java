package work.lilley.felicity.tri_peaks;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

final class Position {
  private static final Map<Integer, Position> POSITIONS = new HashMap<>();
  private static final Map<Position, Set<Position>> BLOCKED_BY_POSITIONS;
  private static final Map<Position, Set<Position>> BLOCKS_POSITIONS;
  private final int level;
  private final int slot;

  static {
    Map<Position, Set<Position>> blockedByPositions = new HashMap<>();
    Map<Position, Set<Position>> blocksPositions = new HashMap<>();

    Set<Entry<Position, Entry<Position, Position>>> positionArrangement = new HashSet<>();

    for(int i = 0; i < Peaks.SLOT_COUNT_BY_LEVEL.get(0); i++ ) {
      positionArrangement.add(new SimpleEntry<>(Position.from(0, i), new SimpleEntry<>(Position.from(1, 2*i), Position.from(1, 2*i + 1))));
    }

    int first = 0;
    for(int i = 0; i < Peaks.SLOT_COUNT_BY_LEVEL.get(1); i++ ) {
      positionArrangement.add(new SimpleEntry<>(Position.from(1, i), new SimpleEntry<>(Position.from(2, first), Position.from(2, first + 1))));
      first += i % 2 == 1?  2 : 1;
    }

    for(int i = 0; i < Peaks.SLOT_COUNT_BY_LEVEL.get(2); i++ ) {
      positionArrangement.add(new SimpleEntry<>(Position.from(2, i), new SimpleEntry<>(Position.from(3, i), Position.from(3, i + 1))));
    }

    positionArrangement.forEach(arrangement -> {
      Position current = arrangement.getKey();
      Position left = arrangement.getValue().getKey();
      Position right = arrangement.getValue().getValue();
      blockedByPositions.put(current, Set.of(left, right));
      setBlocksHelper(blocksPositions, left, current);
      setBlocksHelper(blocksPositions, right, current);
    });

    BLOCKED_BY_POSITIONS = Collections.unmodifiableMap(blockedByPositions);
    BLOCKS_POSITIONS = Collections.unmodifiableMap(blocksPositions);
  }

  private Position(int level, int slot) {
    this.level = level;
    this.slot = slot;
  }

  private static final void setBlocksHelper(Map<Position, Set<Position>> map, Position position, Position blocks) {
    map.computeIfAbsent(position, p -> new HashSet<Position>());
    map.get(position).add(blocks);
  }

  private static final int hashCode(int level, int slot) {
    return level * 31 + slot;
  }

  protected static final Position from(int level, int slot) {
    if (level > Peaks.MAX_LEVEL || slot >= Peaks.SLOT_COUNT_BY_LEVEL.get(level)) {
      throw new RuntimeException(String.format("(%d, %s) is not a valid position", level, slot));
    }
    int hash = hashCode(level, slot);
    POSITIONS.computeIfAbsent(hash, p -> new Position(level, slot));
    return POSITIONS.get(hash);
  }

  protected int getLevel() {
    return level;
  }

  protected int getSlot() {
    return slot;
  }

  protected Set<Position> getPotentiallyUnblockedPositions() {
    return BLOCKS_POSITIONS.getOrDefault(this, Collections.<Position>emptySet());
  }

  protected Set<Position> getBlockingPositions() {
    return BLOCKED_BY_POSITIONS.getOrDefault(this, Collections.<Position>emptySet());
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", level, slot);
  }
}