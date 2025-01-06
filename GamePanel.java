import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;
import java.io.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 650;
    static final int GAME_SCREEN_HEIGHT = 600;
    static final int SCORE_PANEL_HEIGHT = 50;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * GAME_SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean idle = true;
    boolean difficultySet = false;
    Timer timer;
    Random random;
    Font customFont;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.addMouseListener(new MyMouseAdapter());
        loadCustomFont();
        initializeSnake();
    }

    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("game_over.ttf")).deriveFont(30f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Times New Roman", Font.BOLD, 30);
        }
    }

    private void initializeSnake() {
        x[0] = UNIT_SIZE * 4;
        y[0] = (GAME_SCREEN_HEIGHT / 2 / UNIT_SIZE) * UNIT_SIZE;
    }

    public void startGame() {
        idle = true;
        difficultySet = false;
        running = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (idle && !difficultySet) {
            g.setColor(Color.white);
            g.setFont(customFont.deriveFont(40f));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Press 1 for Easy", (SCREEN_WIDTH - metrics.stringWidth("Press 1 for Easy")) / 2,
                    SCREEN_HEIGHT / 2 - 50);
            g.drawString("Press 2 for Medium", (SCREEN_WIDTH - metrics.stringWidth("Press 2 for Medium")) / 2,
                    SCREEN_HEIGHT / 2);
            g.drawString("Press 3 for Hard", (SCREEN_WIDTH - metrics.stringWidth("Press 3 for Hard")) / 2,
                    SCREEN_HEIGHT / 2 + 50);
            newApple();

        } else if (idle && difficultySet) {
            g.setColor(Color.white);
            g.setFont(customFont.deriveFont(40f));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Press any key to start", (SCREEN_WIDTH - metrics.stringWidth("Press any key to start")) / 2,
                    SCREEN_HEIGHT / 2);
        } else if (running) {
            g.setColor(Color.darkGray);
            g.fillRect(0, 0, SCREEN_WIDTH, SCORE_PANEL_HEIGHT);

            g.setColor(Color.white);
            g.setFont(customFont);
            g.drawString("Score: " + applesEaten, SCREEN_WIDTH / 2, SCORE_PANEL_HEIGHT / 2);

            for (int i = 0; i <= GAME_SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(0, SCORE_PANEL_HEIGHT + i * UNIT_SIZE, SCREEN_WIDTH, SCORE_PANEL_HEIGHT + i * UNIT_SIZE);
                g.drawLine(i * UNIT_SIZE, SCORE_PANEL_HEIGHT, i * UNIT_SIZE, GAME_SCREEN_HEIGHT + 50);
            }

            g.setColor(Color.red);
            g.fillOval(appleX, appleY + SCORE_PANEL_HEIGHT, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i] + SCORE_PANEL_HEIGHT, UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i] + SCORE_PANEL_HEIGHT, UNIT_SIZE, UNIT_SIZE);
                }
            }
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) ((GAME_SCREEN_HEIGHT) / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void setDifficulty(int difficulty) {
        if (timer == null) {
            timer = new Timer(DELAY, this);
            timer.start();
        }

        switch (difficulty) {
            case 1 -> timer.setDelay(90);
            case 2 -> timer.setDelay(75);
            case 3 -> timer.setDelay(60);
        }
        difficultySet = true;
        repaint();
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= GAME_SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(customFont.deriveFont(75f));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, (GAME_SCREEN_HEIGHT / 2));
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                GAME_SCREEN_HEIGHT / 2 + 75);
        g.drawString("Press ENTER to Play Again", (SCREEN_WIDTH - metrics.stringWidth("Press ENTER to Play Again")) / 2,
                GAME_SCREEN_HEIGHT / 2 - 75);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (idle && !difficultySet) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1 -> setDifficulty(1);
                    case KeyEvent.VK_2 -> setDifficulty(2);
                    case KeyEvent.VK_3 -> setDifficulty(3);
                }
                return;
            }

            if (idle && difficultySet) {
                idle = false;
                running = true;
                return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                    if (direction != 'R')
                        direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                    if (direction != 'L')
                        direction = 'R';
                    break;
                case KeyEvent.VK_UP, KeyEvent.VK_W:
                    if (direction != 'D')
                        direction = 'U';
                    break;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                    if (direction != 'U')
                        direction = 'D';
                    break;
            }
        }
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (idle) {
                idle = false;
                running = true;
            }
        }
    }
}
