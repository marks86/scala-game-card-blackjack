package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException

object DealAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new DealAction(shoeSettings))
}

final class DealAction(shoeSettings: ShoeManagerSettings) extends BaseAction {
  val id = "dealAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow): Option[GameContext] = {
    val initShoe = flow.gameContext match {
      case Some(context: GameContext) ⇒ context.shoe
      case None ⇒ List.empty[Card]
    }

    val rng = flow.rng
    val draw1 = shoeManager.draw(rng, initShoe, isNewRound = true)
    val draw2 = shoeManager.draw(rng, draw1._2)
    val draw3 = shoeManager.draw(rng, draw2._2)
    val draw4 = shoeManager.draw(rng, draw3._2)

    val dealerHand = List(draw1._1)
    val playerHand = List(draw2._1, draw3._1)
    val holeCard = Some(draw4._1)
    val shoe = draw4._2

    Some(GameContext(
      dealer = DealerContext(
        hand = dealerHand,
        value = 0,
        holeCard = holeCard,
        hasBJ = false,
      ),
      player = PlayerContext(
        hand = playerHand,
        value = 0,
        hasBJ = false,
      ),
      shoe = shoe,
      bet = None,
      totalWin = 0f,
      outcome = None,
      roundEnded = true,
    ))
  }

  // @TODO: validate deal action request
  def validateRequest(flow: Flow): Unit = {}

}
