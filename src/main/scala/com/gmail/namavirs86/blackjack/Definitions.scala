package com.gmail.namavirs86.blackjack

import com.gmail.namavirs86.blackjack.Definitions.CardValue.CardValue
import com.gmail.namavirs86.blackjack.Definitions.RequestType.RequestType
import com.gmail.namavirs86.blackjack.Definitions.Suit.Suit

import scala.collection.mutable.ListBuffer

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

  object Suit extends Enumeration {
    type Suit = Value
    val CLUBS, DIAMONDS, HEARTS, SPADES = Value
  }

  object CardValue extends Enumeration {
    type CardValue = Value
    val TWO = Value(2)
    val THREE = Value(3)
    val FOUR = Value(4)
    val FIVE = Value(5)
    val SIX = Value(6)
    val SEVEN = Value(7)
    val EIGHT = Value(8)
    val NINE = Value(9)
    val TEN = Value(10)
    val JACK = Value(10)
    val QUEEN = Value(10)
    val KING = Value(10)
    val ACE = Value(11)
  }

  case class Card(value: CardValue, suit: Suit)

  type Deck = ListBuffer[Card]
}

