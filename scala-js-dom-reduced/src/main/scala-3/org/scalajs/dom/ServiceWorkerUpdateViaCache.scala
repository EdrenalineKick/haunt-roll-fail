package org.scalajs.dom

import scala.scalajs.js

opaque type ServiceWorkerUpdateViaCache <: String = String

object ServiceWorkerUpdateViaCache {
  /** The service worker script and all of its imports will be updated. */
  val all: ServiceWorkerUpdateViaCache = "all"

  /** Only imports referenced by the service worker script will be updated. This is the default. */
  val imports: ServiceWorkerUpdateViaCache = "imports"

  /** Neither the service worker, nor its imports will be updated. */
  val none: ServiceWorkerUpdateViaCache = "none"
}
