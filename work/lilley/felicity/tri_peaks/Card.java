package work.lilley.felicity.tri_peaks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Collectors;

final class Card {
  private static final Set<Character> ALLOWED_VALUES = Set.of('A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K');
  private static final Map<Character, Set<Character>> ALLOWED_MATCHES;
  private static final Character HIDDEN_CHARACTER = '*';
  private static final Character MISSING_CHARACTER = ' ';

  static {
    Map<Character, Set<Character>> allowedMatches = Map.ofEntries(
      new SimpleImmutableEntry<>('A', Set.of('K', '2')),
      new SimpleImmutableEntry<>('2', Set.of('A', '3')),
      new SimpleImmutableEntry<>('3', Set.of('2', '4')),
      new SimpleImmutableEntry<>('4', Set.of('3', '5')),
      new SimpleImmutableEntry<>('5', Set.of('4', '6')),
      new SimpleImmutableEntry<>('6', Set.of('5', '7')),
      new SimpleImmutableEntry<>('7', Set.of('6', '8')),
      new SimpleImmutableEntry<>('8', Set.of('7', '9')),
      new SimpleImmutableEntry<>('9', Set.of('8', 'T')),
      new SimpleImmutableEntry<>('T', Set.of('9', 'J')),
      new SimpleImmutableEntry<>('J', Set.of('T', 'Q')),
      new SimpleImmutableEntry<>('Q', Set.of('J', 'K')),
      new SimpleImmutableEntry<>('K', Set.of('Q', 'A'))
    );
    ALLOWED_MATCHES = allowedMatches;
  }

  private final char value;
  private Status status;

  private Card(Character value) {
    this.value = value;
    this.status = Status.UNKNOWN;
  }

  public static Card from(char value) {
    if (!ALLOWED_VALUES.contains(value)) {
      throw new IllegalArgumentException(String.format("%s is not a valid card value: allowed values are %s", value, ALLOWED_VALUES));
    }
    return new Card(value);
  }

  public static List<Card> from(String value) {
    return value
      .toUpperCase()
      .chars()
      .mapToObj(c -> (char) c)
      .filter(c -> c != ' ')
      .map(c -> Card.from(c))
      .collect(Collectors.toCollection(LinkedList::new));
  }

  public Character getValue() {
    return this.value;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public boolean isAllowedMatch(Card card) {
    return this.status == Status.OPEN && ALLOWED_MATCHES.get(this.value).contains(card.value);
  }

  public boolean isAllowedValue(Character value) {
    return ALLOWED_VALUES.contains(value);
  }

  public Character getDisplayValue(boolean showHidden) {
    return showHidden ? this.value : this.getMaskedValue();
  }

  private Character getMaskedValue() {
    switch (this.status) {
      case HIDDEN:
          return HIDDEN_CHARACTER;
      case REMOVED:
          return MISSING_CHARACTER;
      default:
         return this.value;
    }
  }

  static enum Status {
   OPEN, HIDDEN, REMOVED, UNKNOWN
  }

  @Override
  public String toString() {
    return this.getValue().toString();
  }
}