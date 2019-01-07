package com.gmail.namavirs86.blackjack

import com.gmail.namavirs86.blackjack.Definitions.{Card, Rank, Suit}
import com.gmail.namavirs86.blackjack.random.RandomCheating
import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.mutable.ListBuffer

class ShoeSpec extends WordSpecLike with Matchers {

  "A Shoe" should {
    "draw a card" in {
      val cheat = ListBuffer[Int](0)
      val rng = new RandomCheating(cheat)
      val shoe = new Shoe(rng, 1)
      shoe.init()

      val card = shoe.draw
      card shouldBe Card(Rank.TWO, Suit.CLUBS)
    }
  }
}
