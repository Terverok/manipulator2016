package main.done.app;

import main.done.letters.Letter;
import main.done.letters.ManipulatorShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pawel on 18.04.2016.
 */
public class DrawLetters extends JFrame {
    private List<ManipulatorShape> letterList;
    private int leftShift;
    private int topShift;
    private MyJPanel panel;
    private int panelWidth, panelHeight;

    public DrawLetters() throws HeadlessException {
        letterList = new LinkedList<>();
        leftShift = 10;
        topShift = 10;

        panelWidth = 750;
        panelHeight = 300;

        init();
    }

    public DrawLetters(int panelWidth, int panelHeight) throws HeadlessException {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        letterList = new LinkedList<>();
        leftShift = 10;
        topShift = 10;

        init();
    }

    private void init() {
        setSize(panelWidth + 50, panelHeight + 100);
        setVisible(true);
        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Draw");
        button.setBounds(250, 310, 80, 50);
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("click");
                draw();
            }
        });
        add(button);

        panel = new MyJPanel(getGraphics());
        panel.setBounds(5, 5, panelWidth, panelHeight);
        panel.setBorder(BorderFactory.createLineBorder(Color.RED));

        add(panel);

        revalidate();
        repaint();
    }

    public void addLetter(ManipulatorShape shape) {
        shape.setShift(leftShift, topShift);
        letterList.add(shape);
        leftShift = (int) shape.getWidth();

        if(leftShift + 100 > panelWidth) {
            topShift = 120;
            leftShift = 10;
        }
    }

    public void addLetter(List<Letter> list) {
        for(Letter shape : list) {
            addLetter(shape);
        }
    }

    public void draw() {
        for(ManipulatorShape shape : letterList) {
            panel.paintManipulatorShape(shape);
        }
    }
}
