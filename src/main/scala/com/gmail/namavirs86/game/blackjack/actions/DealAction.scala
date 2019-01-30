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
    val dealerHand = List(drawCard(flow, isNewRound = true))
    val playerHand = List(drawCard(flow), drawCard(flow))
    val holeCard = Some(drawCard(flow))

    val shoe = flow.gameContext match {
      case Some(context: GameContext) ⇒ context.shoe
      case None ⇒ List.empty[Card]
    }

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

  private def drawCard(flow: Flow, isNewRound: Boolean = false): Card = {
    val rng = flow.rng
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe, isNewRound)
    gameContext.shoe = shoe
    card
  }
}
