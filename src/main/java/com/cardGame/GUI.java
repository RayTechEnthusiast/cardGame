package com.cardGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

/** Controls only the graphics and player input. Solitaire contains the rules. */
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
    private JButton bet5, bet15, bet30, betAll, hitButton, standButton, nextButton;
    private boolean endScreenShown;
    private final Color gold = new Color(240, 198, 78);
    public static String IMG_PATH = System.getProperty("user.dir") + "/src/main/java/com/cardGame/resources/";

    /** Pre: a Solitaire game exists. Post: builds a playable window and displays the hands. */
    public GUI(Solitaire game) {
        this.game = game;
        setTitle("Black Mench");
        setSize(1024, 735);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Like the starter GUI, use a background, null layouts, and layered card piles.
        Container background;
        try {
            Image picture = ImageIO.read(new File(IMG_PATH + "background.jpg"));
            background = new ImagePanel(picture);
        } catch (Exception ignored) {
            JPanel plainBackground = new JPanel();
            plainBackground.setBackground(new Color(14, 55, 39));
            background = plainBackground;
        }
        background.setLayout(null);
        setContentPane(background);

        JPanel boardArea = new JPanel(null);
        boardArea.setOpaque(false);
        boardArea.setBounds(0, 0, 1024, 735);
        background.add(boardArea);

        JLabel title = label("BLACK MENCH", 38);
        title.setFont(new Font("Serif", Font.BOLD, 38));
        title.setForeground(gold);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, title, 250, 12, 520, 60);

        JLabel instruction = label("Reach 1000 tokens to win. Lose them all and the game ends.", 14);
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, instruction, 185, 75, 650, 25);

        place(boardArea, label("DEALER", 21), 45, 115, 190, 30);
        dealerScore = label("Total: ?", 19);
        place(boardArea, dealerScore, 250, 115, 220, 30);
        dealerPane = new JPanel(null);
        dealerPane.setOpaque(false);
        dealerPane.setBorder(BorderFactory.createLineBorder(gold, 1));
        place(boardArea, dealerPane, 35, 150, 680, 170);

        place(boardArea, label("PLAYER", 21), 45, 337, 190, 30);
        playerScore = label("Total: 0", 19);
        place(boardArea, playerScore, 250, 337, 220, 30);
        playerPane = new JPanel(null);
        playerPane.setOpaque(false);
        playerPane.setBorder(BorderFactory.createLineBorder(gold, 1));
        place(boardArea, playerPane, 35, 371, 680, 170);

        tokenCount = label("", 21);
        currentBet = label("", 18);
        place(boardArea, tokenCount, 750, 120, 240, 33);
        place(boardArea, currentBet, 750, 157, 240, 30);

        deckImage = new JLabel("DECK", SwingConstants.CENTER);
        deckImage.setFont(new Font("SansSerif", Font.BOLD, 19));
        deckImage.setForeground(gold);
        deckImage.setBorder(BorderFactory.createLineBorder(gold, 2));
        try {
            Image cardBack = ImageIO.read(new File(IMG_PATH + "back.png"));
            deckImage.setIcon(new ImageIcon(cardBack.getScaledInstance(96, 140, Image.SCALE_SMOOTH)));
            deckImage.setText("");
        } catch (Exception ignored) { /* The DECK text remains clickable. */ }
        deckImage.addMouseListener(this);
        deckImage.setToolTipText("Click to hit after placing a bet");
        place(boardArea, deckImage, 786, 190, 100, 145);
        JLabel deckHint = label("CLICK DECK TO HIT", 12);
        deckHint.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, deckHint, 742, 336, 188, 22);

        bet5 = button("BET 5");
        bet15 = button("BET 15");
        bet30 = button("BET 30");
        betAll = button("BET ALL");
        hitButton = button("HIT");
        standButton = button("STAND");
        nextButton = button("NEXT ROUND");
        place(boardArea, bet5, 746, 367, 105, 39);
        place(boardArea, bet15, 861, 367, 105, 39);
        place(boardArea, bet30, 746, 413, 105, 39);
        place(boardArea, betAll, 861, 413, 105, 39);
        place(boardArea, hitButton, 746, 463, 105, 42);
        place(boardArea, standButton, 861, 463, 105, 42);
        place(boardArea, nextButton, 746, 516, 220, 42);

        for (JButton button : new JButton[] {bet5, bet15, bet30, betAll,
                                              hitButton, standButton, nextButton}) {
            button.addActionListener(this);
        }

        resultLabel = label("Place a bet to start your turn.", 21);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, resultLabel, 30, 568, 960, 34);
        menchLabel = label("", 15);
        menchLabel.setForeground(gold);
        menchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, menchLabel, 35, 604, 950, 26);
        JLabel rules = label("A=1  J=11  Q=12  K=13  |  Mench: below 18 boosts to 18-21; otherwise subtracts 1-18", 13);
        rules.setHorizontalAlignment(SwingConstants.CENTER);
        place(boardArea, rules, 45, 648, 930, 25);

        update();
        setVisible(true);
    }

    /** Pre: text and font size are known. Post: returns a readable game label. */
    private static JLabel label(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    /** Pre: text is known. Post: returns a styled button. */
    private JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(gold);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    /** Pre: panel and bounds are known. Post: adds a positioned element using null layout. */
    private static void place(JPanel panel, JComponent component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        panel.add(component);
    }

    /** Pre: a stack of cards exists. Post: returns a layered pile that keeps every card on-screen. */
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

    /** Pre: game state may have changed. Post: refreshes cards, buttons, and scores. */
    public void update() {
        tokenCount.setText("Tokens: " + game.tokens);
        currentBet.setText("Bet: " + game.bet);
        playerScore.setText("Total: " + game.pValue);
        dealerScore.setText(game.nextRoundAvailable ? "Total: " + game.dValue : "Total: ?");
        resultLabel.setText(game.result.isEmpty() ? "Place a bet to start your turn." : game.result);
        menchLabel.setText(game.getMenchMessage());

        // A dealer's first card stays face down until the round ends.
        dealerPane.removeAll();
        playerPane.removeAll();
        for (Card card : game.playerDeck) card.show();
        for (int i = 0; i < game.dealerDeck.size(); i++) {
            if (i == 0 && !game.nextRoundAvailable) game.dealerDeck.get(i).hide();
            else game.dealerDeck.get(i).show();
        }
        JLayeredPane dealerPile = drawPile(game.dealerDeck);
        JLayeredPane playerPile = drawPile(game.playerDeck);
        dealerPile.setBounds(4, 7, 666, 155);
        playerPile.setBounds(4, 7, 666, 155);
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

    /** Pre: a button is clicked. Post: delegates the action to Solitaire and redraws the GUI. */
    @Override public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
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

    /** Pre: the user clicks the card-back deck. Post: draws a card if the bet is active. */
    @Override public void mouseClicked(MouseEvent event) {
        if (event.getSource() == deckImage) { game.hit(); update(); }
    }

    /** Pre: mouse enters a component. Post: no change needed. */
    @Override public void mouseEntered(MouseEvent event) {}
    /** Pre: mouse exits a component. Post: no change needed. */
    @Override public void mouseExited(MouseEvent event) {}
    /** Pre: mouse is pressed. Post: action is handled by mouseClicked. */
    @Override public void mousePressed(MouseEvent event) {}
    /** Pre: mouse is released. Post: action is handled by mouseClicked. */
    @Override public void mouseReleased(MouseEvent event) {}
    /** Pre: mouse moves. Post: no dragging in this game. */
    @Override public void mouseMoved(MouseEvent event) {}
    /** Pre: mouse drags. Post: no dragging in this game. */
    @Override public void mouseDragged(MouseEvent event) {}
}
