package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.blackjack.Definitions.StandActionSettings
import com.gmail.namavirs86.game.blackjack.utils.CardUtils
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}

import scala.util.Random

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
    val rng = flow.rng

    val (hand, value, shoe) = drawToDealer(gameContext, rng)

    Some(gameContext.copy(
      dealer.copy(
        hand = hand,
        value = value,
        holeCard = None,
      ),
      shoe = shoe
    ))
  }

  // @TODO: validate stand action process
  def validateRequest(flow: Flow): Unit = {}

  private def drawToDealer(gameContext: GameContext, rng: Random): (Hand, Int, Shoe) = {
    val dealer = gameContext.dealer
    val initHand = dealer.hand ++ dealer.holeCard
    val initValue = calcHandValue(initHand)
    val initShoe = gameContext.shoe

    Stream.iterate((initHand, initValue, initShoe))(input ⇒ {
      val (h, _, s) = input
      val (card, shoe) = shoeManager.draw(rng, s)
      val hand = h :+ card
      val value = calcHandValue(hand)
      (hand, value, shoe)
    })
      .dropWhile(output ⇒ {
        val (h, v, _) = output
        v < dealerStandValue || hasSoft(h, v)
      })
      .head
  }

  private def hasSoft(hand: Hand, value: Int): Boolean = {
    hand.length == 2 &&
      hand.head.rank.equals(Rank.ACE) &&
      value == dealerSoftValue
  }

  private def calcHandValue(hand: Hand): Int =
    cardUtils.calculateHandValue(hand, cardValues, bjValue)
}
