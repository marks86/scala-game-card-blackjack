package com.gmail.namavirs86.game.core.random

import scala.collection.mutable.ListBuffer
import scala.util.Random

class RandomCheating(var cheat: ListBuffer[Int] = ListBuffer[Int]()) extends Random {

  override def nextInt(n: Int): Int = {
    if (cheat.nonEmpty) {
      val head = cheat.head
      cheat = cheat.tail
      return head
    }

    super.nextInt(n)
  }
}
