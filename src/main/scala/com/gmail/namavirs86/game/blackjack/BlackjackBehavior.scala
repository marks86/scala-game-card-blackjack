package com.gmail.namavirs86.game.blackjack

import akka.actor.Props
import com.gmail.namavirs86.game.blackjack.Definitions.BehaviorSettings
import com.gmail.namavirs86.game.core.{Behavior, BehaviorMessages}
import com.gmail.namavirs86.game.core.Definitions._

object BlackjackBehavior extends BehaviorMessages {
  def props(settings: BehaviorSettings): Props = Props(new BlackjackBehavior(settings))
}

class BlackjackBehavior(settings: BehaviorSettings) extends Behavior {
  val id = "blackjackBehavior"

  def process(flow: Flow): Unit = {

    updatePlayerContext(flow)

    updateDealerContext(flow)

    updateRoundEnded(flow)

    determineOutcome(flow)

    calculateWin(flow)
  }

  private def updatePlayerContext(flow: Flow): Unit = {
    val player = flow.gameContext.player

    player.value = calculateHandValue(player.hand)
    player.hasBJ = isBlackjack(player.hand)
  }

  private def updateDealerContext(flow: Flow): Unit = {
    val dealer = flow.gameContext.dealer
    val player = flow.gameContext.player
    val hasHoleCard = dealer.holeCard.nonEmpty
    val hasBJ = dealer.hasBJ || player.hasBJ

    if (hasHoleCard) {
      dealer.hasBJ = isBlackjack(dealer.hand ++ dealer.holeCard)
    }

    if (hasHoleCard && hasBJ) {
      dealer.hand += dealer.holeCard.get
      dealer.holeCard = None
    }

    dealer.value = calculateHandValue(dealer.hand)
  }

  private def updateRoundEnded(flow: Flow): Unit = {
    val isStand = flow.requestContext.requestType == RequestType.STAND
    val gameContext = flow.gameContext
    val dealer = gameContext.dealer
    val player = gameContext.player

    gameContext.roundEnded = isStand ||
      player.value > settings.bjValue ||
      dealer.value >= settings.dealerStandValue ||
      dealer.hasBJ ||
      player.hasBJ
  }

  private def determineOutcome(flow: Flow): Unit = {
    val roundEnded = flow.gameContext.roundEnded

    if (!roundEnded) {
      return
    }

    val dealer = flow.gameContext.dealer
    val player = flow.gameContext.player

    val diff = player.value - dealer.value

    val outcome = diff match {
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
    // takes first two cards
    val h = hand.slice(0, 2)
    calculateHandValue(h) == settings.bjValue
  }

  private def calculateHandValue(hand: Hand): Int = {
    hand.foldLeft(0)((sum: Int, card: Card) ⇒ {
      sum + settings.cardValues.getOrElse(card.rank, 0)
    })
  }
}
