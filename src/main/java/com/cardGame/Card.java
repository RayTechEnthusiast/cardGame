package com.cardGame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/** Stores a card. The GUI decides where to put it. */
public class Card extends JPanel {
    public int value;
    public Suit suit;
    public boolean mench;
    public int menchValue;
    boolean isReversed;
    private BufferedImage image;

    public enum Suit {
        Spades(1, false), Hearts(2, true), Diamonds(3, true), Clubs(4, false);
        public final int value;
        public final boolean isRed;
        /** Pre: suit info. Post: saves the suit properties. */
        Suit(int value, boolean isRed) { this.value = value; this.isRed = isRed; }
    }

    /** Pre: number and suit. Post: makes a regular card. */
    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
        setup();
        try { image = ImageIO.read(new File(GUI.IMG_PATH + toString() + ".png")); }
        catch (Exception ignored) { /* Text card is the fallback if a file is missing. */ }
    }

    /** Pre: none. Post: makes the Mench card. */
    public Card() {
        mench = true;
        setup();
    }

    /** Pre: card exists. Post: sets its size. */
    private void setup() {
        setSize(100, 145);
        setPreferredSize(new Dimension(100, 145));
        setMinimumSize(new Dimension(100, 145));
        setMaximumSize(new Dimension(100, 145));
        setOpaque(false);
    }

    /** Pre: none. Post: makes the little M! card back for the deck. */
    public static BufferedImage createBackImage() {
        BufferedImage picture = new BufferedImage(100, 145, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = picture.createGraphics();
        drawBack(pen, 100, 145);
        pen.dispose();
        return picture;
    }

    /** Pre: graphics and card size. Post: draws a simple purple card back. */
    private static void drawBack(Graphics2D g, int width, int height) {
        g.setColor(new Color(126, 77, 153));
        g.fillRoundRect(0, 0, width, height, 9, 9);
        g.setColor(Color.WHITE);
        g.drawRect(6, 6, width - 13, height - 13);
        g.setColor(new Color(255, 218, 115));
        g.setFont(new Font("SansSerif", Font.BOLD, 43));
        g.drawString("M!", 19, 79);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("* MENCH *", 14, 105);
    }

    /** Pre: value 1-13. Post: turns it into card text. */
    public static String valueString(int value) {
        if (value == 1) return "a";
        if (value == 11) return "j";
        if (value == 12) return "q";
        if (value == 13) return "k";
        return Integer.toString(value);
    }

    /** Pre: card text. Post: gets its number. */
    public static int valueInt(String value) {
        if (value.equals("a")) return 1;
        if (value.equals("j")) return 11;
        if (value.equals("q")) return 12;
        if (value.equals("k")) return 13;
        return Integer.parseInt(value);
    }

    /** Pre: card exists. Post: gives its name. */
    @Override public String toString() {
        return mench ? "The Mench" : valueString(value) + " of " + suit.name().toLowerCase();
    }

    /** Pre: card exists. Post: gives its saved info. */
    public String saveAsString() {
        return mench ? "mench of " + menchValue + " of " + isReversed
                : valueString(value) + " of " + suit.name() + " of " + isReversed;
    }

    /** Pre: card exists. Post: flips it over. */
    @Override public void hide() { isReversed = true; repaint(); }

    /** Pre: card exists. Post: flips it face up. */
    @Override public void show() { isReversed = false; repaint(); }

    /** Pre: card needs drawing. Post: paints the right side. */
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isReversed) {
            drawBack(g, getWidth(), getHeight());
        } else if (!mench && image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(mench ? new Color(255, 253, 224) : Color.WHITE);
            g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g.setColor(mench ? new Color(126, 77, 153) : Color.DARK_GRAY);
            g.drawRect(3, 3, getWidth() - 7, getHeight() - 7);
            if (mench) {
                // A silly homemade-looking card instead of a fancy picture.
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.drawString("WHAT?!", 17, 25);
                g.setFont(new Font("SansSerif", Font.BOLD, 45));
                g.drawString("M!", 20, 78);
                g.setFont(new Font("SansSerif", Font.BOLD, 13));
                g.drawString("THE MENCH", 11, 103);
                g.setFont(new Font("SansSerif", Font.BOLD, 20));
                String effect = (menchValue > 0 ? "+" : "") + menchValue;
                g.drawString(effect, 31, 128);
            } else {
                g.setColor(suit.isRed ? new Color(188, 34, 46) : Color.BLACK);
                String symbol = suit == Suit.Hearts ? "\u2665" : suit == Suit.Diamonds ? "\u2666"
                        : suit == Suit.Clubs ? "\u2663" : "\u2660";
                g.setFont(new Font("SansSerif", Font.BOLD, 24));
                g.drawString(valueString(value).toUpperCase(), 12, 30);
                g.setFont(new Font("Serif", Font.BOLD, 48));
                g.drawString(symbol, 32, 97);
            }
        }
        g.dispose();
    }
}
