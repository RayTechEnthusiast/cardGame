package com.cardGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

    private Solitaire game;
    JPanel dealerPane;
    JPanel playerPane;

    public static String IMG_PATH = System.getProperty("user.dir") + "/src/main/java/com/cardGame/resources/";

    public GUI(Solitaire game) {
        this.game = game;
        
        // Set up main window properties
        setTitle("Solitaire");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Load the background image
        try {
            Image bgImage = ImageIO.read(new File(IMG_PATH + "background.jpg"));
            setContentPane(new ImagePanel(bgImage));
        } catch(IOException e) {
            e.printStackTrace();
        }

        // STEP 1: Main board container
        JPanel boardArea = new JPanel();
        boardArea.setLayout(null);
        boardArea.setOpaque(false);
        boardArea.setBounds(0, 0, 900, 600);
        boardArea.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));
        this.add(boardArea);

        // Pane for dealer cards (Holds the deck and revealed cards)
        dealerPane = new JPanel();
        dealerPane.setOpaque(false);
        dealerPane.setLayout(null);
        dealerPane.setBounds(50, 40, 450, 200);
        dealerPane.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.RED));
        boardArea.add(dealerPane);

        // Pane for player cards (Holds playable tableau piles and discard location)
        playerPane = new JPanel();
        playerPane.setOpaque(false);
        playerPane.setLayout(null);
        playerPane.setBounds(50, 300, 450, 200);
        playerPane.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.GREEN));
        boardArea.add(playerPane);

        // Pane for game controls (Score, reset button, etc.)
        JPanel controlPane = new JPanel();
        controlPane.setOpaque(false);
        controlPane.setLayout(null);
        controlPane.setBounds(650, 350, 180, 150);
        controlPane.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.ORANGE));
        boardArea.add(controlPane);

        // STEP 2: Create test stacks and draw them next to each other
        
        // First pile
        Stack<Card> deck1 = new Stack<>();
        deck1.push(new Card(2, Card.Suit.Spades));
        deck1.push(new Card(3, Card.Suit.Spades));
        deck1.push(new Card(4, Card.Suit.Spades));
        
        JLayeredPane pile1 = drawPile(deck1);
        pile1.setBounds(10, 10, 180, 150);
        playerPane.add(pile1);

        // Second pile (Placed next to the first one)
        Stack<Card> deck2 = new Stack<>();
        deck2.push(new Card(5, Card.Suit.Hearts));
        deck2.push(new Card(6, Card.Suit.Hearts));
        
        JLayeredPane pile2 = drawPile(deck2);
        pile2.setBounds(210, 10, 180, 150);
        playerPane.add(pile2);

        this.setVisible(true);
    }
   
    /**
     * Draws cards in a pile layered on top of each other.
     * Returns a new JLayeredPane so multiple rows can be created.
     */
    public JLayeredPane drawPile(Stack<Card> stackIn) {
        JLayeredPane pile = new JLayeredPane();
        pile.setLayout(null);
        
        Object[] cardsArray = stackIn.toArray(); 
        
        // Loop through array and layer cards
        for (int k = 0; k < cardsArray.length; k++) {
            Card c = (Card) cardsArray[k];
            // Stagger each card horizontally by 22 pixels
            c.setBounds(k * 22, 0, 100, 145);
            pile.add(c, Integer.valueOf(k));
        }
        
        return pile;
    }

    // --- Empty Event Listeners ---
    @Override public void mouseDragged(MouseEvent arg0) {}
    @Override public void mouseMoved(MouseEvent arg0) {}
    @Override public void mouseClicked(MouseEvent arg0) {}
    @Override public void mouseEntered(MouseEvent arg0) {}
    @Override public void mouseExited(MouseEvent arg0) {}
    @Override public void mousePressed(MouseEvent arg0) {}
    @Override public void mouseReleased(MouseEvent arg0) {}
    @Override public void actionPerformed(ActionEvent arg0) {}
}