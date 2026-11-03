/**
 * The outcome of firing at a cell.
 *
 * @author Student
 * @version 1.0
 */
public enum ShotResult
{
    /** The cell was fired at before (or the game is over); nothing changed. */
    ALREADY_FIRED,
    /** The shot hit only water. */
    MISS,
    /** The shot hit a ship that is still afloat. */
    HIT,
    /** The shot hit the last unhit cell of a ship, which is now sunk. */
    SUNK
}
