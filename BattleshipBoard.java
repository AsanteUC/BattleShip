import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The 10 x 10 Battleship board. It places the five ships at random, keeps
 * track of which cells have been fired at, and decides whether a shot is a
 * miss, a hit or sinks a ship. It has no GUI code.
 *
 * @author Student
 * @version 1.0
 */
public class BattleshipBoard
{
    public static final int SIZE = 10;

    // Ships in descending size order, which makes placement more efficient
    private static final String[] SHIP_NAMES = { "Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer" };
    private static final int[] SHIP_SIZES = { 5, 4, 3, 3, 2 };

    /** Total number of ship cells on the board (17). */
    public static final int TOTAL_SHIP_CELLS = 17;

    private final Random rng;
    private final CellState[][] cells = new CellState[SIZE][SIZE];
    private final Ship[][] shipAt = new Ship[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();

    /**
     * Creates a board with the ships placed at random.
     */
    public BattleshipBoard()
    {
        this(new Random());
    }

    /**
     * Creates a board using the given random number generator (a seeded
     * generator makes the placement repeatable for testing).
     *
     * @param rng the random number generator
     */
    public BattleshipBoard(Random rng)
    {
        this.rng = rng;
        newGame();
    }

    /**
     * Clears the board and places a new set of ships at random.
     */
    public final void newGame()
    {
        ships.clear();
        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                cells[row][col] = CellState.BLANK;
                shipAt[row][col] = null;
            }
        }
        for (int i = 0; i < SHIP_SIZES.length; i++)
        {
            Ship ship = new Ship(SHIP_NAMES[i], SHIP_SIZES[i]);
            placeShip(ship);
            ships.add(ship);
        }
    }

    /**
     * Places one ship: pick a random direction and a random row or column,
     * scan that line for runs of free cells long enough for the ship, and
     * pick one of those runs at random. If the line has no room, try again.
     */
    private void placeShip(Ship ship)
    {
        boolean placed = false;
        while (!placed)
        {
            boolean horizontal = rng.nextBoolean();
            int line = rng.nextInt(SIZE);

            List<Integer> starts = new ArrayList<>();
            for (int start = 0; start <= SIZE - ship.getSize(); start++)
            {
                if (runIsFree(horizontal, line, start, ship.getSize()))
                {
                    starts.add(start);
                }
            }

            if (!starts.isEmpty())
            {
                int start = starts.get(rng.nextInt(starts.size()));
                for (int i = 0; i < ship.getSize(); i++)
                {
                    int row = horizontal ? line : start + i;
                    int col = horizontal ? start + i : line;
                    shipAt[row][col] = ship;
                    ship.addCell(row, col);
                }
                placed = true;
            }
        }
    }

    private boolean runIsFree(boolean horizontal, int line, int start, int length)
    {
        for (int i = 0; i < length; i++)
        {
            int row = horizontal ? line : start + i;
            int col = horizontal ? start + i : line;
            if (shipAt[row][col] != null)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Fires at a cell. A cell that was already fired at is left alone.
     *
     * @param row the row (0-9)
     * @param col the column (0-9)
     * @return ALREADY_FIRED, MISS, HIT or SUNK
     */
    public ShotResult fire(int row, int col)
    {
        if (cells[row][col] != CellState.BLANK)
        {
            return ShotResult.ALREADY_FIRED;
        }

        Ship ship = shipAt[row][col];
        if (ship == null)
        {
            cells[row][col] = CellState.MISS;
            return ShotResult.MISS;
        }

        cells[row][col] = CellState.HIT;
        ship.registerHit();
        return ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
    }

    public CellState getCellState(int row, int col)
    {
        return cells[row][col];
    }

    /**
     * Returns the ship covering a cell.
     *
     * @param row the row
     * @param col the column
     * @return the ship, or null if the cell is water
     */
    public Ship getShipAt(int row, int col)
    {
        return shipAt[row][col];
    }

    public List<Ship> getShips()
    {
        return ships;
    }

    /**
     * Tells whether every ship has been sunk.
     *
     * @return true if all ships are sunk
     */
    public boolean allShipsSunk()
    {
        for (Ship ship : ships)
        {
            if (!ship.isSunk())
            {
                return false;
            }
        }
        return true;
    }
}
