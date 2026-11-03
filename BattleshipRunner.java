import javax.swing.SwingUtilities;

/**
 * Starts the Battleship game.
 *
 * @author Student
 * @version 1.0
 */
public class BattleshipRunner
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            BattleshipFrame frame = new BattleshipFrame();
            frame.setVisible(true);
        });
    }
}
