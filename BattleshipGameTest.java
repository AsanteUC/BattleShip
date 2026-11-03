import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BattleshipGameTest
{
    private BattleshipGame game;

    @BeforeEach
    void setUp()
    {
        game = new BattleshipGame(new Random(42));
    }

    private List<int[]> waterCells()
    {
        List<int[]> water = new ArrayList<>();
        BattleshipBoard board = game.getBoard();
        for (int r = 0; r < BattleshipBoard.SIZE; r++)
        {
            for (int c = 0; c < BattleshipBoard.SIZE; c++)
            {
                if (board.getShipAt(r, c) == null)
                {
                    water.add(new int[] { r, c });
                }
            }
        }
        return water;
    }

    private int[] firstShipCell()
    {
        return game.getBoard().getShips().get(0).getCells().get(0);
    }

    @Test
    void startsWithAllCountersAtZero()
    {
        assertEquals(0, game.getMissCount());
        assertEquals(0, game.getStrikeCount());
        assertEquals(0, game.getTotalMisses());
        assertEquals(0, game.getTotalHits());
        assertEquals(GameStatus.PLAYING, game.getStatus());
    }

    @Test
    void missRaisesBothMissCounters()
    {
        int[] w = waterCells().get(0);
        assertEquals(ShotResult.MISS, game.fire(w[0], w[1]));
        assertEquals(1, game.getMissCount());
        assertEquals(1, game.getTotalMisses());
    }

    @Test
    void hitRaisesTotalHitsAndResetsMissCounter()
    {
        List<int[]> water = waterCells();
        game.fire(water.get(0)[0], water.get(0)[1]);
        game.fire(water.get(1)[0], water.get(1)[1]);
        game.fire(water.get(2)[0], water.get(2)[1]);
        assertEquals(3, game.getMissCount());

        int[] s = firstShipCell();
        assertEquals(ShotResult.HIT, game.fire(s[0], s[1]));
        assertEquals(1, game.getTotalHits());
        assertEquals(0, game.getMissCount());
        assertEquals(3, game.getTotalMisses());
    }

    @Test
    void fiveMissesInARowGiveOneStrike()
    {
        List<int[]> water = waterCells();
        for (int i = 0; i < 5; i++)
        {
            game.fire(water.get(i)[0], water.get(i)[1]);
        }
        assertEquals(1, game.getStrikeCount());
        assertEquals(0, game.getMissCount());
        assertEquals(5, game.getTotalMisses());
        assertEquals(GameStatus.PLAYING, game.getStatus());
    }

    @Test
    void aHitInTheMiddleStopsAStrike()
    {
        List<int[]> water = waterCells();
        for (int i = 0; i < 4; i++)
        {
            game.fire(water.get(i)[0], water.get(i)[1]);
        }
        int[] s = firstShipCell();
        game.fire(s[0], s[1]);
        game.fire(water.get(4)[0], water.get(4)[1]);
        assertEquals(0, game.getStrikeCount());
        assertEquals(1, game.getMissCount());
    }

    @Test
    void threeStrikesLoseTheGame()
    {
        List<int[]> water = waterCells();
        for (int i = 0; i < 14; i++)
        {
            game.fire(water.get(i)[0], water.get(i)[1]);
        }
        assertEquals(GameStatus.PLAYING, game.getStatus());
        game.fire(water.get(14)[0], water.get(14)[1]);
        assertEquals(3, game.getStrikeCount());
        assertEquals(GameStatus.LOST, game.getStatus());
    }

    @Test
    void repeatedClickOnTheSameCellDoesNotCount()
    {
        int[] w = waterCells().get(0);
        game.fire(w[0], w[1]);
        assertEquals(ShotResult.ALREADY_FIRED, game.fire(w[0], w[1]));
        assertEquals(1, game.getTotalMisses());
        assertEquals(1, game.getMissCount());
    }

    @Test
    void sinkingAShipReportsIt()
    {
        Ship destroyer = game.getBoard().getShips().get(4);
        List<int[]> cells = destroyer.getCells();
        game.fire(cells.get(0)[0], cells.get(0)[1]);
        assertNull(game.getLastSunkShip());
        assertEquals(ShotResult.SUNK, game.fire(cells.get(1)[0], cells.get(1)[1]));
        assertNotNull(game.getLastSunkShip());
        assertEquals("Destroyer", game.getLastSunkShip().getName());
        assertEquals(GameStatus.PLAYING, game.getStatus());
    }

    @Test
    void sinkingAllShipsWinsTheGame()
    {
        for (Ship ship : game.getBoard().getShips())
        {
            for (int[] cell : ship.getCells())
            {
                game.fire(cell[0], cell[1]);
            }
        }
        assertEquals(GameStatus.WON, game.getStatus());
        assertEquals(17, game.getTotalHits());
        assertEquals(0, game.getStrikeCount());
    }

    @Test
    void shotsAfterTheGameIsOverAreIgnored()
    {
        List<int[]> water = waterCells();
        for (int i = 0; i < 15; i++)
        {
            game.fire(water.get(i)[0], water.get(i)[1]);
        }
        assertEquals(GameStatus.LOST, game.getStatus());
        assertEquals(ShotResult.ALREADY_FIRED, game.fire(water.get(15)[0], water.get(15)[1]));
        assertEquals(15, game.getTotalMisses());
    }

    @Test
    void newGameResetsEverything()
    {
        List<int[]> water = waterCells();
        for (int i = 0; i < 7; i++)
        {
            game.fire(water.get(i)[0], water.get(i)[1]);
        }
        game.newGame();
        assertEquals(0, game.getMissCount());
        assertEquals(0, game.getStrikeCount());
        assertEquals(0, game.getTotalMisses());
        assertEquals(0, game.getTotalHits());
        assertEquals(GameStatus.PLAYING, game.getStatus());
        assertEquals(CellState.BLANK, game.getCellState(water.get(0)[0], water.get(0)[1]));
    }
}
