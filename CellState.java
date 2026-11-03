/**
 * What the player can see in one cell of the board.
 *
 * @author Student
 * @version 1.0
 */
public enum CellState
{
    /** Not fired at yet. */
    BLANK,
    /** Fired at; there was no ship. */
    MISS,
    /** Fired at; a ship was hit. */
    HIT
}
