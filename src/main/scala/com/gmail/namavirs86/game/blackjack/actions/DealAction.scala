package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.blackjack.Definitions.{BlackjackContext, DealerContext, PlayerContext}
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}
import com.gmail.namavirs86.game.card.core.Definitions._

import scala.util.Random

object DealAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new DealAction(shoeSettings))
}

final class DealAction(shoeSettings: ShoeManagerSettings) extends BaseAction[BlackjackContext] {
  val id = "dealAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow[BlackjackContext]): Option[BlackjackContext] = Some {
    val initShoe = flow.gameContext match {
      case Some(context: BlackjackContext) ⇒ context.shoe
      case None ⇒ List.empty[Card]
    }

    val (cards, shoe) = drawCards(count = 4, initShoe, flow.rng)

    BlackjackContext(
      dealer = DealerContext(
        hand = List(cards.head),
        value = 0,
        holeCard = Some(cards(3)),
        hasBJ = false,
      ),
      player = PlayerContext(
        hand = List(cards(1), cards(2)),
        value = 0,
        hasBJ = false,
      ),
      shoe = shoe,
      bet = flow.requestContext.bet,
      totalWin = 0f,
      outcome = None,
      roundEnded = true,
    )
  }

  // @TODO: validate deal action request
  def validateRequest(flow: Flow[BlackjackContext]): Unit = {}

  private def drawCards(count: Int, shoe: Shoe, rng: Random): (List[Card], Shoe) = {
    Stream.iterate((List.empty[Card], shoe))(input ⇒ {
      val (c, s) = input
      val isNewRound = c.isEmpty
      val (card, resultShoe) = shoeManager.draw(rng, s, isNewRound)
      val cards = c :+ card
      (cards, resultShoe)
    })
      .dropWhile(_._1.length != count)
      .head
  }
}
