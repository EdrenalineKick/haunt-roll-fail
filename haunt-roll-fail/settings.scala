package hrf
//
//
//
//
import hrf.colmat._
import hrf.logger._
//
//
//
//

import hrf.options._
import hrf.elem._


trait Setting extends BaseOption

sealed trait TipsSetting extends Setting with OneOfGroup {
    val group = "Tips"
}

case object ShowTips extends TipsSetting {
    val valueOn = "Show".hlb
}
case object HideTips extends TipsSetting {
    val valueOn = "Hide".hlb
}


sealed trait FontFaceSetting extends Setting with OneOfGroup {
    val group = "Font Face"
}

case object GameFontFace extends FontFaceSetting {
    val valueOn = "Game".hlb
}
case object CodeFontFace extends FontFaceSetting {
    val valueOn = "Code".hlb
}
case object SystemFontFace extends FontFaceSetting {
    val valueOn = "System".hlb
}


sealed trait FontSizeSetting extends Setting with OneOfGroup {
    val group = "Font Size"
}

case object SmallerFontSize extends FontSizeSetting {
    val valueOn = "Smaller".hlb
}
case object SmallFontSize extends FontSizeSetting {
    val valueOn = "Small".hlb
}
case object NormalFontSize extends FontSizeSetting {
    val valueOn = "Normal".hlb
}
case object LargeFontSize extends FontSizeSetting {
    val valueOn = "Large".hlb
}
case object LargerFontSize extends FontSizeSetting {
    val valueOn = "Larger".hlb
}


sealed trait FullScreenSetting extends Setting with OneOfGroup {
    val group = "Full Screen"
}

case object AlwaysFullScreen extends FullScreenSetting {
    val valueOn = "Always".hlb
}
case object TripleClickFullScreen extends FullScreenSetting {
    val valueOn = "Triple Click".hlb
}
case object NeverFullScreen extends FullScreenSetting {
    val valueOn = "Never".hlb
}


sealed trait ButtonSpacingSetting extends Setting with OneOfGroup {
    val group = "Buttons"
}

case object CondensedButtonSpacing extends ButtonSpacingSetting {
    val valueOn = "Condensed".hlb
}
case object NormalSpacing extends ButtonSpacingSetting {
    val valueOn = "Normal".hlb
}
case object ExpandedButtonSpacing extends ButtonSpacingSetting {
    val valueOn = "Expanded".hlb
}


sealed trait ScrollSpeedSetting extends Setting with OneOfGroup {
    val group = "Scroll Speed"
}

case object SlowestScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Slowest".hlb
}
case object SlowerScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Slower".hlb
}
case object SlowScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Slow".hlb
}
case object NormalScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Normal".hlb
}
case object FastScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Fast".hlb
}
case object FasterScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Faster".hlb
}
case object FastestScrollSpeed extends ScrollSpeedSetting {
    val valueOn = "Fastest".hlb
}
