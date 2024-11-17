package org.scalajs.dom.webgl.extensions

import scala.scalajs.js

/** Provides ETC1 texture compression support. (Community Extension)
  *
  * @see
  *   https://www.khronos.org/registry/webgl/extensions/WEBGL_compressed_texture_pvrtc/
  */
@js.native
trait WEBGLCompressedTextureETC1 extends js.Object {
  val COMPRESSED_RGB_ETC1_WEBGL: Int = js.native
}
