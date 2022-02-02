package work.lilley.felicity.tri_peaks;

class TriPeaksSolverApp {
  public static void main(String[] args) {
    System.out.println("Welcome to the Tri Peaks Solver");
    try {
      Board board = Board.from("8J9QK8A636K497Q537TJ63K9", "472 TJ4AT3 Q5Q685AT4 22J87A2K59");
      board.print(false);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}