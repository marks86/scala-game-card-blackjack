package com.gmail.namavirs86.app

import akka.actor.{Actor, ActorLogging, Props}

object AppSupervisor {
  def props(): Props = Props(new AppSupervisor)
}

class AppSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("Application started")
  override def postStop(): Unit = log.info("Application stopped")

  // No need to handle any messages
  override def receive: Receive = Actor.emptyBehavior
}
