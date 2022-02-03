package work.lilley.felicity.tri_peaks;

import java.util.Optional;

final class Move {
  private static final Move FLIP = new Move(Type.FLIP, Optional.empty(), Optional.empty());

  private final Type type;
  private final Optional<Card> card;
  private final Optional<Position> position;

  private Move(Type type, Optional<Card> card, Optional<Position> position) {
    this.type = type;
    this.card = card;
    this.position = position;
  }

  static Move createMatchCard(Card card, Position position) {
    return new Move(Type.MATCH, Optional.of(card), Optional.of(position));
  }

  static Move createFlip() {
    return FLIP;
  }

  Type getType() {
    return this.type;
  }

  Optional<Card> getCard() {
    return this.card;
  }

  Optional<Position> getPosition() {
    return this.position;
  }

  @Override
  public String toString() {
    if (Type.FLIP.equals(this.type)) {
      return "FLIP";
    }

    return String.format("MATCH %s at %s", this.card.get(), this.position.get());
  }

  enum Type {
    MATCH,
    FLIP
  }
}

