package org.scalajs.dom.webgl.extensions

import scala.scalajs.js

/** Provides 32-bit floating point texture support. (Khronos Extension)
  *
  * @see
  *   https://www.khronos.org/registry/webgl/extensions/OES_texture_float/
  */
@js.native
trait OESTextureHalfFloat extends js.Object {
  val HALF_FLOAT_OES: Int = js.native
}
