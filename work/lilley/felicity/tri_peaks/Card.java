package work.lilley.felicity.tri_peaks;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Card {
  private static final Set<Character> allowedValues = Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');
  private final char value;

  private Card(Character value) {
    this.value = value;
  }

  public static Card from(char value) {
    if (!allowedValues.contains(value)) {
      throw new IllegalArgumentException(String.format("%s is not a valid card value: allowed values are %s", value, allowedValues));
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

  public boolean isAllowedValue(Character value) {
    return allowedValues.contains(value);
  }

  public Character getValue() {
    return value;
  }
}