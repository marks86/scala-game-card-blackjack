package com.gmail.namavirs86.app

import akka.actor.ActorSystem

import scala.io.StdIn

object App {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("iot-system")

    try {
      // Create top level supervisor
      val supervisor = system.actorOf(AppSupervisor.props(), "iot-supervisor")
      // Exit the system after ENTER is pressed
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }

}
