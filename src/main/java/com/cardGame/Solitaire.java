package com.cardGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/** Handles the rules, cards, and score. */
public class Solitaire {
    public final Stack<Card> deck = new Stack<>();
    public final Stack<Card> playerDeck = new Stack<>();
    public final Stack<Card> dealerDeck = new Stack<>();
    public int tokens = 100;
    public int bet;
    public int pValue;
    public int dValue;
    public boolean betted;
    public boolean bust;
    public boolean noMoreHits;
    public boolean nextRoundAvailable;
    public boolean won;
    public boolean lost;
    public String result = "";
    private String playerMenchMessage = "";
    private String dealerMenchMessage = "";
    private final Random random;

    /** Pre: none. Post: starts a new game. */
    public Solitaire() { this(new Random()); }

    /** Pre: random generator. Post: starts the first round. */
    Solitaire(Random random) {
        this.random = random;
        newGame();
    }

    /** Pre: none. Post: shuffles 52 cards plus Mench. */
    public void generateCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (int value = 1; value <= 13; value++) cards.add(new Card(value, suit));
        }
        cards.add(new Card());
        Collections.shuffle(cards, random);
        deck.clear();
        deck.addAll(cards);
    }

    /** Pre: new round needed. Post: shuffles and deals cards. */
    public void newGame() {
        bet = pValue = dValue = 0;
        betted = bust = noMoreHits = nextRoundAvailable = false;
        result = playerMenchMessage = dealerMenchMessage = "";
        playerDeck.clear();
        dealerDeck.clear();
        generateCards();
        // Count normal cards before Mench on the first deal.
        Card firstPlayer = deck.pop();
        Card firstDealer = deck.pop();
        Card secondPlayer = deck.pop();
        Card secondDealer = deck.pop();
        setupStartingHand(playerDeck, firstPlayer, secondPlayer, true);
        setupStartingHand(dealerDeck, firstDealer, secondDealer, false);
        bust = pValue > 21;
    }

    /** Pre: two cards dealt. Post: totals the starting hand. */
    void setupStartingHand(Stack<Card> hand, Card first, Card second, boolean player) {
        hand.clear();
        hand.push(first);
        hand.push(second);
        for (Card card : hand) {
            if (card.mench) applyMenchToHand(hand, card, player);
        }
        if (player) pValue = calculateHand(hand);
        else dValue = calculateHand(hand);
    }

    /** Pre: Mench drawn. Post: picks its plus/minus amount. */
    void applyMenchToHand(Stack<Card> hand, Card menchCard, boolean player) {
        int before = 0;
        for (Card card : hand) {
            if (card != menchCard) before += card.mench ? card.menchValue : card.value;
        }
        if (before < 18) menchCard.menchValue = 18 + random.nextInt(4) - before;
        else menchCard.menchValue = -(1 + random.nextInt(18));
        String message = (player ? "You drew" : "Dealer drew") + " THE MENCH: "
                + (menchCard.menchValue > 0 ? "+" : "") + menchCard.menchValue;
        if (player) playerMenchMessage = message;
        else dealerMenchMessage = message;
    }

    /** Pre: hand has cards. Post: adds up the values. */
    public int calculateHand(Stack<Card> hand) {
        int total = 0;
        for (Card card : hand) total += card.mench ? card.menchValue : card.value;
        return total;
    }

    /** Pre: deck has cards. Post: draws one for the hand. */
    private void draw(Stack<Card> hand, boolean player) {
        if (deck.isEmpty()) return;
        Card card = deck.pop();
        hand.push(card);
        if (card.mench) applyMenchToHand(hand, card, player);
        if (player) pValue = calculateHand(hand);
        else dValue = calculateHand(hand);
    }

    /** Pre: player can bet. Post: takes tokens for the bet. */
    public void bet(int amount) {
        if (betted || nextRoundAvailable || won || lost || amount < 1 || amount > tokens) return;
        tokens -= amount;
        bet = amount;
        betted = true;
        if (bust) gameOver();
    }

    /** Pre: player can hit. Post: draws and checks for bust. */
    public void hit() {
        if (!betted || noMoreHits || nextRoundAvailable || won || lost || pValue >= 21) return;
        if (deck.isEmpty()) { dealerTurn(); return; }
        draw(playerDeck, true);
        if (pValue > 21) {
            bust = true;
            gameOver();
        }
    }

    /** Pre: round is active. Post: dealer plays and ends it. */
    public void dealerTurn() {
        if (!betted || nextRoundAvailable || won || lost) return;
        noMoreHits = true;
        while (!bust && dValue < 17 && !deck.isEmpty()) draw(dealerDeck, false);
        gameOver();
    }

    /** Pre: round finished. Post: figures out who won. */
    public void gameOver() {
        if (!betted || nextRoundAvailable) return;
        if (pValue > 21) {
            result = "BUST! Dealer wins this round.";
        } else if (dValue > 21 || pValue > dValue) {
            tokens += bet * 2;
            result = dValue > 21 ? "Dealer bust! You win this round!" : "You win this round!";
        } else if (pValue == dValue) {
            tokens += bet;
            result = "Tie! Your bet is returned.";
        } else {
            result = "Dealer wins this round.";
        }
        nextRoundAvailable = noMoreHits = true;
        if (tokens <= 0) { lost = true; result = "GAME OVER! No tokens left."; }
        else if (tokens >= 1000) { won = true; result = "YOU WON BLACK MENCH!"; }
    }

    /** Pre: game state exists. Post: gets the Mench message. */
    public String getMenchMessage() {
        if (nextRoundAvailable && !dealerMenchMessage.isEmpty()) {
            return playerMenchMessage.isEmpty() ? dealerMenchMessage
                    : playerMenchMessage + "  |  " + dealerMenchMessage;
        }
        return playerMenchMessage;
    }

    /** Pre: game ended. Post: resets tokens and cards. */
    public void resetGame() {
        tokens = 100;
        won = lost = false;
        newGame();
    }
}
