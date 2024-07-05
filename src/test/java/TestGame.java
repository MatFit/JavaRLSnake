import game.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestGame {
    private Game game = new Game();
    @Test
    public void testCreation(){
        assertNotNull(game);
    }
}
