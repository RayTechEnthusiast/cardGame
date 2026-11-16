package com.cardGame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/** Stores one normal card or the special Mench card. GUI handles where it appears. */
public class Card extends JPanel {
    public int value;
    public Suit suit;
    public boolean mench;
    public int menchValue;
    boolean isReversed;
    private BufferedImage image;
    private BufferedImage backImage;

    public enum Suit {
        Spades(1, false), Hearts(2, true), Diamonds(3, true), Clubs(4, false);
        public final int value;
        public final boolean isRed;
        /** Pre: suit properties. Post: stores the original starter suit metadata. */
        Suit(int value, boolean isRed) { this.value = value; this.isRed = isRed; }
    }

    /** Pre: value 1-13 and a suit. Post: ready to display one normal card. */
    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
        setup();
        try { image = ImageIO.read(new File(GUI.IMG_PATH + toString() + ".png")); }
        catch (Exception ignored) { /* Text card is the fallback if a file is missing. */ }
    }

    /** Pre: none. Post: ready to display the unique Mench card. */
    public Card() {
        mench = true;
        setup();
    }

    /** Pre: a newly created card. Post: sets the same dimensions as starter cards. */
    private void setup() {
        setSize(100, 145);
        setPreferredSize(new Dimension(100, 145));
        setMinimumSize(new Dimension(100, 145));
        setMaximumSize(new Dimension(100, 145));
        setOpaque(false);
        try { backImage = ImageIO.read(new File(GUI.IMG_PATH + "back.png")); }
        catch (Exception ignored) { /* Draw a fallback back if missing. */ }
    }

    /** Pre: a value from 1 to 13. Post: returns its name for a card image. */
    public static String valueString(int value) {
        if (value == 1) return "a";
        if (value == 11) return "j";
        if (value == 12) return "q";
        if (value == 13) return "k";
        return Integer.toString(value);
    }

    /** Pre: a card value string. Post: returns the numeric card value. */
    public static int valueInt(String value) {
        if (value.equals("a")) return 1;
        if (value.equals("j")) return 11;
        if (value.equals("q")) return 12;
        if (value.equals("k")) return 13;
        return Integer.parseInt(value);
    }

    /** Pre: this card exists. Post: returns its readable name. */
    @Override public String toString() {
        return mench ? "The Mench" : valueString(value) + " of " + suit.name().toLowerCase();
    }

    /** Pre: this card exists. Post: returns a short save/debug description. */
    public String saveAsString() {
        return mench ? "mench of " + menchValue + " of " + isReversed
                : valueString(value) + " of " + suit.name() + " of " + isReversed;
    }

    /** Pre: a card is displayed. Post: it shows its back. */
    @Override public void hide() { isReversed = true; repaint(); }

    /** Pre: a card is displayed. Post: it shows its face. */
    @Override public void show() { isReversed = false; repaint(); }

    /** Pre: Swing needs to paint a card. Post: draws face, back, or Mench. */
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isReversed && backImage != null) {
            g.drawImage(backImage, 0, 0, getWidth(), getHeight(), null);
        } else if (!isReversed && !mench && image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(isReversed ? new Color(40, 43, 102) : mench ? new Color(38, 23, 66) : Color.WHITE);
            g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g.setColor(mench || isReversed ? new Color(240, 198, 78) : Color.DARK_GRAY);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            if (isReversed) {
                g.setFont(new Font("Serif", Font.BOLD, 52));
                g.drawString("?", 36, 93);
            } else if (mench) {
                g.setFont(new Font("Serif", Font.BOLD, 48));
                g.drawString("M", 26, 60);
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.drawString("MENCH", 20, 88);
                g.setFont(new Font("SansSerif", Font.BOLD, 23));
                g.drawString((menchValue > 0 ? "+" : "") + menchValue, 24, 119);
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
