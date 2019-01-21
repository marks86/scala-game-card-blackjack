package com.gmail.namavirs86.game

import com.gmail.namavirs86.game.core.Definitions.{CardValues, GameConfig, Rank}
import com.gmail.namavirs86.game.core.Game

package object blackjack {

  val cardValues: CardValues = Map(
    Rank.TWO → 2,
    Rank.THREE → 3,
    Rank.FOUR → 4,
    Rank.FIVE → 5,
    Rank.SIX → 6,
    Rank.SEVEN → 7,
    Rank.EIGHT → 8,
    Rank.NINE → 9,
    Rank.TEN → 10,
    Rank.JACK → 10,
    Rank.QUEEN → 10,
    Rank.KING → 10,
    Rank.ACE → 11
  )

  //  val config: GameConfig = {
  //
  //  }
  //
  //  def create(): Game = {
  //
  //  }
}
