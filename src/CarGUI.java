/* 
 * CarGUI.java
 * Ario Barin Ostovary & Kevin Dang
 * Main class for the Car GUI
 */

import javax.swing.*;

public class CarGUI extends JFrame {
    CarGUIPanel panel = new CarGUIPanel();

    public CarGUI() {
        super("GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new CarGUI();
    }
}