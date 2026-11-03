import java.util.Random;

/**
 * The Battleship rules and scoring: the miss, strike, total miss and total hit
 * counters, and when the player wins or loses. It has no GUI code.
 * <p>
 * Five misses in a row earn a strike (and reset the miss counter); a hit
 * resets the miss counter. Three strikes lose the game; sinking all five
 * ships wins it.
 *
 * @author Asante
 * @version 1.0
 */
public class BattleshipGame
{
    public static final int MAX_MISSES = 5;
    public static final int MAX_STRIKES = 3;

    private final BattleshipBoard board;

    private int missCount = 0;
    private int strikeCount = 0;
    private int totalMisses = 0;
    private int totalHits = 0;
    private GameStatus status = GameStatus.PLAYING;
    private Ship lastSunkShip = null;

    /**
     * Creates a game with a randomly placed fleet.
     */
    public BattleshipGame()
    {
        this(new Random());
    }

    /**
     * Creates a game using the given random number generator.
     *
     * @param rng the random number generator used to place the ships
     */
    public BattleshipGame(Random rng)
    {
        board = new BattleshipBoard(rng);
    }

    /**
     * Fires at a cell and updates the counters. Shots at a cell that was
     * already fired at, and shots after the game is over, change nothing.
     *
     * @param row the row (0-9)
     * @param col the column (0-9)
     * @return ALREADY_FIRED, MISS, HIT or SUNK
     */
    public ShotResult fire(int row, int col)
    {
        if (status != GameStatus.PLAYING)
        {
            return ShotResult.ALREADY_FIRED;
        }

        ShotResult result = board.fire(row, col);
        lastSunkShip = null;

        switch (result)
        {
            case MISS:
                missCount++;
                totalMisses++;
                if (missCount == MAX_MISSES)
                {
                    strikeCount++;
                    missCount = 0;
                    if (strikeCount == MAX_STRIKES)
                    {
                        status = GameStatus.LOST;
                    }
                }
                break;
            case HIT:
                totalHits++;
                missCount = 0;
                break;
            case SUNK:
                totalHits++;
                missCount = 0;
                lastSunkShip = board.getShipAt(row, col);
                if (board.allShipsSunk())
                {
                    status = GameStatus.WON;
                }
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Starts a new game: new random ships and all counters back to zero.
     */
    public void newGame()
    {
        board.newGame();
        missCount = 0;
        strikeCount = 0;
        totalMisses = 0;
        totalHits = 0;
        status = GameStatus.PLAYING;
        lastSunkShip = null;
    }

    public int getMissCount()
    {
        return missCount;
    }

    public int getStrikeCount()
    {
        return strikeCount;
    }

    public int getTotalMisses()
    {
        return totalMisses;
    }

    public int getTotalHits()
    {
        return totalHits;
    }

    public GameStatus getStatus()
    {
        return status;
    }

    /**
     * Returns the ship sunk by the most recent shot.
     *
     * @return the ship, or null if the last shot did not sink one
     */
    public Ship getLastSunkShip()
    {
        return lastSunkShip;
    }

    public CellState getCellState(int row, int col)
    {
        return board.getCellState(row, col);
    }

    public BattleshipBoard getBoard()
    {
        return board;
    }
}
