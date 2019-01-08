package com.gmail.namavirs86.game.core.random

import scala.collection.mutable.ListBuffer
import scala.util.Random

class RandomCheating(val cheat: ListBuffer[Int] = ListBuffer[Int]()) extends Random {

  override def nextInt(n: Int): Int = {
    if (cheat.nonEmpty)
      cheat.remove(0) else super.nextInt(n)
  }
}
