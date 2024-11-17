package org.scalajs.dom

import scala.scalajs.js

/** Fetch API's [[https://fetch.spec.whatwg.org/#requestmode RequestMode enum]] */
opaque type RequestMode <: String = String

object RequestMode {
  val navigate: RequestMode = "navigate"
  val `same-origin`: RequestMode = "same-origin"
  val `no-cors`: RequestMode = "no-cors"
  val cors: RequestMode = "cors"
}
