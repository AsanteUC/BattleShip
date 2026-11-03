import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleshipBoardTest
{
    private static int countShipCells(BattleshipBoard board)
    {
        int count = 0;
        for (int r = 0; r < BattleshipBoard.SIZE; r++)
        {
            for (int c = 0; c < BattleshipBoard.SIZE; c++)
            {
                if (board.getShipAt(r, c) != null)
                {
                    count++;
                }
            }
        }
        return count;
    }

    private static int[] findWater(BattleshipBoard board)
    {
        for (int r = 0; r < BattleshipBoard.SIZE; r++)
        {
            for (int c = 0; c < BattleshipBoard.SIZE; c++)
            {
                if (board.getShipAt(r, c) == null)
                {
                    return new int[] { r, c };
                }
            }
        }
        throw new IllegalStateException("no water");
    }

    @Test
    void newBoardIsAllBlank()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(1));
        for (int r = 0; r < BattleshipBoard.SIZE; r++)
        {
            for (int c = 0; c < BattleshipBoard.SIZE; c++)
            {
                assertEquals(CellState.BLANK, board.getCellState(r, c));
            }
        }
    }

    @Test
    void fleetHasFiveShipsOfTheRightSizes()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(1));
        List<Ship> ships = board.getShips();
        assertEquals(5, ships.size());
        int[] expected = { 5, 4, 3, 3, 2 };
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], ships.get(i).getSize());
        }
    }

    @Test
    void shipsNeverOverlapAndAlwaysCover17Cells()
    {
        for (int seed = 0; seed < 300; seed++)
        {
            BattleshipBoard board = new BattleshipBoard(new Random(seed));
            assertEquals(BattleshipBoard.TOTAL_SHIP_CELLS, countShipCells(board), "seed " + seed);
        }
    }

    @Test
    void everyShipIsAStraightUnbrokenLine()
    {
        for (int seed = 0; seed < 100; seed++)
        {
            BattleshipBoard board = new BattleshipBoard(new Random(seed));
            for (Ship ship : board.getShips())
            {
                List<int[]> cells = ship.getCells();
                assertEquals(ship.getSize(), cells.size());

                boolean sameRow = true;
                boolean sameCol = true;
                int minRow = Integer.MAX_VALUE, maxRow = -1, minCol = Integer.MAX_VALUE, maxCol = -1;
                for (int[] cell : cells)
                {
                    sameRow &= cell[0] == cells.get(0)[0];
                    sameCol &= cell[1] == cells.get(0)[1];
                    minRow = Math.min(minRow, cell[0]);
                    maxRow = Math.max(maxRow, cell[0]);
                    minCol = Math.min(minCol, cell[1]);
                    maxCol = Math.max(maxCol, cell[1]);
                }
                assertTrue(sameRow || sameCol, "ship is not straight, seed " + seed);
                assertEquals(ship.getSize() - 1, (maxRow - minRow) + (maxCol - minCol), "ship has a gap, seed " + seed);
                assertTrue(minRow >= 0 && maxRow < BattleshipBoard.SIZE && minCol >= 0 && maxCol < BattleshipBoard.SIZE);
            }
        }
    }

    @Test
    void firingAtWaterIsAMiss()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(2));
        int[] water = findWater(board);
        assertEquals(ShotResult.MISS, board.fire(water[0], water[1]));
        assertEquals(CellState.MISS, board.getCellState(water[0], water[1]));
    }

    @Test
    void firingAtAShipIsAHit()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(2));
        Ship carrier = board.getShips().get(0);
        int[] cell = carrier.getCells().get(0);
        assertEquals(ShotResult.HIT, board.fire(cell[0], cell[1]));
        assertEquals(CellState.HIT, board.getCellState(cell[0], cell[1]));
    }

    @Test
    void firingTwiceAtTheSameCellIsIgnored()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(3));
        int[] water = findWater(board);
        board.fire(water[0], water[1]);
        assertEquals(ShotResult.ALREADY_FIRED, board.fire(water[0], water[1]));
    }

    @Test
    void lastHitOnAShipSinksIt()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(4));
        Ship destroyer = board.getShips().get(4);
        List<int[]> cells = destroyer.getCells();
        assertEquals(ShotResult.HIT, board.fire(cells.get(0)[0], cells.get(0)[1]));
        assertFalse(destroyer.isSunk());
        assertEquals(ShotResult.SUNK, board.fire(cells.get(1)[0], cells.get(1)[1]));
        assertTrue(destroyer.isSunk());
        assertFalse(board.allShipsSunk());
    }

    @Test
    void allShipsSunkAfterEveryShipCellIsHit()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(5));
        for (Ship ship : board.getShips())
        {
            for (int[] cell : ship.getCells())
            {
                board.fire(cell[0], cell[1]);
            }
        }
        assertTrue(board.allShipsSunk());
    }

    @Test
    void newGameClearsTheBoardAndReplacesTheShips()
    {
        BattleshipBoard board = new BattleshipBoard(new Random(6));
        int[] water = findWater(board);
        board.fire(water[0], water[1]);
        board.newGame();
        assertEquals(CellState.BLANK, board.getCellState(water[0], water[1]));
        assertEquals(5, board.getShips().size());
        assertEquals(BattleshipBoard.TOTAL_SHIP_CELLS, countShipCells(board));
        assertNotNull(board.getShips().get(0));
    }
}
