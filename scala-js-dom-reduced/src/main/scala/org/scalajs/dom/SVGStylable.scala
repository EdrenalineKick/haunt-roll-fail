/** All documentation for facades is thanks to Mozilla Contributors at https://developer.mozilla.org/en-US/docs/Web/API
  * and available under the Creative Commons Attribution-ShareAlike v2.5 or later.
  * http://creativecommons.org/licenses/by-sa/2.5/
  *
  * Everything else is under the MIT License http://opensource.org/licenses/MIT
  */
package org.scalajs.dom

import scala.scalajs.js

/** The SVGStylable interface is implemented on all objects corresponding to SVG elements that can have style, class and
  * presentation attributes specified on them.
  */
@js.native
trait SVGStylable extends js.Object {

  /** Corresponds to attribute class on the given element. */
  var className: SVGAnimatedString = js.native

  /** Corresponds to attribute style on the given element. */
  var style: CSSStyleDeclaration = js.native
}
