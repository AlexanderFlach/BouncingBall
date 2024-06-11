package app;


import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Using a method reference: see https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());

        Animation animation = new BallAnimation();
        animation.start();
    }


}
