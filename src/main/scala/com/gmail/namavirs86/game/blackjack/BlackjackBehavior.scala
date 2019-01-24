package com.gmail.namavirs86.game.blackjack

import akka.actor.Props
import com.gmail.namavirs86.game.blackjack.Definitions.BehaviorSettings
import com.gmail.namavirs86.game.blackjack.utils.CardUtils
import com.gmail.namavirs86.game.card.core.{Behavior, BehaviorMessages}
import com.gmail.namavirs86.game.card.core.Definitions._

object BlackjackBehavior extends BehaviorMessages {
  def props(settings: BehaviorSettings): Props = Props(new BlackjackBehavior(settings))
}

final class BlackjackBehavior(settings: BehaviorSettings) extends Behavior {
  val id = "blackjackBehavior"

  private val cardUtils = new CardUtils()

  def process(flow: Flow): Unit = {

    updatePlayerContext(flow)

    updateDealerContext(flow)

    updateRoundEnded(flow)

    determineOutcome(flow)

    calculateWin(flow)
  }

  private def updatePlayerContext(flow: Flow): Unit = {
    val cardValues = settings.cardValues
    val bjValue = settings.bjValue
    val player = flow.gameContext.player

    player.value = cardUtils.calculateHandValue(player.hand, cardValues, bjValue)
    player.hasBJ = isBlackjack(player.hand)
  }

  private def updateDealerContext(flow: Flow): Unit = {
    val dealer = flow.gameContext.dealer
    val player = flow.gameContext.player
    val hasHoleCard = dealer.holeCard.nonEmpty

    if (hasHoleCard) {
      dealer.hasBJ = isBlackjack(dealer.hand ++ dealer.holeCard)
    }

    val bjValue = settings.bjValue
    val hasBJ = dealer.hasBJ || player.hasBJ
    val isPlayerBust = cardUtils.isBust(player.value, bjValue)

    if (hasHoleCard && (hasBJ || isPlayerBust)) {
      dealer.hand += dealer.holeCard.get
      dealer.holeCard = None
    }

    val cardValues = settings.cardValues
    dealer.value = cardUtils.calculateHandValue(dealer.hand, cardValues, bjValue)
  }

  private def updateRoundEnded(flow: Flow): Unit = {
    val isStand = flow.requestContext.action == ActionType.STAND
    val gameContext = flow.gameContext
    val dealer = gameContext.dealer
    val player = gameContext.player
    val bjValue = settings.bjValue

    gameContext.roundEnded = isStand ||
      cardUtils.isBust(player.value, bjValue) ||
      dealer.value >= settings.dealerStandValue ||
      dealer.hasBJ ||
      player.hasBJ
  }

  private def determineOutcome(flow: Flow): Unit = {
    val roundEnded = flow.gameContext.roundEnded

    if (!roundEnded) {
      return
    }

    val bjValue = settings.bjValue
    val dealer = flow.gameContext.dealer
    val player = flow.gameContext.player
    val diff = player.value - dealer.value

    val outcome = diff match {
      case _ if cardUtils.isBust(player.value, bjValue) ⇒ Outcome.DEALER
      case _ if cardUtils.isBust(dealer.value, bjValue) ⇒ Outcome.PLAYER
      case x if x > 0 ⇒ Outcome.PLAYER
      case x if x < 0 ⇒ Outcome.DEALER
      case x if x == 0 ⇒ Outcome.TIE
    }

    flow.gameContext.outcome = Some(outcome)
  }

  private def calculateWin(flow: Flow): Unit = {
    val gameContext = flow.gameContext
    val outcome = gameContext.outcome
    val bet = gameContext.bet

    gameContext.totalWin = outcome match {
      case Some(Outcome.PLAYER) ⇒
        val hasBJ = gameContext.player.hasBJ
        val multiplier = if (hasBJ) settings.bjPayoutMultiplier else settings.payoutMultiplier
        bet * multiplier
      case Some(Outcome.TIE) ⇒ bet
      case Some(Outcome.DEALER) ⇒ 0
      case None ⇒ 0
    }
  }

  private def isBlackjack(hand: Hand): Boolean = {
    val cardValues = settings.cardValues
    val bjValue = settings.bjValue

    // takes first two cards
    val h = hand.slice(0, 2)
    cardUtils.calculateHandValue(h, cardValues, bjValue) == settings.bjValue
  }
}
