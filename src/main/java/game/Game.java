package game;

import game.utils.Direction;
import game.utils.ImageFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game extends JPanel implements ActionListener {
    private static final int TILE_SIZE = Position.PLAYER_SIZE;
    private static final int ALL_TILES = (Position.GAME_DIMENSIONS_X * Position.GAME_DIMENSIONS_Y) / (TILE_SIZE * TILE_SIZE);
    private static final int DELAY = 100;
    private static final ImageFactory gameUtil = new ImageFactory();
    private transient Position[] cleanSnakePosition = new Position[ALL_TILES];
    private transient Position[] snakePosition = new Position[ALL_TILES];
    private transient Position foodPosition;
    private Direction currentDirection = Direction.RIGHT;
    private int snakeLength = 3;
    private boolean runningGame = false;
    private Timer timer;

    public Game(){
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(Position.GAME_DIMENSIONS_X, Position.GAME_DIMENSIONS_Y));
        // we have KeyListener to listen in on keys pressed
        addKeyListener(new KeyListener());
        initializeGame();
    }

    private void initializeGame() {
        for (int i = 0; i < ALL_TILES; i++) {
            snakePosition[i] = new Position(50 - i * TILE_SIZE, 50);
        }

        placeFood();
        // timer allows action events to take place.
        // That is some timer needs to go off for listeners to
        // actually listen
        timer = new Timer(DELAY, this);
        timer.start();
        runningGame = true;
    }
    private void restartGame() {
        cleanSnakePosition = new Position[ALL_TILES];
        for (int i = 0; i < ALL_TILES; i++) {
            cleanSnakePosition[i] = new Position(50 - i * TILE_SIZE, 50);
        }

        snakePosition = cleanSnakePosition.clone();
        snakeLength = 3;
        currentDirection = Direction.RIGHT;
        runningGame = true;
        placeFood();
        timer.start();
    }




    // -- AT EVERY FRAME -- //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (runningGame) {
            checkFood();
            checkCollision();
            move();
        }
        // This calls on paintComponent every frame
        repaint();
    }

    // Occurs at each frame. ok technically actionPerformed that
    // occurs at every frame but calls repaint which calls this. so
    // actionPerformed -> repaint -> paintcomponent
    @Override
    protected void paintComponent(Graphics g) {
        // g allows us to draw on the game
        super.paintComponent(g);
        if (runningGame) {
            // drawing
            drawBorder(g);
            drawFood(g);
            drawSnake(g);
            drawScore(g);
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }
    // ----------------- //


    //-- DRAWING --
    private void drawFood(Graphics g) {
        g.drawImage(gameUtil.imageTypeFactory("food"), foodPosition.getX(), foodPosition.getY(), this);
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < snakeLength; i++) {
            g.drawImage(gameUtil.imageTypeFactory(i == 0 ? "head" : "tail"), snakePosition[i].getX(), snakePosition[i].getY(), this);
        }
    }
    private void drawBorder(Graphics g) {
        g.setColor(Color.WHITE);
        int borderSize = 1;
        g.fillRect(0, 0, borderSize, Position.GAME_DIMENSIONS_Y); // Left border
        g.fillRect(0, 0, Position.GAME_DIMENSIONS_X, borderSize); // Top border
        g.fillRect(Position.GAME_DIMENSIONS_X - borderSize, 0, borderSize, Position.GAME_DIMENSIONS_Y); // Right border
        g.fillRect(0, Position.GAME_DIMENSIONS_Y - borderSize, Position.GAME_DIMENSIONS_X, borderSize); // Bottom border
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Score: " + snakeLength, 10, 20);
    }
    // ----------------- //


    private void gameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.red);
        g.setFont(small);
        g.drawString(msg, (Position.GAME_DIMENSIONS_X - metr.stringWidth(msg)) / 2, Position.GAME_DIMENSIONS_Y / 2);
    }

    private void move() {
        for (int i = snakeLength - 1; i > 0; i--) {
            // given: 1 2 3 4 5. 5 grabs the position of 4, 4 grabs position of 3... up until 1
            snakePosition[i] = new Position(snakePosition[i - 1].getX(), snakePosition[i - 1].getY());
        }

        // changing directions
        switch (currentDirection) {
            case LEFT:
                snakePosition[0] = new Position(snakePosition[0].getX() - TILE_SIZE, snakePosition[0].getY());
                break;
            case RIGHT:
                snakePosition[0] = new Position(snakePosition[0].getX() + TILE_SIZE, snakePosition[0].getY());
                break;
            case UP:
                snakePosition[0] = new Position(snakePosition[0].getX(), snakePosition[0].getY() - TILE_SIZE);
                break;
            case DOWN:
                snakePosition[0] = new Position(snakePosition[0].getX(), snakePosition[0].getY() + TILE_SIZE);
                break;
        }
    }

    // -- GAME LOGIC -- //
    private void checkCollision() {
        for (int i = snakeLength; i > 0; i--) {
            if ((i > 4) && (snakePosition[0].getX() == snakePosition[i].getX()) && (snakePosition[0].getY() == snakePosition[i].getY())) {
                runningGame = false;
            }
        }
        if (!snakePosition[0].insideGameBounds()) {
            runningGame = false;
        }
        if (!runningGame) {
            timer.stop();
        }
    }

    private void checkFood() {
        // see if head hits food,
        if (snakePosition[0].getX() == foodPosition.getX() && snakePosition[0].getY() == foodPosition.getY()) {
            snakeLength++;
            placeFood();
        }
    }
    private void placeFood() {
        int x = (int) (Math.random() * (Position.GAME_DIMENSIONS_X / TILE_SIZE)) * TILE_SIZE;
        int y = (int) (Math.random() * (Position.GAME_DIMENSIONS_Y / TILE_SIZE)) * TILE_SIZE;

        for (int i = 0; i < snakeLength; i++){
            if (snakePosition[i].getX() == x && snakePosition[i].getY() == y){
                placeFood();
            }
        }

        foodPosition = new Position(x, y);
    }
    // --------------- //











    // AGENT FUNCTIONS //
    public double[] getState() {
        double[] state = new double[11];

        // Snake's current direction (one-hot encoded)
        state[0] = (currentDirection == Direction.LEFT) ? 1 : 0;
        state[1] = (currentDirection == Direction.RIGHT) ? 1 : 0;
        state[2] = (currentDirection == Direction.UP) ? 1 : 0;
        state[3] = (currentDirection == Direction.DOWN) ? 1 : 0;

        // Food position relative to snake's head
        state[4] = (foodPosition.getX() - snakePosition[0].getX()) / (double) Position.GAME_DIMENSIONS_X;
        state[5] = (foodPosition.getY() - snakePosition[0].getY()) / (double) Position.GAME_DIMENSIONS_Y;

        // Danger straight
        state[6] = isDangerStraight() ? 1 : 0;

        // Danger right
        state[7] = isDangerRight() ? 1 : 0;

        // Danger left
        state[8] = isDangerLeft() ? 1 : 0;

        // Snake's head position
        state[9] = snakePosition[0].getX() / (double) Position.GAME_DIMENSIONS_X;
        state[10] = snakePosition[0].getY() / (double) Position.GAME_DIMENSIONS_Y;

        return state;
    }

    // Helper methods for danger detection
    private boolean isDangerStraight() {
        Position nextPos = getNextPosition(currentDirection);
        return !nextPos.insideGameBounds() || isSnakeBody(nextPos);
    }

    private boolean isDangerRight() {
        Direction rightDir = getDirectionRight(currentDirection);
        Position nextPos = getNextPosition(rightDir);
        return !nextPos.insideGameBounds() || isSnakeBody(nextPos);
    }

    private boolean isDangerLeft() {
        Direction leftDir = getDirectionLeft(currentDirection);
        Position nextPos = getNextPosition(leftDir);
        return !nextPos.insideGameBounds() || isSnakeBody(nextPos);
    }

    private Position getNextPosition(Direction dir) {
        int x = snakePosition[0].getX();
        int y = snakePosition[0].getY();
        switch (dir) {
            case LEFT: x -= TILE_SIZE; break;
            case RIGHT: x += TILE_SIZE; break;
            case UP: y -= TILE_SIZE; break;
            case DOWN: y += TILE_SIZE; break;
        }
        return new Position(x, y);
    }

    private boolean isSnakeBody(Position pos) {
        for (int i = 1; i < snakeLength; i++) {
            if (snakePosition[i].getX() == pos.getX() && snakePosition[i].getY() == pos.getY()) {
                return true;
            }
        }
        return false;
    }

    private Direction getDirectionRight(Direction dir) {
        switch (dir) {
            case UP: return Direction.RIGHT;
            case RIGHT: return Direction.DOWN;
            case DOWN: return Direction.LEFT;
            case LEFT: return Direction.UP;
            default: throw new IllegalStateException("Unexpected direction: " + dir);
        }
    }

    private Direction getDirectionLeft(Direction dir) {
        switch (dir) {
            case UP: return Direction.LEFT;
            case LEFT: return Direction.DOWN;
            case DOWN: return Direction.RIGHT;
            case RIGHT: return Direction.UP;
            default: throw new IllegalStateException("Unexpected direction: " + dir);
        }
    }

    // New method to perform an action based on the RL agent's decision
    public double performAction(int action) {
        Direction newDirection;
        switch (action) {
            case 0: newDirection = Direction.LEFT; break;
            case 1: newDirection = Direction.RIGHT; break;
            case 2: newDirection = Direction.UP; break;
            case 3: newDirection = Direction.DOWN; break;
            default: throw new IllegalArgumentException("Invalid action: " + action);
        }

        // Only change direction if it's not opposite to the current direction
        if ((newDirection != Direction.LEFT || currentDirection != Direction.RIGHT) &&
                (newDirection != Direction.RIGHT || currentDirection != Direction.LEFT) &&
                (newDirection != Direction.UP || currentDirection != Direction.DOWN) &&
                (newDirection != Direction.DOWN || currentDirection != Direction.UP)) {
            currentDirection = newDirection;
        }

        int oldScore = snakeLength;
        move();
        checkFood();
        checkCollision();

        // Calculate reward
        double reward;
        if (!runningGame) {
            reward = -10; // Penalty for game over
        } else if (snakeLength > oldScore) {
            reward = 50; // Reward for eating food
        } else {
            reward = -0.2; // Small penalty for each move to encourage efficiency
        }

        repaint(); // Update the game display
        return reward;
    }
    // New method to check if the game is over
    public boolean isGameOver() {
        return !runningGame;
    }

    // New method to get the current score
    public int getScore() {
        return snakeLength;
    }

    // Modify the restart method to be public
    public void reset() {
        restartGame();
    }


    // -- KEYS -- //
    private class KeyListener extends KeyAdapter {
        // cannot put in own class file because
        // current direction cannot be passed in when overriding method
        // parameters needs to be the same
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (currentDirection != Direction.RIGHT)) {
                currentDirection = Direction.LEFT;
            }

            if ((key == KeyEvent.VK_RIGHT) && (currentDirection != Direction.LEFT)) {
                currentDirection = Direction.RIGHT;
            }

            if ((key == KeyEvent.VK_UP) && (currentDirection != Direction.DOWN)) {
                currentDirection = Direction.UP;
            }

            if ((key == KeyEvent.VK_DOWN) && (currentDirection != Direction.UP)) {
                currentDirection = Direction.DOWN;
            }
            if ((key == KeyEvent.VK_ENTER) && !runningGame) {
                restartGame();
            }
        }
    }
}
