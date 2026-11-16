package com.cardGame;

import javax.swing.SwingUtilities;

public class Main {
    /** Pre: Java starts. Post: opens the Black Mench Swing window on the GUI thread. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Solitaire game = new Solitaire();
            new GUI(game);
        });
    }
}
