package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.Shoe
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}

object HitAction extends BaseActionMessages {
  def props: Props = Props(new HitAction())
}

class HitAction extends BaseAction {
  val id = "hitAction"

  private val shoe = new Shoe(1)

  def process(flow: Flow): Unit = {
    val playerHand = flow.gameContext.playerHand
    val rng = flow.rng

    playerHand += shoe.draw(rng)
  }

  def validateRequest(flow: Flow): Unit = {}
}
