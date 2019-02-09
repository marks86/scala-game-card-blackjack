package com.gmail.namavirs86.game.blackjack.utils

import com.gmail.namavirs86.game.blackjack.Definitions.{CardValues, Hand}
import com.gmail.namavirs86.game.card.core.Definitions.{Card, Rank}

class CardUtils {

  def calculateHandValue(hand: Hand, cardValues: CardValues, maxValue: Int): Int = {
    hand.foldLeft(0)((sum: Int, card: Card) ⇒ {
      val value = cardValues.getOrElse(card.rank, 0)
      val isAce = card.rank.equals(Rank.ACE)
      val isBust = this.isBust(sum + value, maxValue)

      val cardValue = if (isBust && isAce) 1 else value
      sum + cardValue
    })
  }

  def isBust(handValue: Int, maxValue: Int): Boolean = {
    handValue > maxValue
  }
}
