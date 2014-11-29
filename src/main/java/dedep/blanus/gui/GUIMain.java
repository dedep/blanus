package dedep.blanus.gui;

import javax.swing.*;
import java.awt.*;

public class GUIMain {

    public static void createAndShowGUI() {
        JFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
