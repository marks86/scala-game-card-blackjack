package com.gmail.namavirs86.game

import akka.actor.Props
import com.gmail.namavirs86.game.blackjack.Definitions.{BehaviorSettings, BlackjackActionType, CardValues, StandActionSettings}
import com.gmail.namavirs86.game.blackjack.actions.{DealAction, HitAction, StandAction}
import com.gmail.namavirs86.game.blackjack.adapters.ResponseAdapter
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Game

package object blackjack {

  private val cardValues: CardValues = Map(
    Rank.TWO → 2,
    Rank.THREE → 3,
    Rank.FOUR → 4,
    Rank.FIVE → 5,
    Rank.SIX → 6,
    Rank.SEVEN → 7,
    Rank.EIGHT → 8,
    Rank.NINE → 9,
    Rank.TEN → 10,
    Rank.JACK → 10,
    Rank.QUEEN → 10,
    Rank.KING → 10,
    Rank.ACE → 11
  )

  private val shoeManagerSettings = ShoeManagerSettings(
    deckCount = 6,
    cutCardPosition = 40,
  )

  private def config = GameConfig(
    id = "bj",
    actions = Map(
      BlackjackActionType.DEAL → DealAction.props(
        shoeManagerSettings
      ),
      BlackjackActionType.HIT → HitAction.props(
        shoeManagerSettings
      ),
      BlackjackActionType.STAND → StandAction.props(
        StandActionSettings(
          shoeSettings = shoeManagerSettings,
          cardValues = cardValues,
          dealerStandValue = 17,
          dealerSoftValue = 17,
          bjValue = 21,
        )
      )
    ),
    responseAdapter = ResponseAdapter.props,
    behavior = BlackjackBehavior.props(
      BehaviorSettings(
        cardValues = cardValues,
        bjValue = 21,
        dealerStandValue = 17,
        softValue = 17,
        bjPayoutMultiplier = 3f / 2f,
        payoutMultiplier = 2,
      )
    )
  )

  val id: String = config.id

  def props: Props = Game.props(config)

}
