package done;

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
    private MyJPanel panel;

    public DrawLetters() throws HeadlessException {
        letterList = new LinkedList<>();
        leftShift = 0;

        init();
    }

    private void init() {
        setSize(400, 400);
        setVisible(true);
        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Button");
        button.setBounds(250, 250, 50, 50);
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("click");
                draw();
            }
        });
        add(button);

        panel = new MyJPanel(getGraphics());
        panel.setBounds(0, 0, 200, 200);
        panel.setBorder(BorderFactory.createLineBorder(Color.RED));

        add(panel);

        revalidate();
        repaint();
    }

    public void addLetter(ManipulatorShape shape) {
        letterList.add(shape);
    }

    public void draw() {
        for(ManipulatorShape shape : letterList) {
            panel.paintManipulatorShape(shape);
        }
    }
}
