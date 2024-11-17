package org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation._

/** See [[https://slightlyoff.github.io/ServiceWorker/spec/service_worker_1/#cache ¶5.4 cache]] of ServiceWorker whatwg
  * spec.
  */
@js.native
@JSGlobal
abstract class Cache extends js.Object {

  def `match`(request: RequestInfo,
      options: js.UndefOr[CacheQueryOptions] = js.native): js.Promise[js.UndefOr[Response]] = js.native

  def matchAll(request: RequestInfo = js.native,
      options: js.UndefOr[CacheQueryOptions] = js.native): js.Promise[js.Array[Response]] = js.native

  def add(request: RequestInfo): js.Promise[Unit] = js.native

  def addAll(requests: js.Array[RequestInfo]): js.Promise[Unit] = js.native

  def put(request: RequestInfo, response: Response): js.Promise[Unit] = js.native

  def delete(request: RequestInfo, options: js.UndefOr[CacheQueryOptions] = js.native): js.Promise[Boolean] = js.native

  def keys(request: js.UndefOr[RequestInfo] = js.native,
      options: js.UndefOr[CacheQueryOptions] = js.native): js.Promise[js.Array[Request]]
}
