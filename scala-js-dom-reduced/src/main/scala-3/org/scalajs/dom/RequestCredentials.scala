package org.scalajs.dom

import scala.scalajs.js

/** Fetch APIs [[https://fetch.spec.whatwg.org/#requestcredentials RequestCredentials enum]] */
opaque type RequestCredentials <: String = String

object RequestCredentials {
  val omit: RequestCredentials = "omit"
  val `same-origin`: RequestCredentials = "same-origin"
  val include: RequestCredentials = "include"
}
