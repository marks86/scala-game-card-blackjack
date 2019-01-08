package com.gmail.namavirs86.game.core

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.Rank.Rank
import com.gmail.namavirs86.game.core.Definitions.RequestType.RequestType
import com.gmail.namavirs86.game.core.Definitions.Suit.Suit
import com.gmail.namavirs86.game.core.actions.BaseAction

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Definitions {

  final case class GameConfig(
                               id: String,
                               actions: Map[RequestType, Props],
                               responseAdapter: Props
                             )

  final case class RequestContext(
                                   requestId: Long,
                                   requestType: RequestType
                                 )

  final case class GameContext(
                              dealerHand: ListBuffer[Card],
                              playerHand: ListBuffer[Card],
                              var roundEnded: Boolean,
                              )

  final case class Flow(
                            requestContext: RequestContext,
                            gameContext: GameContext,
                            rng: Random
                          )

  object RequestType {

    sealed abstract class RequestType

    case object DEAL extends RequestType

    case object HIT extends RequestType

    case object STAND extends RequestType

    case object DOUBLE extends RequestType

    case object SPLIT extends RequestType

  }

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
