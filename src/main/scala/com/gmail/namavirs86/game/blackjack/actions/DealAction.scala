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

  def process(flow: Flow): Unit = {
    flow.gameContext = Some(createDefaultGameContext(flow))

    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val dealer = gameContext.dealer
    val player = gameContext.player

    dealer.hand = dealer.hand :+ drawCard(flow, isNewRound = true)
    player.hand = player.hand :+ drawCard(flow)
    dealer.holeCard = Some(drawCard(flow))
    player.hand = player.hand :+ drawCard(flow)

    gameContext.bet = flow.requestContext.bet
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

  private def createDefaultGameContext(flow: Flow): GameContext = {
    val shoe = flow.gameContext match {
      case Some(context: GameContext) ⇒ context.shoe
      case None ⇒ List.empty[Card]
    }

    GameContext(
      dealer = DealerContext(
        hand = List.empty[Card],
        value = 0,
        holeCard = None,
        hasBJ = false,
      ),
      player = PlayerContext(
        hand = List.empty[Card],
        value = 0,
        hasBJ = false,
      ),
      shoe = shoe,
      bet = None,
      totalWin = 0f,
      outcome = None,
      roundEnded = true,
    )
  }
}
