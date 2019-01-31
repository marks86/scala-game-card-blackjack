package com.gmail.namavirs86.game.blackjack.adapters

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.adapters.{BaseResponseAdapter, BaseResponseAdapterMessages}
import spray.json._
import com.gmail.namavirs86.game.blackjack.protocols.ResponseJsonProtocol._

object ResponseAdapter extends BaseResponseAdapterMessages {
  def props: Props = Props(new ResponseAdapter())
}

final class ResponseAdapter extends BaseResponseAdapter {
  val id = "responseAdapter"

  def process(flow: Flow): Option[JsValue] = {
    flow.gameContext match {
      case Some(gameContext: GameContext) ⇒
        val dealer = gameContext.dealer
        val player = gameContext.player
        Some(GamePlayResponse(
          dealer = ResponseDealerContext(
            hand = dealer.hand,
            value = dealer.value,
            hasBJ = dealer.hasBJ,
          ),
          player = ResponsePlayerContext(
            hand = player.hand,
            value = player.value,
            hasBJ = player.hasBJ,
          ),
          outcome = gameContext.outcome,
          bet = flow.requestContext.bet,
          totalWin = gameContext.totalWin,
          roundEnded = gameContext.roundEnded,
        ).toJson)
      case None ⇒ None
    }
  }
}
