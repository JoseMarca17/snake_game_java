import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH + SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;

    Random random = new Random();

    // Serpent
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;

    // Check panel
    boolean gameStarted = false;

    GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void draw(Graphics g) {
        if (!gameStarted) {
            drawStartScreen(g);
        } else if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            //APPLE
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 100, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Show points while playing
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            g.drawString("Puntaje: " + applesEaten, 10, 25);
        } else {
            gameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollision() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] < 0) running = false;
        if (x[0] >= SCREEN_WIDTH) running = false;
        if (y[0] < 0) running = false;
        if (y[0] >= SCREEN_HEIGHT) running = false;

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Puntaje: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Puntaje: " + applesEaten))
                / 2, g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Restart Game
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.setColor(Color.white);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Presiona R para reiniciar", (SCREEN_WIDTH - metrics.stringWidth("Presiona R para reiniciar")) / 2,
                SCREEN_HEIGHT / 2 + 150);
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if(!gameStarted){
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    gameStarted = true;
                }
            }

            if(!running && e.getKeyCode() == KeyEvent.VK_R){
                restartGame();
            }
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }

    // Start Screen
    public void drawStartScreen(Graphics g){
        g.setColor(Color.green);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("SNAKE GAME", (SCREEN_WIDTH - metrics.stringWidth("SNAKE GAME")) / 2, SCREEN_HEIGHT / 2);

        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.setColor(Color.white);
        FontMetrics metricsStart = getFontMetrics(g.getFont());
        g.drawString("Presiona ESPACIO para iniciar", (SCREEN_WIDTH - metricsStart.stringWidth("Presiona ESPACIO para iniciar"))
                / 2, SCREEN_HEIGHT / 2 + 100);
    }

    // Restrart game method
    public void restartGame(){
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        for(int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
    }
}