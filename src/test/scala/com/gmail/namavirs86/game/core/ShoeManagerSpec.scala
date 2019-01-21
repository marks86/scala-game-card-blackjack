package com.gmail.namavirs86.game.core

import com.gmail.namavirs86.game.core.Definitions.{Card, Rank, ShoeManagerSettings, Suit}
import com.gmail.namavirs86.game.core.helpers.Helpers
import com.gmail.namavirs86.game.core.random.RandomCheating
import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.mutable.ListBuffer
import scala.util.Random

class ShoeManagerSpec extends WordSpecLike with Matchers {

  val shoeManager = new ShoeManager(Helpers.shoeManagerSettings)

  "Shoe Manager" should {
    "draw a card" in {
      val rng = new Random()
      val shoe = List(
        Card(Rank.ACE, Suit.CLUBS),
        Card(Rank.QUEEN, Suit.HEARTS),
      )

      val (card, resultShoe) = shoeManager.draw(rng, shoe)
      card shouldBe Card(Rank.ACE, Suit.CLUBS)
      resultShoe shouldBe List(Card(Rank.QUEEN, Suit.HEARTS))
    }

    "reshuffle and draw a card" in {
      val cheat = (for(i ← 1 to 51) yield 1).to[ListBuffer]
      val rng = new RandomCheating(cheat)
      val (card, resultShoe) = shoeManager.draw(rng, List(), isNewRound = true)

      card shouldBe Card(Rank.TWO, Suit.CLUBS)
      resultShoe.length shouldBe 51
    }
  }
}
