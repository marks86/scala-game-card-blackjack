package com.gmail.namavirs86.game.blackjack

import com.gmail.namavirs86.game.core.Definitions.CardValues

object Definitions {

  case class BehaviorSettings(
                               cardValues: CardValues,
                               bjValue: Int,
                               dealerStandValue: Int,
                               softValue: Int,
                               bjPayoutMultiplier: Float,
                               payoutMultiplier: Float,
                             )

}
