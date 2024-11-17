package org.scalajs.dom.webgl.extensions

import scala.scalajs.js

/** Provides additional driver and renderer information. (Khronos Extension)
  *
  * @see
  *   https://www.khronos.org/registry/webgl/extensions/WEBGL_debug_renderer_info/
  */
@js.native
trait WebGLDebugRendererInfo extends js.Object {
  val UNMASKED_VENDOR_WEBGL: Int = js.native
  val UNMASKED_RENDERER_WEBGL: Int = js.native
}
