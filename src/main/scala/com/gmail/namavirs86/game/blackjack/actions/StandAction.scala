package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.blackjack.Definitions.StandActionSettings
import com.gmail.namavirs86.game.blackjack.utils.CardUtils
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}

object StandAction extends BaseActionMessages {
  def props(settings: StandActionSettings): Props = Props(new StandAction(settings))
}

final class StandAction(settings: StandActionSettings) extends BaseAction {
  val id = "standAction"

  private val StandActionSettings(
  shoeSettings,
  cardValues,
  dealerStandValue,
  dealerSoftValue,
  bjValue,
  ) = settings

  private val shoeManager = new ShoeManager(shoeSettings)
  private val cardUtils = new CardUtils()

  def process(flow: Flow): Option[GameContext] = {
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val dealer = gameContext.dealer

    var hand = dealer.hand ++ dealer.holeCard
    var value = calcHandValue(hand)
    val hasNext = () ⇒ value < dealerStandValue || hasSoft(hand, value)

    while (hasNext()) {
      hand = hand :+ drawCard(flow)
      value = calcHandValue(hand)
    }

    Some(gameContext.copy(
      dealer.copy(
        hand = hand,
        value = value,
        holeCard = None,
      )
    ))
  }

  // @TODO: validate stand action process
  def validateRequest(flow: Flow): Unit = {}

  private def hasSoft(hand: Hand, value: Int): Boolean = {
    hand.length == 2 &&
      hand.head.rank.equals(Rank.ACE) &&
      value == dealerSoftValue
  }

  private def drawCard(flow: Flow): Card = {
    val rng = flow.rng
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe)
    gameContext.shoe = shoe
    card
  }

  private def calcHandValue(hand: Hand): Int =
    cardUtils.calculateHandValue(hand, cardValues, bjValue)
}
