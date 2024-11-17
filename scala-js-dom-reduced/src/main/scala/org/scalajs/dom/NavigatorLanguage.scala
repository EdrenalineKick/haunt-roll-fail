/** All documentation for facades is thanks to Mozilla Contributors at https://developer.mozilla.org/en-US/docs/Web/API
  * and available under the Creative Commons Attribution-ShareAlike v2.5 or later.
  * http://creativecommons.org/licenses/by-sa/2.5/
  *
  * Everything else is under the MIT License http://opensource.org/licenses/MIT
  */
package org.scalajs.dom

import scala.scalajs.js

/** NavigatorLanguage contains methods and properties related to the language of the navigator. */
@js.native
trait NavigatorLanguage extends js.Object {

  /** Returns a DOMString representing the preferred language of the user, usually the language of the browser UI. The
    * null value is returned when this is unknown.
    */
  def language: String = js.native

  /** Returns a Array of DOMStrings representing the the user's preferred languages. The language is described using BCP
    * 47 language tags. The null value is returned when this is unknown.
    */
  def languages: js.Array[String] = js.native
}
