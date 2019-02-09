package com.gmail.namavirs86.game.blackjack

import com.gmail.namavirs86.game.blackjack.Definitions.Outcome.Outcome
import com.gmail.namavirs86.game.card.core.Definitions.Rank.Rank
import com.gmail.namavirs86.game.card.core.Definitions._

object Definitions {

  final case class BehaviorSettings(
                               cardValues: CardValues,
                               bjValue: Int,
                               dealerStandValue: Int,
                               softValue: Int,
                               bjPayoutMultiplier: Float,
                               payoutMultiplier: Float,
                             )

  final case class StandActionSettings(
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

  type Hand = List[Card]

  object Outcome {

    sealed abstract class Outcome

    case object DEALER extends Outcome

    case object PLAYER extends Outcome

    case object TIE extends Outcome

    val outcomes = List(DEALER, PLAYER, TIE)
  }

  final case class ResponseDealerContext(
                                          hand: Hand,
                                          value: Int,
                                          hasBJ: Boolean,
                                        )

  final case class ResponsePlayerContext(
                                          hand: Hand,
                                          value: Int,
                                          hasBJ: Boolean,
                                        )

  final case class GamePlayResponse(
                                     dealer: ResponseDealerContext,
                                     player: ResponsePlayerContext,
                                     outcome: Option[Outcome],
                                     bet: Option[Float],
                                     totalWin: Float,
                                     roundEnded: Boolean,
                                   )

  final case class PlayerContext(
                                  hand: Hand,
                                  value: Int,
                                  hasBJ: Boolean,
                                )

  final case class DealerContext(
                                  hand: Hand,
                                  value: Int,
                                  holeCard: Option[Card],
                                  hasBJ: Boolean,
                                )

  final case class BlackjackContext(
                                dealer: DealerContext,
                                player: PlayerContext,
                                outcome: Option[Outcome],
                                shoe: Shoe,
                                bet: Option[Float],
                                totalWin: Float,
                                roundEnded: Boolean,
                              ) extends GameContext

  type CardValues = Map[Rank, Int]

}
