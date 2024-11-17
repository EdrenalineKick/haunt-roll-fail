/** All documentation for facades is thanks to Mozilla Contributors at https://developer.mozilla.org/en-US/docs/Web/API
  * and available under the Creative Commons Attribution-ShareAlike v2.5 or later.
  * http://creativecommons.org/licenses/by-sa/2.5/
  *
  * Everything else is under the MIT License http://opensource.org/licenses/MIT
  */
package org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

/** Each DataTransferItem object is associated with a [[DataTransfer]] object. */
@js.native
@JSGlobal
class DataTransferItem private[this] extends js.Object {

  /** Returns the drag data item kind, one of: "string", "file". */
  def kind: DragDataItemKind = js.native

  /** Returns the drag data item type string. */
  def `type`: String = js.native

  /** Invokes the callback with the string data as the argument, if the drag data item kind is text. */
  def getAsString(callback: js.Function1[String, Unit]): Unit = js.native

  /** Returns a File object, if the drag data item kind is File. */
  def getAsFile(): File = js.native
}
