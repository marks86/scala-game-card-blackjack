package com.gmail.namavirs86.game.core

import com.gmail.namavirs86.game.core.Definitions._

import scala.util.Random

class Shoe(deckCount: Int) {

  private var shoe = List[Card]()

  def init(): Unit = {
    shoe = createShoe()
  }

  def draw(rng: Random): Card = {
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
