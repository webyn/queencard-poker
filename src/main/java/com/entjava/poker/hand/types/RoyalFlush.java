package com.entjava.poker.hand.types;

import com.entjava.poker.card.Card;
import com.entjava.poker.hand.Hand;
import com.entjava.poker.hand.HandType;

import java.util.List;

/**
 * @see <a href="https://en.wikipedia.org/wiki/List_of_poker_hands#Royal_flush">What is a Royal Flush?</a>
 */
public class RoyalFlush extends Hand {

    private List<Card> cards;

    public RoyalFlush(List<Card> cards) {
        this.cards = cards;
        // A Royal Flush is always the top 5 cards (A, K, Q, J, 10) of the same suit.
        setCurrentHand(this.cards.subList(0, 5));
    }

    @Override
    public HandType getHandType() {
        return HandType.ROYAL_FLUSH;
    }

    public List<Card> getCards() {
        return cards;
    }

    /**
     * @return The name of the hand, e.g. "Royal Flush"
     */
    @Override
    public String toString() {
        return "Royal Flush (" + cards.get(0).getSuit() + ")";
    }
}

