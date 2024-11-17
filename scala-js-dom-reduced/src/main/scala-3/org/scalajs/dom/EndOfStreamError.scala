package org.scalajs.dom

opaque type EndOfStreamError <: String = String

object EndOfStreamError {
  val decode: EndOfStreamError = "decode"
  val network: EndOfStreamError = "network"
}
