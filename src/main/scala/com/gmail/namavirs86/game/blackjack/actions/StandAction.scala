package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions._
import com.gmail.namavirs86.game.core.ShoeManager
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}
import com.gmail.namavirs86.game.blackjack.Definitions.StandActionSettings
import com.gmail.namavirs86.game.blackjack.utils.CardUtils

object StandAction extends BaseActionMessages {
  def props(settings: StandActionSettings): Props = Props(new StandAction(settings))
}

final class StandAction(settings: StandActionSettings) extends BaseAction {
  val id = "standAction"

  private val StandActionSettings(
  shoeSettings,
  dealerStandValue,
  dealerSoftValue,
  cardValues,
  bjValue,
  ) = settings

  private val shoeManager = new ShoeManager(shoeSettings)
  private val cardUtils = new CardUtils()

  def process(flow: Flow): Unit = {
    val gameContext = flow.gameContext
    val dealer = gameContext.dealer

    dealer.hand += dealer.holeCard.get
    dealer.holeCard = None
    dealer.value = calcHandValue(dealer.hand)

    while (dealer.value < dealerStandValue || hasSoft(dealer)) {
      dealer.hand += drawCard(flow)
      dealer.value = calcHandValue(dealer.hand)
    }
  }

  def validateRequest(flow: Flow): Unit = {}

  private def hasSoft(dealer: DealerContext): Boolean = {
    val hasAce = dealer.hand.head.rank.equals(Rank.ACE)
    hasAce && dealer.value == dealerSoftValue
  }

  private def drawCard(flow: Flow): Card = {
    val rng = flow.rng
    val gameContext = flow.gameContext
    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe)
    gameContext.shoe = shoe
    card
  }

  private def calcHandValue(hand: Hand): Int =
    cardUtils.calculateHandValue(hand, cardValues, bjValue)
}
