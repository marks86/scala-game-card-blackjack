package com.gmail.namavirs86.game.blackjack

import com.gmail.namavirs86.game.card.core.Definitions.{ActionType, CardValues, ShoeManagerSettings}

object Definitions {

  case class BehaviorSettings(
                               cardValues: CardValues,
                               bjValue: Int,
                               dealerStandValue: Int,
                               softValue: Int,
                               bjPayoutMultiplier: Float,
                               payoutMultiplier: Float,
                             )

  case class StandActionSettings(
                                  shoeSettings: ShoeManagerSettings,
                                  cardValues: CardValues,
                                  dealerStandValue: Int,
                                  dealerSoftValue: Int,
                                  bjValue: Int,
                                )

  object BlackjackActionType {

    val DEAL: ActionType = "DEAL"

    val HIT: ActionType = "HIT"

    val STAND: ActionType = "STAND"
  }

}
