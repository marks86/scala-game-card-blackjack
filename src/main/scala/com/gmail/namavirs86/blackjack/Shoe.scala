package com.gmail.namavirs86.blackjack

import com.gmail.namavirs86.blackjack.Definitions._
import scala.util.Random

class Shoe(rng: Random, deckCount: Int) {

  private var shoe = List[Card]()

  def init(): Unit = {
    shoe = createShoe()
  }

  def draw: Card = {
    val index = rng.nextInt(shoe.length)
    shoe(index)
  }

  private def createShoe(): List[Card] = {
    for (
      s ← Suit.suits;
      r ← Rank.ranks;
      i ← 1 to deckCount
    )
      yield Card(r, s)
  }
}
