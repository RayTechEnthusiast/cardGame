package com.cardGame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Card class to store the information of single card
 */
public class Card extends JPanel {
    // Members
    public int value;
    public Suit suit;
    private BufferedImage image;
    private BufferedImage backImage;
    boolean isReversed;
    Point positionOffset;
    
    /** Enum to store the suit values */
    public enum Suit {
        Spades(1, false),
        Hearts(2, true),
        Diamonds(3, true),
        Clubs(4, false);
        
        public int value;
        public boolean isRed;
        
        private Suit(int value, boolean isRed) {
            this.value = value;
            this.isRed = isRed;
        }
    };
    
    // Converts the value of the card to a string
    public static String valueString(int value) {
        if(value == 11) return "j";
        if(value == 12) return "q";
        if(value == 13) return "k";
        if(value == 1) return "a";
        return Integer.toString(value);
    }

    // Converts the value of the card to an int
    public static int valueInt(String value) {
        if(value.equals("j")) return 11;
        if(value.equals("q")) return 12;
        if(value.equals("k")) return 13;
        if(value.equals("a")) return 1;
        return Integer.parseInt(value);
    }
    
    // toString method
    public String toString() {
        return valueString(value) + " of " + suit.name().toLowerCase();
    }
    
    // Save state string
    public String saveAsString() {
        return valueString(value) + " of " + suit.name() + " of " + isReversed;
    }
    
    // Class constructor
    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;        
        this.isReversed = false;
        
        try {
            // Load the images
            this.image = ImageIO.read(new File(GUI.IMG_PATH + toString() + ".png"));
            this.backImage = ImageIO.read(new File(GUI.IMG_PATH + "back.png"));
            setBounds(0, 0, image.getWidth(), image.getHeight());
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        positionOffset = new Point(0,0);
        
        // Setting explicit dimensions
        setSize(new Dimension(100, 145));
        setPreferredSize(new Dimension(100, 145));
        setMinimumSize(new Dimension(100, 145));
        setMaximumSize(new Dimension(100, 145));
        setOpaque(false);
    }
    
    // Turns the card back up
    public void hide() {
        isReversed = true;
    }
    
    // Turns the card face up
    public void show() {
        isReversed = false;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Determine which image to draw
        BufferedImage currentImg = image;
        if (isReversed == true) {
            currentImg = backImage;
        }

        g.drawImage(currentImg, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}