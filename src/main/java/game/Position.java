package game;

public class Position {
    private final int x;
    private final int y;
    public static final int GAME_DIMENSIONS_X = 300;
    public static final int GAME_DIMENSIONS_Y = 300;
    public static final int PLAYER_SIZE = 10;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean insideGameBounds(){
        return (x < GAME_DIMENSIONS_X) && (y < GAME_DIMENSIONS_Y) && (x >= 0) && (y >= 0);
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

}
