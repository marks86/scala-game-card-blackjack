package com.gmail.namavirs86.game.blackjack

import akka.actor.Props
import com.gmail.namavirs86.game.blackjack.Definitions._
import com.gmail.namavirs86.game.blackjack.utils.CardUtils
import com.gmail.namavirs86.game.card.core.{Behavior, BehaviorMessages}
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException

object BlackjackBehavior extends BehaviorMessages {
  def props(settings: BehaviorSettings): Props = Props(new BlackjackBehavior(settings))
}

final class BlackjackBehavior(settings: BehaviorSettings) extends Behavior[BlackjackContext] {
  val id = "blackjackBehavior"

  private val cardUtils = new CardUtils()

  def process(flow: Flow[BlackjackContext]): Option[BlackjackContext] = Some {
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())

    Array(
      updatePlayerContext _,
      updateDealerContext _,
      updateRoundEnded _,
      determineOutcome _,
      calculateWin _
    )
      .foldLeft(gameContext) {
        case (ctx, fn) ⇒ fn(ctx)
      }
  }

  private def updatePlayerContext(gameContext: BlackjackContext): BlackjackContext = {
    val cardValues = settings.cardValues
    val bjValue = settings.bjValue
    val player = gameContext.player

    gameContext.copy(
      player = player.copy(
        value = cardUtils.calculateHandValue(player.hand, cardValues, bjValue),
        hasBJ = isBlackjack(player.hand)
      )
    )
  }

  private def updateDealerContext(gameContext: BlackjackContext): BlackjackContext = {
    val bjValue = settings.bjValue
    val cardValues = settings.cardValues
    val dealer = gameContext.dealer
    val player = gameContext.player
    val hasHoleCard = dealer.holeCard.nonEmpty

    val hasBJ = if (hasHoleCard)
      isBlackjack(dealer.hand ++ dealer.holeCard) else dealer.hasBJ

    val revealHoleCard = hasHoleCard &&
      (hasBJ ||
        player.hasBJ ||
        cardUtils.isBust(player.value, bjValue))

    val (hand, holeCard) = if (revealHoleCard)
      (dealer.hand ++ dealer.holeCard, None) else (dealer.hand, dealer.holeCard)

    val value = cardUtils.calculateHandValue(hand, cardValues, bjValue)

    gameContext.copy(
      dealer = dealer.copy(
        hand = hand,
        holeCard = holeCard,
        value = value,
        hasBJ = hasBJ,
      )
    )
  }

  private def updateRoundEnded(gameContext: BlackjackContext): BlackjackContext = {
    val dealer = gameContext.dealer
    val player = gameContext.player
    val bjValue = settings.bjValue

    val roundEnded = cardUtils.isBust(player.value, bjValue) ||
      dealer.value >= settings.dealerStandValue ||
      dealer.hasBJ ||
      player.hasBJ

    gameContext.copy(
      roundEnded = roundEnded,
    )
  }

  private def determineOutcome(gameContext: BlackjackContext): BlackjackContext = {
    val roundEnded = gameContext.roundEnded

    if (!roundEnded) {
      return gameContext
    }

    val bjValue = settings.bjValue
    val dealer = gameContext.dealer
    val player = gameContext.player
    val diff = player.value - dealer.value

    val outcome = diff match {
      case _ if cardUtils.isBust(player.value, bjValue) ⇒ Outcome.DEALER
      case _ if cardUtils.isBust(dealer.value, bjValue) ⇒ Outcome.PLAYER
      case x if x > 0 ⇒ Outcome.PLAYER
      case x if x < 0 ⇒ Outcome.DEALER
      case x if x == 0 ⇒ Outcome.TIE
    }

    gameContext.copy(
      outcome = Some(outcome),
    )
  }

  private def calculateWin(gameContext: BlackjackContext): BlackjackContext = {
    val outcome = gameContext.outcome
    val bet = gameContext.bet.getOrElse(0f)

    val multiplier = () ⇒ {
      val hasBJ = gameContext.player.hasBJ
      if (hasBJ) settings.bjPayoutMultiplier else settings.payoutMultiplier
    }

    val totalWin = outcome match {
      case Some(Outcome.PLAYER) ⇒ bet * multiplier()
      case Some(Outcome.TIE) ⇒ bet
      case Some(Outcome.DEALER) ⇒ 0
      case None ⇒ 0
    }

    gameContext.copy(
      totalWin = totalWin,
    )
  }

  private def isBlackjack(hand: Hand): Boolean = {
    val cardValues = settings.cardValues
    val bjValue = settings.bjValue

    // takes first two cards
    val h = hand.slice(0, 2)
    cardUtils.calculateHandValue(h, cardValues, bjValue) == settings.bjValue
  }
}
