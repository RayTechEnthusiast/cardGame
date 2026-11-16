package com.cardGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

/** Displays Black Mench and handles input. Solitaire keeps all the game rules. */
public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private final Solitaire game;
    JPanel dealerPane;
    JPanel playerPane;
    private JLabel deckImage;
    private JLabel playerScore;
    private JLabel dealerScore;
    private JLabel tokenCount;
    private JLabel currentBet;
    private JLabel resultLabel;
    private JLabel menchLabel;
    private JButton bet5, bet15, bet30, betAll, hitButton, standButton, nextButton, howToButton;
    private boolean endScreenShown;

    private static final Color CYAN = new Color(107, 223, 218);
    private static final Color GOLD = new Color(235, 199, 127);
    private static final Color NAVY = new Color(11, 27, 49);
    public static String IMG_PATH = System.getProperty("user.dir") + "/src/main/java/com/cardGame/resources/";

    /** Pre: an initialized Solitaire game. Post: a playable, styled game window opens. */
    public GUI(Solitaire game) {
        this.game = game;
        setTitle("Black Mench | Midnight Edition");
        setSize(1024, 735);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Keep the starter project's image background and null-layout approach.
        Container background;
        try {
            Image picture = ImageIO.read(new File(IMG_PATH + "midnight_background.jpg"));
            background = new ImagePanel(picture);
        } catch (Exception ignored) {
            JPanel fallback = new JPanel();
            fallback.setBackground(NAVY);
            background = fallback;
        }
        background.setLayout(null);
        setContentPane(background);

        JPanel boardArea = new JPanel(null) {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(5, 20, 39, 180));
                g.fillRoundRect(725, 110, 268, 460, 24, 24);
                g.setColor(new Color(107, 223, 218, 90));
                g.drawRoundRect(725, 110, 268, 460, 24, 24);
                g.dispose();
            }
        };
        boardArea.setOpaque(false);
        boardArea.setBounds(0, 0, 1024, 735);
        background.add(boardArea);

        JLabel title = label("BLACK MENCH", 36);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(GOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, title, 250, 16, 524, 58);

        JLabel subtitle = label("MIDNIGHT EDITION  /  GET CLOSE TO 21", 13);
        subtitle.setForeground(CYAN);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, subtitle, 273, 75, 480, 22);

        howToButton = button("?  HOW TO PLAY");
        howToButton.setBackground(new Color(30, 72, 90));
        howToButton.setForeground(Color.WHITE);
        howToButton.setBorder(BorderFactory.createLineBorder(CYAN, 1));
        place(boardArea, howToButton, 842, 39, 146, 39);
        howToButton.addActionListener(this);

        JLabel dealerTitle = label("DEALER'S HAND", 18);
        dealerTitle.setForeground(CYAN);
        place(boardArea, dealerTitle, 46, 117, 230, 29);
        dealerScore = label("Total: ?", 18);
        place(boardArea, dealerScore, 285, 117, 160, 29);
        dealerPane = new JPanel(null);
        dealerPane.setOpaque(false);
        dealerPane.setBorder(BorderFactory.createLineBorder(new Color(107, 223, 218, 165), 1));
        place(boardArea, dealerPane, 39, 151, 673, 169);

        JLabel playerTitle = label("YOUR HAND", 18);
        playerTitle.setForeground(CYAN);
        place(boardArea, playerTitle, 46, 337, 220, 29);
        playerScore = label("Total: 0", 18);
        place(boardArea, playerScore, 285, 337, 160, 29);
        playerPane = new JPanel(null);
        playerPane.setOpaque(false);
        playerPane.setBorder(BorderFactory.createLineBorder(new Color(107, 223, 218, 165), 1));
        place(boardArea, playerPane, 39, 372, 673, 169);

        tokenCount = label("", 21);
        tokenCount.setForeground(GOLD);
        currentBet = label("", 16);
        place(boardArea, tokenCount, 746, 122, 240, 33);
        place(boardArea, currentBet, 746, 157, 240, 27);

        // This deck graphic is different from the starter project's back.png.
        deckImage = new JLabel("DECK", SwingConstants.CENTER);
        deckImage.setForeground(CYAN);
        deckImage.setBorder(BorderFactory.createLineBorder(CYAN, 1));
        try {
            Image cardBack = ImageIO.read(new File(IMG_PATH + "mench_back.png"));
            deckImage.setIcon(new ImageIcon(cardBack.getScaledInstance(100, 145, Image.SCALE_SMOOTH)));
            deckImage.setText("");
        } catch (Exception ignored) { /* Text remains usable if the picture is absent. */ }
        deckImage.addMouseListener(this);
        deckImage.setToolTipText("Click the deck to hit after placing a bet");
        place(boardArea, deckImage, 808, 189, 100, 145);
        JLabel deckHint = label("CLICK DECK TO HIT", 11);
        deckHint.setForeground(CYAN);
        deckHint.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, deckHint, 774, 340, 168, 17);

        bet5 = button("BET 5");
        bet15 = button("BET 15");
        bet30 = button("BET 30");
        betAll = button("BET ALL");
        hitButton = button("HIT");
        standButton = button("STAND");
        nextButton = button("NEXT ROUND");
        place(boardArea, bet5, 744, 374, 108, 36);
        place(boardArea, bet15, 861, 374, 108, 36);
        place(boardArea, bet30, 744, 417, 108, 36);
        place(boardArea, betAll, 861, 417, 108, 36);
        place(boardArea, hitButton, 744, 466, 108, 39);
        place(boardArea, standButton, 861, 466, 108, 39);
        place(boardArea, nextButton, 744, 517, 225, 39);
        nextButton.setBackground(GOLD);

        for (JButton button : new JButton[] {bet5, bet15, bet30, betAll,
                                              hitButton, standButton, nextButton}) {
            button.addActionListener(this);
        }

        resultLabel = label("Place a bet to start your turn.", 20);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, resultLabel, 26, 580, 968, 32);
        menchLabel = label("", 15);
        menchLabel.setForeground(GOLD);
        menchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, menchLabel, 30, 614, 960, 26);
        JLabel footer = label("ACE 1  /  JACK 11  /  QUEEN 12  /  KING 13      -      SPECIAL CARD: THE MENCH", 12);
        footer.setForeground(new Color(205, 219, 235));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, footer, 50, 656, 920, 22);

        update();
        setVisible(true);
    }

    /** Pre: text and font size. Post: creates a readable text label. */
    private static JLabel label(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    /** Pre: a button label. Post: creates a midnight-colored game button. */
    private JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(CYAN);
        button.setForeground(NAVY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Pre: component and coordinates. Post: places it using the starter's null layout. */
    private static void place(JPanel panel, JComponent component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        panel.add(component);
    }

    /** Pre: the player requests help. Post: displays bullet-point rules in a dialog. */
    private void showInstructions() {
        String instructions = "<html><body style='width:440px;font-family:sans-serif'>"
                + "<h2>HOW TO PLAY BLACK MENCH</h2>"
                + "<ul>"
                + "<li>Place a bet of 5, 15, 30, or all your available tokens.</li>"
                + "<li>Try to get closer to 21 than the dealer without going over 21.</li>"
                + "<li>Click the deck or HIT to draw a card. Choose STAND to finish your turn.</li>"
                + "<li>The dealer draws until their total is at least 17.</li>"
                + "<li>Ace = 1, Jack = 11, Queen = 12, and King = 13.</li>"
                + "<li>There is one special card: THE MENCH. Either player can draw it.</li>"
                + "<li>If your hand was below 18, Mench raises your total to a random 18-21.</li>"
                + "<li>Otherwise Mench subtracts a random 1-18 from your hand.</li>"
                + "<li>Beat the dealer to win your bet amount in profit. A tie returns your bet.</li>"
                + "<li>Click NEXT ROUND to play again.</li>"
                + "<li>Reach 1,000 tokens to win. Run out of tokens and the game ends.</li>"
                + "</ul></body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(instructions), "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Pre: stack of cards exists. Post: shows a layered row with no cards cut off. */
    public JLayeredPane drawPile(Stack<Card> stackIn) {
        JLayeredPane pile = new JLayeredPane();
        pile.setLayout(null);
        int count = stackIn.size();
        int gap = count < 2 ? 0 : Math.min(55, 555 / (count - 1));
        for (int k = 0; k < count; k++) {
            Card card = stackIn.get(k);
            card.setBounds(5 + k * gap, 4, 100, 145);
            pile.add(card, Integer.valueOf(k));
        }
        return pile;
    }

    /** Pre: state may have changed. Post: updates the GUI without changing game rules. */
    public void update() {
        tokenCount.setText("TOKENS  " + game.tokens + " / 1000");
        currentBet.setText("CURRENT BET  " + game.bet);
        playerScore.setText("Total: " + game.pValue);
        dealerScore.setText(game.nextRoundAvailable ? "Total: " + game.dValue : "Total: ?");
        resultLabel.setText(game.result.isEmpty() ? "Place a bet to start your turn." : game.result);
        menchLabel.setText(game.getMenchMessage());

        dealerPane.removeAll();
        playerPane.removeAll();
        for (Card card : game.playerDeck) card.show();
        for (int i = 0; i < game.dealerDeck.size(); i++) {
            if (i == 0 && !game.nextRoundAvailable) game.dealerDeck.get(i).hide();
            else game.dealerDeck.get(i).show();
        }
        JLayeredPane dealerPile = drawPile(game.dealerDeck);
        JLayeredPane playerPile = drawPile(game.playerDeck);
        dealerPile.setBounds(4, 7, 663, 155);
        playerPile.setBounds(4, 7, 663, 155);
        dealerPane.add(dealerPile);
        playerPane.add(playerPile);

        boolean canBet = !game.betted && !game.nextRoundAvailable && !game.won && !game.lost;
        boolean canPlay = game.betted && !game.nextRoundAvailable && !game.won && !game.lost;
        bet5.setEnabled(canBet && game.tokens >= 5);
        bet15.setEnabled(canBet && game.tokens >= 15);
        bet30.setEnabled(canBet && game.tokens >= 30);
        betAll.setEnabled(canBet && game.tokens > 0);
        hitButton.setEnabled(canPlay && game.pValue < 21 && !game.deck.isEmpty());
        standButton.setEnabled(canPlay);
        nextButton.setEnabled(game.nextRoundAvailable || game.won || game.lost);
        nextButton.setText(game.won || game.lost ? "PLAY AGAIN" : "NEXT ROUND");
        deckImage.setCursor(Cursor.getPredefinedCursor(canPlay ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));

        dealerPane.revalidate();
        playerPane.revalidate();
        dealerPane.repaint();
        playerPane.repaint();
        if ((game.won || game.lost) && !endScreenShown) {
            endScreenShown = true;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    game.result + "\nClick PLAY AGAIN to restart.",
                    game.won ? "YOU WON BLACK MENCH!" : "GAME OVER",
                    JOptionPane.INFORMATION_MESSAGE));
        }
    }

    /** Pre: player clicks a button. Post: delegates to Solitaire or shows the help dialog. */
    @Override public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == howToButton) { showInstructions(); return; }
        if (source == bet5) game.bet(5);
        else if (source == bet15) game.bet(15);
        else if (source == bet30) game.bet(30);
        else if (source == betAll) game.bet(game.tokens);
        else if (source == hitButton) game.hit();
        else if (source == standButton) game.dealerTurn();
        else if (source == nextButton) {
            if (game.won || game.lost) { game.resetGame(); endScreenShown = false; }
            else if (game.nextRoundAvailable) game.newGame();
        }
        update();
    }

    /** Pre: the deck is clicked. Post: draws one card if a round is active. */
    @Override public void mouseClicked(MouseEvent event) {
        if (event.getSource() == deckImage) { game.hit(); update(); }
    }

    /** Pre: mouse enters. Post: no additional behavior. */
    @Override public void mouseEntered(MouseEvent event) {}
    /** Pre: mouse exits. Post: no additional behavior. */
    @Override public void mouseExited(MouseEvent event) {}
    /** Pre: mouse pressed. Post: click handler runs on release. */
    @Override public void mousePressed(MouseEvent event) {}
    /** Pre: mouse released. Post: no additional behavior. */
    @Override public void mouseReleased(MouseEvent event) {}
    /** Pre: mouse moved. Post: no dragging needed. */
    @Override public void mouseMoved(MouseEvent event) {}
    /** Pre: mouse dragged. Post: no dragging needed. */
    @Override public void mouseDragged(MouseEvent event) {}
}
