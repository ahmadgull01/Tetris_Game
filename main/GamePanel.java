package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public int FPS = 60;
    Thread gameThread; // USE TO RUN GAME LOOP, AND FOR THIS WE HAVE TO IMPLEMENT RUNNABLE
    PlayManager pm;
    public static Sounds music = new Sounds();
    public static Sounds se = new Sounds();

    public GamePanel() {
        // PANEL SETTING
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        // IMPLEMENT KEY LISTENER
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);


        pm = new PlayManager();
    }

    public void runGame() {
        gameThread = new Thread(this);
        gameThread.start();
        // WHEN A THREAD START IT AUTOMATICALLY CALL THE RUN METHOD

        music.play(0, true);
        music.loop();
    }

    @Override
    public void run() {

        // GAME LOOP
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint(); // REPAINT IS BASICALLY THE PRINT COMPONENT METHOD
                delta--;
            }
        }

    }

    public void update() {
        if (KeyHandler.pausePressed == false && pm.gameOver == false) {
            pm.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        pm.draw(g2);
    }
}
