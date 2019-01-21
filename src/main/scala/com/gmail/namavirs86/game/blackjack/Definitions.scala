package com.gmail.namavirs86.game.blackjack

import com.gmail.namavirs86.game.core.Definitions.{CardValues, ShoeManagerSettings}

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
                                dealerStandValue: Int,
                                dealerSoftValue: Int,
                                cardValues: CardValues,
                                bjValue: Int,
                                )
}
