import javax.swing.JButton;

/**
 * A button for one cell of the Battleship board. It remembers its row and
 * column so the shared listener can tell which cell was clicked.
 *
 * @author Asante
 * @version 1.0
 */
public class BattleshipCellButton extends JButton
{
    private final int row;
    private final int col;

    /**
     * Creates the button for a cell.
     *
     * @param row the row (0-9)
     * @param col the column (0-9)
     */
    public BattleshipCellButton(int row, int col)
    {
        super();
        this.row = row;
        this.col = col;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }
}
