package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {

    // MAIN PLAY AREA
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // OTHERS
    public static int dropInterval = 60; // MINO DROP IN EVERY 60 FRAMES
    boolean gameOver;

    // EFFECTS
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // SCORE
    int level = 1;
    int lines;
    int score;


    public PlayManager() {
        // MAIN PLAY AREA FRAME
        left_x = (GamePanel.WIDTH/2) - (WIDTH-2); // 1280/2 - 360/2 = 460
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        // Mino
        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        // NEXT MINO
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // SET STARTING MINO
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        // NEXT MINO
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {
        // PICK A RANDOM MINO
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i) {
            case 0 :
                mino = new Mino_L1();
                break;
            case 1 :
                mino = new Mino_L2();
                break;
            case 2 :
                mino = new Mino_Square();
                break;
            case 3 :
                mino = new Mino_Bar();
                break;
            case 4 :
                mino = new Mino_T();
                break;
            case 5 :
                mino = new Mino_Z1();
                break;
            case 6 :
                mino = new Mino_Z2();
                break;
        }
        return mino;
    }

    public void update() {

        // CHECK IF THE CURRENT MINO IS ACTIVE
        if (currentMino.active == false) {

            // IF THE MINO IS ACTIVE PUT IT IN THE STATIC BLOCK
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //  CEHCK IF THE GAME IS OVER
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                // THIS MEANS THE CURENT MINO IMMEDIATELY COLLIDES A BLOCK AND COULD NOT MOVE AT ALL
                // SO IT 'XY' ARE THE SAME WITH THE NEXT MINO
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2, false);
            }

            currentMino.deactivating = false;

            // REPLACE THE CURRENT MINO WITH THE NEXT MINO
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // WHEN A MINO BECOMES IN ACTIVE CHECK IF CERTAIN LINES CAN BE DELETED
            checkDelete();
        }
        else {
            currentMino.update();
        }
    }

    private void checkDelete() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {

            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    // INCREASE THE COUNT IF THERE IS A STATIC BLOCK
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if (x == right_x) {

                if (x == right_x) {

                    // IF BLOCK COUNT HIT 12 THAT MEANS THE CURRENT Y LINE IS ALL FILLED WITH BLOCKS
                    // SO WE CAN DELETE THEM
                    if (blockCount == 12) {

                        effectCounterOn = true;
                        effectY.add(y);

                        for (int i = staticBlocks.size()-1; i > -1; i--) {
                            // REMOVE ALL THE BLOCKS IN THE CURRENT Y LINE
                            if (staticBlocks.get(i).y == y) {
                                staticBlocks.remove(i);
                            }
                        }

                        lineCount++;
                        lines++;
                        // DROP SPEED
                        // IF TEH LINE SCORE HIT CERTAIN NUMBER INCREASE THE DROP SPEED
                        // 1 IS THE FASTEST
                        if (lines % 10 == 0 && dropInterval > 1) {

                            level++;
                            if (dropInterval > 10) {
                                dropInterval -= 10;
                            }
                            else {
                                dropInterval -= 1;
                            }
                        }

                        // A LINE HAS BEEN DELETED SO WE WILL MOVE DOWN THE BLOCKS THAT ARE ABOVE
                        for (int i = 0; i < staticBlocks.size(); i++) {
                            // IF A BLOCK IS ABOVE THE CURRENT Y MOVE IT DOWN BY THE BLOCK SIZE
                            if (staticBlocks.get(i).y < y) {
                                staticBlocks.get(i).y += Block.SIZE;
                            }
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // ADD SCORE
        if (lineCount > 0) {
            GamePanel.se.play(1, false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g2) {

        // DRAW PLAY AREA FRAME
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f)); // WIDTH OF FRAME
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        // DRAW NEXT MINOO FRAME
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y + 60);

        // DRAW SCORE FRAME
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y);
        y += 70;
        g2.drawString("LINES: " + lines, x, y);
        y += 70;
        g2.drawString("SCORE: " + score, x, y);


        // DRAW CURRENT MINO
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        // DRAW THE NEXT MINO
        nextMino.draw(g2);

        // DRAW STATIC BLOCKS
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

        // DRAW EFFECT
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.RED);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }


        // DRAW PAUSE AND GAME OVER
        g2.setColor(Color.YELLOW);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER!", x, y);
        }
        else if (KeyHandler.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        // DRAW THE GAME TITLE
        x = 35;
        y = top_y + 320;
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("TETRIS", x + 15, y);
    }
}
