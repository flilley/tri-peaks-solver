package work.lilley.felicity.tri_peaks;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import work.lilley.felicity.tri_peaks.Card.Status;

public class SmartSelectionSolver extends Solver {
  private final Map<Move, Boolean> isMoveBlocking = new HashMap<>();
  private final Set<Character> remainingStockValues = new HashSet<>();
  private final Map<Character, Card.Status> remainingPeakCharacterStatus = new HashMap<>();
  private final Map<Position, Card.Status> remainingPeakPositionValues = new HashMap<>();

  SmartSelectionSolver(Board board) {
    super(board);
  }

  @Override
  void doPrework(Map<Move.Type, List<Move>> groupedMoves) {
    board.getRemainingStockCards().stream()
      .map(card -> card.getValue())
      .forEach(this.remainingStockValues::add);

    BiFunction<Status, Status, Status> mergeStatus = (v1, v2) -> v1 == Status.OPEN || v2 == Status.OPEN ? Status.OPEN : Status.HIDDEN;
    board.getRemainingPeakCards().entrySet().stream()
      .forEach(entry -> {
        Position position = entry.getKey();
        Card card = entry.getValue();
        this.remainingPeakCharacterStatus.merge(card.getValue(), card.getStatus(), mergeStatus);
        this.remainingPeakPositionValues.put(position, card.getStatus());
      });

    groupedMoves.getOrDefault(Move.Type.MATCH, Collections.emptyList()).stream()
      .forEach(move -> {
        Character moveCardValue = move.getCard().get().getValue();
        boolean isBlocking =  matchMightBlockFutureMoves(move.getPosition().get(), moveCardValue);
        this.isMoveBlocking.put(move, isBlocking);
      });
  }

  private boolean matchMightBlockFutureMoves(Position position, Character value) {
    return arePotentiallyBlockedCardsLeftInPeak(value) && !otherMatchesInStackOrPeak(position, value);
  }

  private boolean arePotentiallyBlockedCardsLeftInPeak(Character value) {
    Set<Character> potentiallyBlockedValues = Card.getAllowedMatchValues(value);
    boolean blockedCardsInPeak = this.remainingPeakCharacterStatus.keySet().stream()
      .anyMatch(v -> potentiallyBlockedValues.contains(v));
    return blockedCardsInPeak;
  }

  private boolean otherMatchesInStackOrPeak(Position position, Character value) {
    Set<Character> stockAndOpenValues = this.remainingPeakCharacterStatus.entrySet()
      .stream()
      .filter(e -> e.getValue() == Card.Status.OPEN)
      .map(e -> e.getKey())
      .collect(Collectors.toSet());
    stockAndOpenValues.addAll(this.remainingStockValues);

    if (stockAndOpenValues.contains(value)) {
      return true;
    }

    Set<Character> oppositeMatchingCards = Card.getAllowedMatchValues(value).stream()
      .flatMap(v -> Card.getAllowedMatchValues(v).stream())
      .filter(v -> !v.equals(value))
      .collect(Collectors.toSet());

    if (stockAndOpenValues.containsAll(oppositeMatchingCards)) {
      return true;
    }
    
    return false;
  }

  @Override
  boolean shouldDoOptionalFlip(List<Move> matchMoves) {
    return !this.isMoveBlocking.containsValue(false);
  }

  @Override
  Move getBestMatch(List<Move> matchMoves) {
    Map<Long, Set<Move>> scoredMoves = matchMoves.stream()
      .map(move -> new SimpleEntry<>(move, getMoveScore(move)))
      .collect(Collectors.groupingBy(SimpleEntry::getValue, Collectors.mapping(SimpleEntry::getKey, Collectors.toSet())));

    Long bestScore = scoredMoves.keySet().stream().max(Long::compare).get();
    Move bestMatch = scoredMoves.get(bestScore).iterator().next();
    return bestMatch;
  }

  Long getMoveScore(Move move) {
    long score = 0L;
    
    score += this.isMoveBlocking.get(move) ? -100 : 0;
    score += move.getPosition().get().getLevel();
    score += getUnblockedPositionCount(move) * 3;
    score += getPotentialTwoMoveCount(move) * 7;

    return score;
  }

  private long getUnblockedPositionCount(Move move) {
    Predicate<Position> wouldUnblockPosition = (p) -> !p.getBlockingPositions().stream()
      .filter(blocking -> blocking != move.getPosition().get())
      .anyMatch(this.remainingPeakPositionValues::containsKey);

    return move.getPosition().get().getPotentiallyUnblockedPositions().stream()
      .filter(p -> this.remainingPeakPositionValues.get(p) == Card.Status.HIDDEN)
      .filter(wouldUnblockPosition::test)
      .count();
  }

  private long getPotentialTwoMoveCount(Move move) {
    Set<Character> twoMoveValues = Card.getAllowedMatchValues(move.getCard().get().getValue()).stream()
      .flatMap(c -> Card.getAllowedMatchValues(c).stream())
      .collect(Collectors.toSet());
    long potentialNextMoveCount = twoMoveValues.stream()
      .map(c -> this.remainingPeakCharacterStatus.get(c))
      .filter(s -> s == Card.Status.OPEN)
      .count();
    return potentialNextMoveCount;
  }
  
  @Override
  void doCleanup() {
    this.isMoveBlocking.clear();
    this.remainingPeakCharacterStatus.clear();
    this.remainingPeakPositionValues.clear();
  }
}
