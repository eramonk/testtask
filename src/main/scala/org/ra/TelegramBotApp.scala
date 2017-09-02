package org.ra

import info.mukel.telegrambot4s._
import api._
import methods._
import models._
import Implicits._
import info.mukel.telegrambot4s.api.declarative.Commands


object TelegramBotApp extends App{
  MyBot.run()
  "\uD83C\uDD7F️"
}

object MyBot extends TelegramBot with Polling with Commands {
  def token = "381737106:AAH-3ZZeAZtkZtRV-2T_gLmi7S-_7mAItgE"

  onCommand('test){implicit msg => reply(msg.text.getOrElse("!!!"))}



}



