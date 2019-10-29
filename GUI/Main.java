import javax.swing.*;
import java.awt.*;
import javax.swing.SwingUtilities;
import java.lang.Thread;

public class Main {

    /**
     * main- runs the GUI
     * @param args - none
     */
    public static void main(String[] args) {
        JFrame userFrame = new
        JFrame("Shortest Path Algorithms");
        Gui userGui = new Gui();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userFrame.setContentPane(userGui.userPanel);
                userFrame.setLocation(0,0);
                userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                userFrame.pack();
                //userFrame.enableInitialControls(true);
                userFrame.setVisible(true);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.out.println("Error in main run: " + e);
                }
            }
        });
        DrawingPanel panel = new DrawingPanel(700,500);
        Graphics2D g = panel.getGraphics();
        AnimationArea drawing = new AnimationArea();
        Graph userGraph = new Graph();
        drawing.animate(g, panel, userGui, userGraph);

        //close everything
        panel.closeWindow();
        userFrame.dispose();
    }
}
