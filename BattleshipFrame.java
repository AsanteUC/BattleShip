import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * The Battleship window (the view): a title, the status counters, a 10 x 10
 * grid of cell buttons, and the Play Again and Quit buttons. Every click is
 * sent to the BattleshipGame, which decides what it means.
 * <p>
 * The cell pictures are drawn in code, so no image files are needed.
 *
 * @author Student
 * @version 1.0
 */
public class BattleshipFrame extends JFrame
{
    private static final int CELL_PIXELS = 44;

    private final BattleshipGame game = new BattleshipGame();
    private final BattleshipCellButton[][] cells =
            new BattleshipCellButton[BattleshipBoard.SIZE][BattleshipBoard.SIZE];

    private final ImageIcon blankIcon = createIcon("~", new Color(173, 216, 230), new Color(30, 110, 180));
    private final ImageIcon missIcon = createIcon("M", new Color(40, 90, 160), Color.YELLOW);
    private final ImageIcon hitIcon = createIcon("X", Color.WHITE, Color.RED);

    private JLabel missLabel;
    private JLabel strikeLabel;
    private JLabel totalMissLabel;
    private JLabel totalHitLabel;

    private final Font titleFont = new Font("SansSerif", Font.BOLD, 30);
    private final Font statFont = new Font("SansSerif", Font.PLAIN, 15);
    private final Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

    /**
     * Builds the game window.
     */
    public BattleshipFrame()
    {
        super("Battleship");
        setLayout(new BorderLayout());

        JPanel north = new JPanel(new BorderLayout());
        north.add(createTitleLabel(), BorderLayout.NORTH);
        north.add(createStatusPanel(), BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(createBoardPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshAll();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JLabel createTitleLabel()
    {
        JLabel title = new JLabel("Battleship", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        return title;
    }

    private JPanel createStatusPanel()
    {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15),
                BorderFactory.createTitledBorder("Status")));

        missLabel = createStatLabel();
        strikeLabel = createStatLabel();
        totalMissLabel = createStatLabel();
        totalHitLabel = createStatLabel();

        panel.add(missLabel);
        panel.add(strikeLabel);
        panel.add(totalMissLabel);
        panel.add(totalHitLabel);
        return panel;
    }

    private JLabel createStatLabel()
    {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(statFont);
        return label;
    }

    private JPanel createBoardPanel()
    {
        JPanel panel = new JPanel(new GridLayout(BattleshipBoard.SIZE, BattleshipBoard.SIZE, 1, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // One listener instance shared by all 100 cells
        ActionListener cellListener = ae ->
        {
            BattleshipCellButton cell = (BattleshipCellButton) ae.getSource();
            handleShot(cell.getRow(), cell.getCol());
        };

        for (int row = 0; row < BattleshipBoard.SIZE; row++)
        {
            for (int col = 0; col < BattleshipBoard.SIZE; col++)
            {
                BattleshipCellButton cell = new BattleshipCellButton(row, col);
                cell.setIcon(blankIcon);
                cell.setDisabledIcon(blankIcon);
                cell.setMargin(new java.awt.Insets(0, 0, 0, 0));
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                cell.setFocusPainted(false);
                cell.addActionListener(cellListener);
                cells[row][col] = cell;
                panel.add(cell);
            }
        }
        return panel;
    }

    private JPanel createButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(buttonFont);
        playAgainButton.addActionListener(ae -> playAgainClicked());

        JButton quitButton = new JButton("Quit");
        quitButton.setFont(buttonFont);
        quitButton.addActionListener(ae -> quitClicked());

        panel.add(playAgainButton);
        panel.add(quitButton);
        return panel;
    }

    /**
     * Sends a shot to the game and shows what happened.
     */
    private void handleShot(int row, int col)
    {
        ShotResult result = game.fire(row, col);
        if (result == ShotResult.ALREADY_FIRED)
        {
            return; // clicks on used cells do not count as moves
        }

        refreshCell(row, col);
        refreshStatus();

        if (result == ShotResult.SUNK)
        {
            JOptionPane.showMessageDialog(this,
                    "You sunk the " + game.getLastSunkShip().getName() + "!",
                    "Ship Sunk", JOptionPane.INFORMATION_MESSAGE);
        }

        if (game.getStatus() == GameStatus.WON)
        {
            disableAllCells();
            askReplay("You win! You sunk all five ships.", "You Win");
        }
        else if (game.getStatus() == GameStatus.LOST)
        {
            disableAllCells();
            askReplay("You lose! You received three strikes.", "You Lose");
        }
    }

    /**
     * Tells the player the game is over and asks whether to play again.
     */
    private void askReplay(String message, String title)
    {
        int choice = JOptionPane.showConfirmDialog(this,
                message + "\n\nWould you like to play again?",
                title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION)
        {
            startNewGame();
        }
        else
        {
            System.exit(0);
        }
    }

    private void playAgainClicked()
    {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to start a new game?",
                "Play Again", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION)
        {
            startNewGame();
        }
    }

    private void quitClicked()
    {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit?",
                "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }
    }

    private void startNewGame()
    {
        game.newGame();
        refreshAll();
    }

    private void refreshAll()
    {
        for (int row = 0; row < BattleshipBoard.SIZE; row++)
        {
            for (int col = 0; col < BattleshipBoard.SIZE; col++)
            {
                refreshCell(row, col);
                cells[row][col].setEnabled(true);
            }
        }
        refreshStatus();
    }

    /**
     * Shows a cell's state and disables it once it has been fired at.
     */
    private void refreshCell(int row, int col)
    {
        BattleshipCellButton cell = cells[row][col];
        CellState state = game.getCellState(row, col);

        ImageIcon icon;
        if (state == CellState.HIT)
        {
            icon = hitIcon;
        }
        else if (state == CellState.MISS)
        {
            icon = missIcon;
        }
        else
        {
            icon = blankIcon;
        }
        cell.setIcon(icon);
        cell.setDisabledIcon(icon);
        cell.setEnabled(state == CellState.BLANK);
    }

    private void disableAllCells()
    {
        for (int row = 0; row < BattleshipBoard.SIZE; row++)
        {
            for (int col = 0; col < BattleshipBoard.SIZE; col++)
            {
                cells[row][col].setEnabled(false);
            }
        }
    }

    private void refreshStatus()
    {
        missLabel.setText("Miss Counter: " + game.getMissCount() + " [0-" + BattleshipGame.MAX_MISSES + "]");
        strikeLabel.setText("Strike Counter: " + game.getStrikeCount() + " [0-" + BattleshipGame.MAX_STRIKES + "]");
        totalMissLabel.setText("Total Misses: " + game.getTotalMisses() + " [0-83]");
        totalHitLabel.setText("Total Hits: " + game.getTotalHits() + " [0-" + BattleshipBoard.TOTAL_SHIP_CELLS + "]");
    }

    /**
     * Draws a square picture with one centered symbol.
     */
    private static ImageIcon createIcon(String symbol, Color background, Color foreground)
    {
        BufferedImage image = new BufferedImage(CELL_PIXELS, CELL_PIXELS, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(background);
        g.fillRect(0, 0, CELL_PIXELS, CELL_PIXELS);
        g.setColor(foreground);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int x = (CELL_PIXELS - fm.stringWidth(symbol)) / 2;
        int y = (CELL_PIXELS - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(symbol, x, y);
        g.dispose();
        return new ImageIcon(image);
    }
}
