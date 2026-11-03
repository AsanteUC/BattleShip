import java.util.ArrayList;
import java.util.List;

/**
 * One ship: a name, a size, the cells it occupies and how many times it has
 * been hit.
 *
 * @author Asante
 * @version 1.0
 */
public class Ship
{
    private final String name;
    private final int size;
    private final List<int[]> cells = new ArrayList<>();
    private int hits = 0;

    /**
     * Creates a ship that has not been placed yet.
     *
     * @param name the ship's name
     * @param size the number of cells it occupies
     */
    public Ship(String name, int size)
    {
        this.name = name;
        this.size = size;
    }

    public String getName()
    {
        return name;
    }

    public int getSize()
    {
        return size;
    }

    /**
     * Records a cell the ship occupies.
     *
     * @param row the row
     * @param col the column
     */
    public void addCell(int row, int col)
    {
        cells.add(new int[] { row, col });
    }

    /**
     * Returns the cells the ship occupies, each as {row, col}.
     *
     * @return the ship's cells
     */
    public List<int[]> getCells()
    {
        return cells;
    }

    /**
     * Records one hit on the ship.
     */
    public void registerHit()
    {
        if (hits < size)
        {
            hits++;
        }
    }

    public int getHits()
    {
        return hits;
    }

    /**
     * Tells whether every cell of the ship has been hit.
     *
     * @return true if the ship is sunk
     */
    public boolean isSunk()
    {
        return hits >= size;
    }
}
