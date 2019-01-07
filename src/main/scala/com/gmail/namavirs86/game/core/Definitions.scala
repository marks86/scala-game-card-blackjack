package com.gmail.namavirs86.game.core

import com.gmail.namavirs86.game.core.Definitions.Rank.Rank
import com.gmail.namavirs86.game.core.Definitions.RequestType.RequestType
import com.gmail.namavirs86.game.core.Definitions.Suit.Suit

object Definitions {

  final case class RequestContext(
                                   requestId: Long,
                                   requestType: RequestType
                                 )

  final case class GameContext(
                                //  var a: Long
                              )

  final case class Context(
                            requestContext: RequestContext,
                            gameContext: GameContext
                          )

  object RequestType extends Enumeration {
    type RequestType = Value
    val DEAL, HIT, STAND, DOUBLE, SPLIT = Value
  }

  final case class GameConfig(
                               id: String,
                               actions: Map[RequestType, String],
                               responseAdapter: String
                             )

  object Suit {

    sealed abstract class Suit

    case object CLUBS extends Suit

    case object SPADES extends Suit

    case object HEARTS extends Suit

    case object DIAMONDS extends Suit

    val suits = List(CLUBS, SPADES, HEARTS, DIAMONDS)
  }

  object Rank {

    sealed abstract class Rank

    case object TWO extends Rank

    case object THREE extends Rank

    case object FOUR extends Rank

    case object FIVE extends Rank

    case object SIX extends Rank

    case object SEVEN extends Rank

    case object EIGHT extends Rank

    case object NINE extends Rank

    case object TEN extends Rank

    case object JACK extends Rank

    case object QUEEN extends Rank

    case object KING extends Rank

    case object ACE extends Rank

    val ranks = List(TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE)
  }

  case class Card(rank: Rank, suit: Suit)

}
