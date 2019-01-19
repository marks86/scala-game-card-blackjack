package com.gmail.namavirs86.game.core

import com.gmail.namavirs86.game.core.Definitions._

import scala.collection.mutable.ListBuffer
import scala.util.Random

class ShoeManager(settings: ShoeManagerSettings) {

  def draw(rng: Random, shoe: Shoe, isNewRound: Boolean): (Card, Shoe) = {
    val resultShoe =
      if (isNewRound && checkReShuffle(shoe)) shuffle(rng) else shoe

    val (card, _) = draw(rng, resultShoe)

    (card, resultShoe)
  }

  def draw(rng: Random, shoe: Shoe): (Card, Shoe) = {
    val card = shoe.remove(0)
    (card, shoe)
  }

  private def checkReShuffle(shoe: Shoe): Boolean = {
    val shoeMaxLen = Rank.ranks.length * Suit.suits.length * settings.deckCount
    shoeMaxLen - shoe.length >= settings.cutCardPosition
  }

  private def shuffle(rng: Random): Shoe = {
    val shoe = for (
      s ← Suit.suits;
      r ← Rank.ranks;
      i ← 1 to settings.deckCount
    )
      yield Card(r, s)

    rng.shuffle(shoe).to[ListBuffer]
  }
}
