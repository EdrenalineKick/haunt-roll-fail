package org.scalajs.dom

import scala.scalajs.js

trait NotificationOptions extends js.Object {

  /** The body property of the Notification interface indicates the body string of the notification. */
  var body: js.UndefOr[String] = js.undefined

  /** The dir property of the Notification interface indicates the text direction of the notification. */
  var dir: js.UndefOr[String] = js.undefined

  /** The icon property of the Notification interface contains the URL of an icon to be displayed as part of the
    * notification.
    */
  var icon: js.UndefOr[String] = js.undefined

  /** The lang property of the Notification interface indicates the text direction of the notification. */
  var lang: js.UndefOr[String] = js.undefined

  /** The noscreen property of the Notification interface specifies whether the notification firing should enable the
    * device's screen or not.
    */
  var noscreen: js.UndefOr[Boolean] = js.undefined

  /** The renotify property of the Notification interface specifies whether the user should be notified after a new
    * notification replaces an old one.
    */
  var renotify: js.UndefOr[Boolean] = js.undefined

  /** The silent property of the Notification interface specifies whether the notification should be silent, i.e. no
    * sounds or vibrations should be issued, regardless of the device settings.
    */
  var silent: js.UndefOr[Boolean] = js.undefined

  /** The sound property of the Notification interface specifies the URL of an audio file to be played when the
    * notification fires.
    */
  var sound: js.UndefOr[String] = js.undefined

  /** The sticky property of the Notification interface specifies whether the notification should be 'sticky', i.e. not
    * easily clearable by the user.
    */
  var sticky: js.UndefOr[Boolean] = js.undefined

  /** The tag property of the Notification interface signifies an identifying tag for the notification.
    *
    * The idea of notification tags is that more than one notification can share the same tag, linking them together.
    * One notification can then be programmatically replaced with another to avoid the users' screen being filled up
    * with a huge number of similar notifications.
    */
  var tag: js.UndefOr[String] = js.undefined

  /** The onclick property of the Notification interface specifies an event listener to receive click events. These
    * events occur when the user clicks on a displayed Notification.
    */
  var onclick: js.UndefOr[js.Function1[Event, Any]] = js.undefined

  /** The onerror property of the Notification interface specifies an event listener to receive error events. These
    * events occur when something goes wrong with a Notification (in many cases an error preventing the notification
    * from being displayed.)
    */
  var onerror: js.UndefOr[js.Function1[ErrorEvent, Any]] = js.undefined

  var vibrate: js.UndefOr[js.Array[Double]] = js.undefined
}

@deprecated("all members of NotificationOptions are deprecated", "2.0.0")
object NotificationOptions {

  /** Construct a new NotificationOptions
    *
    * @param body
    *   The body text of the notification.
    * @param dir
    *   The text direction of the notification.
    * @param icon
    *   The icon URL of the notification.
    * @param lang
    *   The text direction of the notification.
    * @param noscreen
    *   Boolean indicating if notification firing should enable the device's screen or not
    * @param renotify
    *   Boolean indicating whether the user should be notified after a new notification replaces an old one.
    * @param silent
    *   Boolean indicating specifies whether the notification should be silent, i.e. no sounds or vibrations should be
    *   issued, regardless of the device settings
    * @param sound
    *   The URL of an audio file to be played when the notification fires.
    * @param sticky
    *   Boolean indicating whether the notification should be 'sticky', i.e. not easily clearable by the user.
    * @param tag
    *   A text identifying tag for the notification.
    * @param vibrate
    *   The vibration pattern for hardware to emit
    * @return
    *   a new NotificationOptions
    */
  @deprecated("use `new NotificationOptions { ... }` instead", "2.0.0")
  @inline
  def apply(
      body: js.UndefOr[String] = js.undefined, dir: js.UndefOr[String] = js.undefined,
      icon: js.UndefOr[String] = js.undefined, lang: js.UndefOr[String] = js.undefined,
      noscreen: js.UndefOr[Boolean] = js.undefined, renotify: js.UndefOr[Boolean] = js.undefined,
      silent: js.UndefOr[Boolean] = js.undefined, sound: js.UndefOr[String] = js.undefined,
      sticky: js.UndefOr[Boolean] = js.undefined, tag: js.UndefOr[String] = js.undefined,
      onclick: js.UndefOr[js.Function1[Event, Any]] = js.undefined,
      onerror: js.UndefOr[js.Function1[ErrorEvent, Any]] = js.undefined,
      vibrate: js.UndefOr[js.Array[Double]] = js.undefined
  ): NotificationOptions = {
    val body0 = body
    val dir0 = dir
    val icon0 = icon
    val lang0 = lang
    val noscreen0 = noscreen
    val renotify0 = renotify
    val silent0 = silent
    val sound0 = sound
    val sticky0 = sticky
    val tag0 = tag
    val onclick0 = onclick
    val onerror0 = onerror
    val vibrate0 = vibrate
    new NotificationOptions {
      this.body = body0
      this.dir = dir0
      this.icon = icon0
      this.lang = lang0
      this.noscreen = noscreen0
      this.renotify = renotify0
      this.silent = silent0
      this.sound = sound0
      this.sticky = sticky0
      this.tag = tag0
      this.onclick = onclick0
      this.onerror = onerror0
      this.vibrate = vibrate0
    }
  }
}
