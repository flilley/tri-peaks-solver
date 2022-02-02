package work.lilley.felicity.tri_peaks;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Card {
  private static final Set<Character> ALLOWED_VALUES = Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');
  private static final Character HIDDEN_CHARACTER = '*';
  private static final Character MISSING_CHARACTER = ' ';

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

  public boolean isAllowedValue(Character value) {
    return ALLOWED_VALUES.contains(value);
  }

  public Character getDisplayValue(boolean showHidden) {
    return showHidden ? this.getValue() : this.getMaskedValue();
  }

  private Character getMaskedValue() {
    switch (this.status) {
      case HIDDEN:
          return HIDDEN_CHARACTER;
      case REMOVED:
          return MISSING_CHARACTER;
      default:
         return this.getValue();
    }
  }

  static enum Status {
   OPEN, HIDDEN, REMOVED, UNKNOWN
  }
}