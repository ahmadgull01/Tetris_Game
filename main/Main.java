package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ADD GAME PANEL IN THE WINDOW
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack(); // WE USE IT SO THAT THE SIZE OF GAME PANEL WILL BECOME THE SIZE OF WINDOW


        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.runGame();
    }
}
