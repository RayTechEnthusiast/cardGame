package com.cardGame;

import java.util.*;

/** Simple, dependency-free checks for Black Mench rules (no GUI required). */
public class BlackMenchTests {
    static int passed;

    /** Pre: a test condition. Post: counts a pass or raises an error. */
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        passed++;
    }

    /** Pre: JDK installed. Post: runs tests and prints the pass count. */
    public static void main(String[] args) {
        testDeck();
        testMench();
        testInitialMench();
        testBetAndStand();
        testHitAndBustProtection();
        testWinAndReset();
        testRoundRefresh();
        System.out.println("PASS: " + passed + " Black Mench rule checks");
    }

    /** Pre: a new game. Post: verifies 53 unique cards and normal values. */
    static void testDeck() {
        Solitaire game = new Solitaire(new Random(2));
        check(game.deck.size() == 49, "53 minus 4 dealt should remain");
        HashSet<String> seen = new HashSet<>();
        int menchCount = 0;
        for (Card card : allCards(game)) {
            seen.add(card.toString());
            if (card.mench) menchCount++;
        }
        check(seen.size() == 53, "53 unique cards exist");
        check(menchCount == 1, "exactly one Mench exists");
        check(Card.valueInt("a") == 1 && Card.valueInt("k") == 13, "card values agree with starter");
    }

    /** Pre: game hands and deck exist. Post: combines them for inspection. */
    static ArrayList<Card> allCards(Solitaire game) {
        ArrayList<Card> cards = new ArrayList<>(game.deck);
        cards.addAll(game.playerDeck);
        cards.addAll(game.dealerDeck);
        return cards;
    }

    /** Pre: ordinary cards and Mench. Post: tests both random Mench branches. */
    static void testMench() {
        Solitaire game = new Solitaire(new Random(0));
        Stack<Card> low = new Stack<>();
        low.push(new Card(5, Card.Suit.Spades));
        low.push(new Card(7, Card.Suit.Hearts));
        Card mench = new Card();
        low.push(mench);
        game.applyMenchToHand(low, mench, true);
        check(game.calculateHand(low) >= 18 && game.calculateHand(low) <= 21,
                "Mench boosts low hand into 18-21");
        check(mench.menchValue > 0, "low total gets positive Mench");
        Stack<Card> high = new Stack<>();
        high.push(new Card(10, Card.Suit.Clubs));
        high.push(new Card(10, Card.Suit.Spades));
        Card reverseMench = new Card();
        high.push(reverseMench);
        game.applyMenchToHand(high, reverseMench, false);
        check(reverseMench.menchValue <= -1 && reverseMench.menchValue >= -18,
                "Mench subtracts 1 through 18 at 18+ total");
    }

    /** Pre: a first-dealt Mench. Post: verifies normal card counts first. */
    static void testInitialMench() {
        Solitaire game = new Solitaire(new Random(3));
        Stack<Card> hand = new Stack<>();
        game.setupStartingHand(hand, new Card(), new Card(10, Card.Suit.Clubs), true);
        check(game.pValue >= 18 && game.pValue <= 21, "initial Mench counts normal card first");
        check(hand.get(0).mench, "initial card display order remains unchanged");
    }

    /** Pre: a fresh round. Post: validates tokens, stand, and one-time payout. */
    static void testBetAndStand() {
        Solitaire game = new Solitaire(new Random(5));
        int initialTokens = game.tokens;
        game.bet(15);
        check(game.betted && game.tokens == initialTokens - 15, "bet deducts tokens");
        game.bet(10000);
        check(game.tokens == initialTokens - 15, "cannot bet again");
        if (!game.nextRoundAvailable) game.dealerTurn();
        check(game.nextRoundAvailable, "stand resolves the round");
        int tokensAfter = game.tokens;
        game.gameOver();
        check(game.tokens == tokensAfter, "settlement cannot pay twice");
    }

    /** Pre: a playable hand. Post: verifies drawing and action limits. */
    static void testHitAndBustProtection() {
        Solitaire game = new Solitaire(new Random(6));
        for (int i = 0; game.pValue >= 21 && i < 30; i++) game.newGame();
        check(game.pValue < 21, "test starts on a hittable hand");
        game.bet(5);
        int count = game.playerDeck.size();
        game.hit();
        check(game.playerDeck.size() == count + 1, "hit draws one card");
        if (game.nextRoundAvailable) {
            int after = game.playerDeck.size();
            game.hit();
            check(game.playerDeck.size() == after, "cannot hit after round ends");
        }
    }

    /** Pre: near-target tokens. Post: verifies victory and restart. */
    static void testWinAndReset() {
        Solitaire game = new Solitaire(new Random(7));
        game.tokens = 999;
        game.bet(1);
        game.pValue = 21;
        game.dValue = 20;
        game.gameOver();
        check(game.won && game.tokens >= 1000, "1000 tokens triggers win");
        game.resetGame();
        check(!game.won && game.tokens == 100, "replay restores starting tokens");
    }

    /** Pre: a finished round. Post: validates the next deck and hands. */
    static void testRoundRefresh() {
        Solitaire game = new Solitaire(new Random(8));
        game.bet(5);
        if (!game.nextRoundAvailable) game.dealerTurn();
        if (!game.lost && !game.won) {
            game.newGame();
            check(game.deck.size() == 49 && game.playerDeck.size() == 2
                    && game.dealerDeck.size() == 2, "new round replaces entire deck cleanly");
        }
    }
}
