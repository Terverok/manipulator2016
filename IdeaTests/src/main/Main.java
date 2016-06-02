package main;

import main.done.letters.ConcreteShapes;
import main.done.app.DrawLetters;

/**
 * Created by pawel on 18.04.2016.
 */
public class Main {
    public static void main(String[] args) {
        DrawLetters drawLetters = new DrawLetters();

        drawLetters.addLetter(ConcreteShapes.getAlphabet());
    }
}
