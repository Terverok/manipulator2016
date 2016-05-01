import done.ConcreteShapes;
import done.DrawLetters;
import done.Letter;

/**
 * Created by pawel on 18.04.2016.
 */
public class Main {
    public static void main(String[] args) {
        DrawLetters drawLetters = new DrawLetters();

        drawLetters.addLetter(ConcreteShapes.letterC());
    }
}
