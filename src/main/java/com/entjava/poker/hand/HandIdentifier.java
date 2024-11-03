package com.entjava.poker.hand;

import com.entjava.poker.card.CardSuit;
import com.entjava.poker.hand.types.*;
import com.entjava.poker.card.Card;
import com.entjava.poker.card.CardRank;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A service that is used to identify the {@link Hand} given the player's cards and the community
 * cards.
 */
@Component
public class HandIdentifier {

    /**
     * Given the player's cards and the community cards, identifies the player's hand.
     *
     * @param playerCards
     * @param communityCards
     * @return The player's {@link Hand} or `null` if no Hand was identified.
     */
    public Hand identifyHand(List<Card> playerCards, List<Card> communityCards) {


            if (communityCards.isEmpty()) {
                // Initial hand with just player cards
                if (playerCards.stream().allMatch(card -> card.getRank() == playerCards.get(0).getRank())) {
                    return new OnePair(playerCards, new ArrayList<>());
                }
                return new HighCard(playerCards);
            }
        

        List<Card> combinedCards = new ArrayList<>();
        combinedCards.addAll(playerCards);
        combinedCards.addAll(communityCards);

        combinedCards.sort(Collections.reverseOrder());

        // Map to track same suits
        Map<String, List<Card>> sameCardSuitMap = combinedCards.stream()
                .collect(Collectors.groupingBy(c -> c.getSuit().toString()));

        // Check for flush or straight flush
        List<Card> sameSuitCardsList = sameCardSuitMap.values().stream()
                .filter(l -> l.size() >= 5)
                .findFirst()
                .orElse(new ArrayList<>());

        if (!sameSuitCardsList.isEmpty()) {
            // Check for Royal Flush (A, K, Q, J, 10 of the same suit)
            if (isRoyalFlush(sameSuitCardsList)) {
                return new RoyalFlush(sameSuitCardsList.subList(0, 5)); // Return the royal flush cards
            }

            // Check for Straight Flush
            if (isStraight(sameSuitCardsList)) {
                return new StraightFlush(sameSuitCardsList.subList(0, 5)); // Return the top 5 cards of straight flush
            } else {
                return new Flush(sameSuitCardsList.subList(0, 5)); // Return the top 5 flush cards
            }
        }

        // Map to track same ranks
        Map<String, List<Card>> sameCardRankMap = combinedCards.stream()
                .collect(Collectors.groupingBy(c -> c.getRank().toString()));

        // Four of a kind checker
        List<Card> fourOfAKindList = sameCardRankMap.values().stream()
                .filter(l -> l.size() == 4)
                .findFirst()
                .orElse(new ArrayList<>());

        if (!fourOfAKindList.isEmpty()) {
            combinedCards.removeAll(fourOfAKindList);
            return new FourOfAKind(fourOfAKindList, combinedCards.subList(0, 1)); // Kicker is the highest remaining card
        }

        // Full house or Three of a kind checker
        List<Card> threeOfAKindList = sameCardRankMap.values().stream()
                .filter(l -> l.size() == 3)
                .findFirst()
                .orElse(new ArrayList<>());

        if (!threeOfAKindList.isEmpty()) {
            sameCardRankMap.remove(threeOfAKindList.get(0).getRank().toString());
            List<Card> fullHouseKickerList = sameCardRankMap.values().stream()
                    .filter(l -> l.size() >= 2)
                    .findFirst()
                    .orElse(new ArrayList<>());

            if (!fullHouseKickerList.isEmpty()) {
                return new FullHouse(threeOfAKindList, fullHouseKickerList.subList(0, 2)); // Pair for the full house
            } else {
                combinedCards.removeAll(threeOfAKindList);
                return new ThreeOfAKind(threeOfAKindList, combinedCards.subList(0, 2)); // Kickers are the top 2 remaining cards
            }
        }

        // Check for two pair or one pair
        List<Card> twoOfAKindList = sameCardRankMap.values().stream()
                .filter(l -> l.size() == 2)
                .findFirst()
                .orElse(new ArrayList<>());

        if (!twoOfAKindList.isEmpty()) {
            sameCardRankMap.remove(twoOfAKindList.get(0).getRank().toString());
            List<Card> secondPairList = sameCardRankMap.values().stream()
                    .filter(l -> l.size() == 2)
                    .findFirst()
                    .orElse(new ArrayList<>());

            if (!secondPairList.isEmpty()) {
                combinedCards.removeAll(twoOfAKindList);
                combinedCards.removeAll(secondPairList);
                return new TwoPair(twoOfAKindList, secondPairList, combinedCards.subList(0, 1)); // Kicker is top remaining card
            } else {
                combinedCards.removeAll(twoOfAKindList);
                return new OnePair(twoOfAKindList, combinedCards.subList(0, 3)); // Kickers are the top 3 remaining cards
            }
        }

        // Check for straight (no flush)
        if (isStraight(combinedCards)) {
            return new Straight(combinedCards.subList(0, 5)); // Return the top 5 cards of the straight
        }

        // Default to high card
        return new HighCard(combinedCards.subList(0, 5)); // Return the top 5 cards
    }

    /**
     * Checks if the given list of cards forms a straight.
     *
     * @param cards List of cards sorted in descending order
     * @return true if the list forms a straight, otherwise false
     */
    private boolean isStraight(List<Card> cards) {
        List<Integer> ranks = cards.stream()
                .map(card -> card.getRank().ordinal())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Special case for Ace-low straight
        if (ranks.contains(CardRank.ACE.ordinal()) &&
                ranks.contains(CardRank.TWO.ordinal()) &&
                ranks.contains(CardRank.THREE.ordinal()) &&
                ranks.contains(CardRank.FOUR.ordinal()) &&
                ranks.contains(CardRank.FIVE.ordinal())) {
            return true;
        }

        // Check for a normal straight
        for (int i = 0; i < ranks.size() - 4; i++) {
            if (ranks.get(i) - ranks.get(i + 4) == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given list of cards forms a Royal Flush.
     *
     * @param cards List of cards all of the same suit
     * @return true if the list forms a Royal Flush, otherwise false
     */
    private boolean isRoyalFlush(List<Card> cards)
    {
        if (cards.size() < 5)
            return false; // Ensure there are enough cards

        // Check if all cards are of the same suit
        CardSuit cardSuit = cards.get(0).getSuit();
        if (!cards.stream().allMatch(card -> card.getSuit() == cardSuit))
        {
            return false;
        }

        // Define the Royal Flush ranks
        Set<CardRank> royalRanks = EnumSet.of(CardRank.TEN, CardRank.JACK,
                CardRank.QUEEN, CardRank.KING, CardRank.ACE);
        Set<CardRank> cardRanks = cards.stream()
                .map(Card::getRank)
                .collect(Collectors.toSet());

        return cardRanks.containsAll(royalRanks);
    }
}
