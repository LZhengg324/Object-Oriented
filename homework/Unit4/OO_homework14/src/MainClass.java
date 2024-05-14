public class MainClass {
    public static void main(String[] args) {
        LibrarySystem librarySystem = new LibrarySystem();
        Input input = new Input(librarySystem);
        input.getInput();
        librarySystem.start();
    }
}
