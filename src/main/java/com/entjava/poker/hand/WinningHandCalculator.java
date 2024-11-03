package com.entjava.poker.hand;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A service class used to calculate the winning hand.
 */
@Component
public class WinningHandCalculator {

	/**
	 * @param playerHands
	 * @return The winning {@link Hand} from a list of player hands.
	 */
	public Optional<Hand> calculateWinningHand(List<Hand> playerHands) {
        if (playerHands.isEmpty()) {
            return Optional.empty();
        }

        // Sort hands by rank (implemented in Hand.compareTo())
        Collections.sort(playerHands, Collections.reverseOrder());
        
        // Get highest ranked hand
        Hand bestHand = playerHands.get(0);
        
        // Check for ties
        List<Hand> tiedHands = playerHands.stream()
            .filter(hand -> hand.compareTo(bestHand) == 0)
            .collect(Collectors.toList());

        if (tiedHands.size() > 1) {
            // Handle ties by comparing kickers
            return Optional.of(compareKickers(tiedHands));
        }

        return Optional.of(bestHand);
    }

    private Hand compareKickers(List<Hand> tiedHands) {
        // If there's only one hand, return it
        if (tiedHands.size() == 1) {
            return tiedHands.get(0);
        }

        // Compare each card in the hands
        for (int i = 0; i < tiedHands.get(0).getCurrentHand().size(); i++) {
            final int cardIndex = i;
            int highestRank = tiedHands.stream()
                .mapToInt(hand -> hand.getCurrentHand().get(cardIndex).getRank().ordinal())
                .max()
                .getAsInt();

            // Filter to hands that have the highest rank card at this position
            tiedHands = tiedHands.stream()
                .filter(hand -> hand.getCurrentHand().get(cardIndex).getRank().ordinal() == highestRank)
                .collect(Collectors.toList());

            // If we've filtered down to one hand, we have a winner
            if (tiedHands.size() == 1) {
                return tiedHands.get(0);
            }
        }

        // If we get here, the hands are exactly equal
        return tiedHands.get(0);
    }
}
