/** All documentation for facades is thanks to Mozilla Contributors at https://developer.mozilla.org/en-US/docs/Web/API
  * and available under the Creative Commons Attribution-ShareAlike v2.5 or later.
  * http://creativecommons.org/licenses/by-sa/2.5/
  *
  * Everything else is under the MIT License http://opensource.org/licenses/MIT
  */
package org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation._

/** The ClipboardEvent interface represents events providing information related to modification of the clipboard, that
  * is cut, copy, and paste events.
  */
@js.native
@JSGlobal
class ClipboardEvent(typeArg: String, init: js.UndefOr[ClipboardEventInit] = js.undefined)
    extends Event(typeArg, init) {

  /** Is a DataTransfer object containing the data affected by the user-initialed cut, copy, or paste operation, along
    * with its MIME type.
    */
  def clipboardData: DataTransfer = js.native
}
