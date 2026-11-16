package com.cardGame;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JComponent;

/** Keeps the original starter project's background-image component. */
class ImagePanel extends JComponent {
    private final Image image;

    /** Pre: background image exists. Post: creates an image-backed panel. */
    public ImagePanel(Image image) { this.image = image; }

    /** Pre: Swing repaints. Post: background image fills the panel. */
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}
