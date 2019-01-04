package com.gmail.namavirs86.blackjack

import com.gmail.namavirs86.blackjack.RequestType.RequestType

final case class RequestContext(
                                 requestId: Long,
                                 requestType: RequestType
                               )

final case class GameContext()

final case class Context(
                          requestContext: RequestContext,
                          gameContext: GameContext
                        )

object RequestType extends Enumeration {
  type RequestType = Value
  val DEAL, HIT, STAND, DOUBLE, SPLIT = Value
}

final case class GameConfig(
                             id: String,
                             actions: Map[RequestType, String],
                             responseAdapter: String
                           )


