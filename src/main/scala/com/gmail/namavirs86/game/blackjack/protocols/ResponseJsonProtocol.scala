package com.gmail.namavirs86.game.blackjack.protocols

import spray.json._
import com.gmail.namavirs86.game.card.core.Definitions.{Card, Rank, Suit}
import com.gmail.namavirs86.game.card.core.Definitions.Rank.Rank
import com.gmail.namavirs86.game.card.core.Definitions.Suit.Suit
import com.gmail.namavirs86.game.card.core.protocols.CoreJsonProtocol
import com.gmail.namavirs86.game.blackjack.Definitions._
import com.gmail.namavirs86.game.blackjack.Definitions.Outcome.Outcome

object ResponseJsonProtocol extends CoreJsonProtocol {

  import DefaultJsonProtocol._

  implicit object suitFormat extends JsonFormat[Suit] {
    val string2Suit: Map[JsValue, Suit] =
      Suit.suits.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): Suit = {
      string2Suit.getOrElse(json, throw DeserializationException("Suit type expected"))
    }

    override def write(obj: Suit): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit object rankFormat extends JsonFormat[Rank] {
    val string2Rank: Map[JsValue, Rank] =
      Rank.ranks.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): Rank = {
      string2Rank.getOrElse(json, throw DeserializationException("Rank type expected"))
    }

    override def write(obj: Rank): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit val cardFormat: RootJsonFormat[Card] = jsonFormat2(Card)

  implicit val responseDealerContextFormat: RootJsonFormat[ResponseDealerContext] = jsonFormat3(ResponseDealerContext)

  implicit object outcomeFormat extends JsonFormat[Outcome] {
    val string2OutcomeType: Map[JsValue, Outcome] =
      Outcome.outcomes.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): Outcome = {
      string2OutcomeType.getOrElse(json, throw DeserializationException("Outcome type expected"))
    }

    override def write(obj: Outcome): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit val responsePlayerContextFormat: RootJsonFormat[ResponsePlayerContext] = jsonFormat3(ResponsePlayerContext)

  implicit val gamePlayResponseFormat: RootJsonFormat[GamePlayResponse] = jsonFormat6(GamePlayResponse)
}
