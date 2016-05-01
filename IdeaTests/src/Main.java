import done.letters.ConcreteShapes;
import done.app.DrawLetters;

/**
 * Created by pawel on 18.04.2016.
 */
public class Main {
    public static void main(String[] args) {
        DrawLetters drawLetters = new DrawLetters();

        drawLetters.addLetter(ConcreteShapes.letterF());
//        drawLetters.addLetter(ConcreteShapes.letterB());
//        drawLetters.addLetter(ConcreteShapes.letterC());
    }
}
