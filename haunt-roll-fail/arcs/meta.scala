package arcs
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

import hrf.meta._
import hrf.options._
import hrf.elem._

case class UnknownOption(o : String) extends GameOption {
    val group = "Unknown"
    val valueOn = "Unknown Option " ~ o
}

trait OldIncorrectBehaviour extends GameOption with ToggleOption {
    val group = "Old Incorrect Behaviour"
}

trait SetupOption extends GameOption with ToggleOption {
    val group = "Setup".hh
}

case object RandomPlayerOrder extends SetupOption {
    val valueOn = "Random Player Order".hh
}

case object RandomizePlanetResources extends SetupOption {
    val valueOn = "Randomize Planet Resources".hh
}

case object RandomizeStartingSystems extends SetupOption {
    val valueOn = "Randomize Starting Systems".hh
    override def blocked(all : $[BaseOption]) = all.of[CampaignOption]./($(_))
    override val explain = $(
    )
}


trait LeadersAndLoreOption extends GameOption with ToggleOption with ImportantOption {
    val group = "Leaders and Lore".hh
    override def blocked(all : $[BaseOption]) : $[$[BaseOption]]  = all.of[CampaignOption]./($(_))
}

case object LeadersAndLorePreset1 extends LeadersAndLoreOption {
    val valueOn = "Preset".hh ~ " " ~ "#1".hlb ~ " (Movement/Influence)"
    override val explain = $(
        "Preset of " ~ "five".hh ~ " leader cards and " ~ "five".hh ~ " lore cards.",
        $(Elder, Mystic, Feastbringer, Noble, Demagogue)./(_.elem).join(", "),
        $(SprinterDrives, PredictiveSensors, ForceBeams, CatapultOverdriveLL, SurvivalOverrides)./(_.elem).join(", "),
        "Developed by " ~ "whichwit".hlb ~ " and " ~ "JT".hlb
    )
}

case object LeadersAndLorePreset2 extends LeadersAndLoreOption {
    val valueOn = "Preset".hh ~ " " ~ "#2".hlb ~ " (Buildings/Planets)"
    override val explain = $(
        "Preset of " ~ "five".hh ~ " leader cards and " ~ "five".hh ~ " lore cards.",
        $(Agitator, Feastbringer, Warrior, Noble, Upstart)./(_.elem).join(", "),
        $(LivingStructures, CloudCities, GateStations, GatePorts, ToolPriests)./(_.elem).join(", "),
        "Developed by " ~ "whichwit".hlb ~ " and " ~ "JT".hlb
    )
}

case object LeadersAndLorePreset3 extends LeadersAndLoreOption {
    val valueOn = "Preset".hh ~ " " ~ "#3".hlb ~ " (Offense/Defense)"
    override val explain = $(
        "Preset of " ~ "five".hh ~ " leader cards and " ~ "five".hh ~ " lore cards.",
        $(Overseer, Corsair, Anarchist, Shaper, Quartermaster)./(_.elem).join(", "),
        $(SignalBreaker, RailgunArrays, SeekerTorpedoes, MirrorPlating, GalacticRifles)./(_.elem).join(", "),
        "Developed by " ~ "whichwit".hlb ~ " and " ~ "JT".hlb
    )
}

case object LeadersAndLorePreset4 extends LeadersAndLoreOption {
    val valueOn = "Archivist".hh ~ " & " ~ "Lores".hh

    override def required(all : $[BaseOption]) = $($(LeadersAndLorePreset1), $(LeadersAndLorePreset2), $(LeadersAndLorePreset3))

    override val explain = $(
        Archivist.elem ~ " leader card.",
        $(GuildLoyaltyLL, AncientHoldings, HiddenHarbors, RaiderExosuits, RepairDrones)./(_.elem).join(", "),
        "Developed by " ~ "whichwit".hlb ~ " and " ~ "JT".hlb
    )
}

case object DoubleLore extends LeadersAndLoreOption {
    val valueOn = "Double Lore".hh

    override def blocked(all : $[BaseOption]) = super.blocked(all) ++ $($(TripleLore))
    override def required(all : $[BaseOption]) = $($(LeadersAndLorePreset1, LeadersAndLorePreset2), $(LeadersAndLorePreset2, LeadersAndLorePreset3), $(LeadersAndLorePreset1, LeadersAndLorePreset3))
}

case object TripleLore extends LeadersAndLoreOption {
    val valueOn = "Triple Lore".hh

    override def blocked(all : $[BaseOption]) = super.blocked(all) ++ $($(DoubleLore))
    override def required(all : $[BaseOption]) = $($(LeadersAndLorePreset1, LeadersAndLorePreset2, LeadersAndLorePreset3))
}


trait CampaignOption extends GameOption with ImportantOption {
    val group = "Campaign Mode".hh
    override def blocked(all : $[BaseOption]) = all.of[LeadersAndLoreOption]./($(_))
}

case object NoFate extends CampaignOption with ToggleOption {
    val valueOn = "No Fate".hlb
}


case object SplitDiscardPile extends GameOption with ToggleOption {
    val group = "Other Options".hh
    val valueOn = "Split Discard Piles".hh
}


trait StarportsOption extends hrf.Setting with OneOfGroup {
    val group = "Starports Shape"
}

case object StarStarports extends StarportsOption {
    val valueOn = "Star".hlb
}

case object TriangleStarports extends StarportsOption {
    val valueOn = "Triangle".hlb
}


trait ShipsSizeOption extends hrf.Setting with OneOfGroup {
    val group = "Ships Size"
}

case object StandardShipsSize extends ShipsSizeOption {
    val valueOn = "Standard".hlb
}

case object SmallShipsSize extends ShipsSizeOption {
    val valueOn = "Small".hlb
}

case object SmallerShipsSize extends ShipsSizeOption {
    val valueOn = "Smaller".hlb
}

case object SmallestShipsSize extends ShipsSizeOption {
    val valueOn = "Smallest".hlb
}


trait FactionPanesOption extends hrf.Setting with OneOfGroup {
    val group = "Player Panes"
}

case object AutoFactionPanes extends FactionPanesOption {
    val valueOn = "Auto".hlb
}

case object VerticalFactionPanes extends FactionPanesOption {
    val valueOn = "Vertical".hlb
}

case object HorizontalFactionPanes extends FactionPanesOption {
    val valueOn = "Horizontal".hlb
}


trait EndOfTurnOption extends hrf.Setting with OneOfGroup {
    val group = "End of Turn"
}

case object AutoEndOfTurn extends EndOfTurnOption {
    val valueOn = "Auto".hlb
}

case object ConfirmEndOfTurn extends EndOfTurnOption {
    val valueOn = "Confirm".hlb
}


object Meta extends MetaGame { mmm =>
    val gaming = arcs.gaming

    type F = Faction

    def tagF = implicitly

    val name = "arcs"
    val label = "Arcs"

    val factions = $(Red, Yellow, White, Blue)

    val minPlayers = 3

    override val hiddenOptions = $

    val options =
        $(LeadersAndLorePreset1, LeadersAndLorePreset2, LeadersAndLorePreset3, LeadersAndLorePreset4, DoubleLore, TripleLore) ++
        $(RandomPlayerOrder, RandomizePlanetResources, RandomizeStartingSystems) ++
        $(SplitDiscardPile) ++
        hiddenOptions

    override val gradualFactions : Boolean = true

    val quickMin = 3
    val quickMax = 4

    override val quickOptions = Map(
        LeadersAndLorePreset1 -> 0.23,
        LeadersAndLorePreset2 -> 0.23,
        LeadersAndLorePreset3 -> 0.23,
        LeadersAndLorePreset4 -> 0.50,
        DoubleLore -> 0.10,
        RandomPlayerOrder -> 1.0,
    )

    def randomGameName() = {
        val n = $("Space", "Politics", "Betrayal", "Explosion", "Conquest", "Warp", "Renegade", "Sway", "Diplomacy", "Conflict", "Expanse", "Treachery", "Catastrophe", "Zero Gravity", "Wormhole", "Negotiation", "Control", "Force", "Galaxy", "Singularity").shuffle
        val c = $("for", "against", "versus", "through", "and", "of", "in", "as", "by").shuffle
        n.head + " " + c.head + " " + n.last
    }

    def validateFactionCombination(factions : $[Faction]) = None ||
        (factions.num < 3).?(ErrorResult("Minimum three factions")) ||
        (factions.num < 2).?(ErrorResult("Minimum two factions")) ||
        (factions.num > 4).?(ErrorResult("Max four factions")) |
        InfoResult("Arcs")

    def validateFactionSeatingOptions(factions : $[Faction], options : $[O]) = validateFactionCombination(factions)

    def factionName(f : Faction) = f.name
    def factionElem(f : Faction) = f.name.styled(f)

    override def glyph(g : G) : |[String] = g.current./(_.style + "-glyph")
    override def glyph(f : F) : |[String] = |(f.style + "-glyph")
    override def glyph(g : G, f : F) : |[String] = glyph(f).%!(_ => g.highlightFaction.has(f) && hrf.HRF.uptime() / 1000 % 2 == 1)

    def createGame(factions : $[Faction], options : $[O]) = new Game(factions, options)

    def getBots(f : Faction) = $("Easy")

    def getBot(f : Faction, b : String) = (f, b) match {
        case (f, "Easy") => new BotNew(f)
        case (f, _) => new BotOld(f)
    }

    def defaultBots : $[String] = $("Easy")

    def writeFaction(f : Faction) = f.short
    def parseFaction(s : String) : |[Faction] = factions.%(_.short == s).single

    def writeOption(o : O) = Serialize.write(o)
    def parseOption(s : String) = $(options.find(o => writeOption(o) == s) || options.find(o => o.toString == s) | (UnknownOption(s)))

    def parseAction(s : String) : Action = Serialize.parseAction(s)
    def writeAction(a : Action) : String = Serialize.write(a)

    val start = StartAction(gaming.version)

    override def bodyFont = Some("neue-kabel")
    override def titleFont = Some("fm-bolyar-pro-900")

    override def settingsList = super.settingsList ++
        $(StarStarports, TriangleStarports) ++
        $(AutoEndOfTurn, ConfirmEndOfTurn) ++
        $(StandardShipsSize, SmallShipsSize, SmallerShipsSize, SmallestShipsSize) ++
        $(AutoFactionPanes, HorizontalFactionPanes, VerticalFactionPanes)

    override def settingsDefaults = super.settingsDefaults ++ $(StarStarports, AutoEndOfTurn, StandardShipsSize, AutoFactionPanes)

    override def tips = super.tips ++ $(
        "You can change Starport token shape in the settings.",
        "A ship is in the system where the center of its base is positioned.",
        "You can enable End-of-Turn confirmation in the settings.",
    )

    val assets =
    ConditionalAssetsList((factions : $[F], options : $[O]) => true)(
        ImageAsset("map-no-slots") ::
        ImageAsset("map-regions").makeLossless ::
        ImageAsset("map-regions-select").makeLossless ::

        ImageAsset("map-out-1") ::
        ImageAsset("map-out-2") ::
        ImageAsset("map-out-3", {
            if (random() < 0.121)
                "map-out-3-betrayal"
            else
            if (random() < 0.012)
                "map-out-3-catan"
            else
                "map-out-3"
        }) ::
        ImageAsset("map-out-4") ::
        ImageAsset("map-out-5") ::
        ImageAsset("map-out-6") ::

        ImageAsset("map-ambitions-3") ::

        ImageAsset("ambitions").scaled(50) ::
        ImageAsset("resources").scaled(50) ::
        ImageAsset("edicts").scaled(50) ::
        ImageAsset("laws").scaled(50) ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "icon")(
        ImageAsset("material").scaled(40) ::
        ImageAsset("fuel").scaled(40) ::
        ImageAsset("weapon").scaled(40) ::
        ImageAsset("relic").scaled(40) ::
        ImageAsset("psionic").scaled(40) ::

        ImageAsset("nothingness").scaled(40) ::
        ImageAsset("discard-resource").scaled(40) ::

        ImageAsset("material-outrage").scaled(40) ::
        ImageAsset("fuel-outrage").scaled(40) ::
        ImageAsset("weapon-outrage").scaled(40) ::
        ImageAsset("relic-outrage").scaled(40) ::
        ImageAsset("psionic-outrage").scaled(40) ::

        ImageAsset("keys-1").scaled(12.5) ::
        ImageAsset("keys-2").scaled(12.5) ::
        ImageAsset("keys-3").scaled(12.5) ::
        ImageAsset("keys-4").scaled(12.5) ::
        ImageAsset("half-keys-1").scaled(12.5) ::
        ImageAsset("half-keys-2").scaled(12.5) ::
        ImageAsset("half-keys-3").scaled(12.5) ::
        ImageAsset("half-keys-4").scaled(12.5) ::
        ImageAsset("card-back-small") ::
        ImageAsset("card-back-5") ::
        ImageAsset("b-glyph") ::
        ImageAsset("r-glyph") ::
        ImageAsset("w-glyph") ::
        ImageAsset("y-glyph") ::

        ImageAsset("raid-key") ::

        ImageAsset("agent-background") ::

        ImageAsset("assault-die-1") ::
        ImageAsset("assault-die-2") ::
        ImageAsset("assault-die-3") ::
        ImageAsset("assault-die-4") ::
        ImageAsset("assault-die-5") ::
        ImageAsset("assault-die-6") ::
        ImageAsset("assault-die") ::
        ImageAsset("raid-die-1") ::
        ImageAsset("raid-die-2") ::
        ImageAsset("raid-die-3") ::
        ImageAsset("raid-die-4") ::
        ImageAsset("raid-die-5") ::
        ImageAsset("raid-die-6") ::
        ImageAsset("raid-die") ::
        ImageAsset("skirmish-die-1") ::
        ImageAsset("skirmish-die-2") ::
        ImageAsset("skirmish-die-3") ::
        ImageAsset("skirmish-die-4") ::
        ImageAsset("skirmish-die-5") ::
        ImageAsset("skirmish-die-6") ::
        ImageAsset("skirmish-die") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "action")(
        ImageAsset("card-back") ::

        ImageAsset("event") ::
        ImageAsset("event-number") ::

        ImageAsset("administration-1") ::
        ImageAsset("administration-2") ::
        ImageAsset("administration-3") ::
        ImageAsset("administration-4") ::
        ImageAsset("administration-5") ::
        ImageAsset("administration-6") ::
        ImageAsset("administration-7") ::
        ImageAsset("aggression-1") ::
        ImageAsset("aggression-2") ::
        ImageAsset("aggression-3") ::
        ImageAsset("aggression-4") ::
        ImageAsset("aggression-5") ::
        ImageAsset("aggression-6") ::
        ImageAsset("aggression-7") ::
        ImageAsset("construction-1") ::
        ImageAsset("construction-2") ::
        ImageAsset("construction-3") ::
        ImageAsset("construction-4") ::
        ImageAsset("construction-5") ::
        ImageAsset("construction-6") ::
        ImageAsset("construction-7") ::
        ImageAsset("mobilization-1") ::
        ImageAsset("mobilization-2") ::
        ImageAsset("mobilization-3") ::
        ImageAsset("mobilization-4") ::
        ImageAsset("mobilization-5") ::
        ImageAsset("mobilization-6") ::
        ImageAsset("mobilization-7") ::

        ImageAsset("administration-number-1") ::
        ImageAsset("administration-number-2") ::
        ImageAsset("administration-number-3") ::
        ImageAsset("administration-number-4") ::
        ImageAsset("administration-number-5") ::
        ImageAsset("administration-number-6") ::
        ImageAsset("administration-number-7") ::
        ImageAsset("administration-pips-1") ::
        ImageAsset("administration-pips-2") ::
        ImageAsset("administration-pips-3") ::
        ImageAsset("administration-pips-4") ::
        ImageAsset("administration-pips-copy") ::
        ImageAsset("administration-pips-pivot") ::
        ImageAsset("administration-plaque") ::

        ImageAsset("aggression-number-1") ::
        ImageAsset("aggression-number-2") ::
        ImageAsset("aggression-number-3") ::
        ImageAsset("aggression-number-4") ::
        ImageAsset("aggression-number-5") ::
        ImageAsset("aggression-number-6") ::
        ImageAsset("aggression-number-7") ::
        ImageAsset("aggression-pips-1") ::
        ImageAsset("aggression-pips-2") ::
        ImageAsset("aggression-pips-3") ::
        ImageAsset("aggression-pips-copy") ::
        ImageAsset("aggression-pips-pivot") ::
        ImageAsset("aggression-plaque") ::

        ImageAsset("construction-number-1") ::
        ImageAsset("construction-number-2") ::
        ImageAsset("construction-number-3") ::
        ImageAsset("construction-number-4") ::
        ImageAsset("construction-number-5") ::
        ImageAsset("construction-number-6") ::
        ImageAsset("construction-number-7") ::
        ImageAsset("construction-pips-1") ::
        ImageAsset("construction-pips-2") ::
        ImageAsset("construction-pips-3") ::
        ImageAsset("construction-pips-4") ::
        ImageAsset("construction-pips-copy") ::
        ImageAsset("construction-pips-pivot") ::
        ImageAsset("construction-plaque") ::

        ImageAsset("mobilization-number-1") ::
        ImageAsset("mobilization-number-2") ::
        ImageAsset("mobilization-number-3") ::
        ImageAsset("mobilization-number-4") ::
        ImageAsset("mobilization-number-5") ::
        ImageAsset("mobilization-number-6") ::
        ImageAsset("mobilization-number-7") ::
        ImageAsset("mobilization-pips-1") ::
        ImageAsset("mobilization-pips-2") ::
        ImageAsset("mobilization-pips-3") ::
        ImageAsset("mobilization-pips-4") ::
        ImageAsset("mobilization-pips-copy") ::
        ImageAsset("mobilization-pips-pivot") ::
        ImageAsset("mobilization-plaque") ::

        ImageAsset("zeroed") ::
        ImageAsset("hidden") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "court")(
        ImageAsset("bc01") ::
        ImageAsset("bc02") ::
        ImageAsset("bc03") ::
        ImageAsset("bc04") ::
        ImageAsset("bc05") ::
        ImageAsset("bc06") ::
        ImageAsset("bc07") ::
        ImageAsset("bc08") ::
        ImageAsset("bc09") ::
        ImageAsset("bc10") ::
        ImageAsset("bc11") ::
        ImageAsset("bc12") ::
        ImageAsset("bc13") ::
        ImageAsset("bc14") ::
        ImageAsset("bc15") ::
        ImageAsset("bc16") ::
        ImageAsset("bc17") ::
        ImageAsset("bc18") ::
        ImageAsset("bc19") ::
        ImageAsset("bc20") ::
        ImageAsset("bc21") ::
        ImageAsset("bc22") ::
        ImageAsset("bc23") ::
        ImageAsset("bc24") ::
        ImageAsset("bc25") ::
        ImageAsset("bc26") ::
        ImageAsset("bc27") ::
        ImageAsset("bc28") ::
        ImageAsset("bc29") ::
        ImageAsset("bc30") ::
        ImageAsset("bc31") ::
        ImageAsset("cc01") ::
        ImageAsset("cc02") ::
        ImageAsset("cc03") ::
        ImageAsset("cc04") ::
        ImageAsset("cc05") ::
        ImageAsset("cc06") ::
        ImageAsset("cc07") ::
        ImageAsset("cc08") ::
        ImageAsset("cc09") ::
        ImageAsset("cc10") ::
        ImageAsset("cc11") ::
        ImageAsset("cc12") ::
        ImageAsset("cc13") ::
        ImageAsset("cc14") ::
        ImageAsset("cc15") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "setup")(
        ImageAsset("setup-2p-01") ::
        ImageAsset("setup-2p-02") ::
        ImageAsset("setup-2p-03") ::
        ImageAsset("setup-2p-04") ::
        ImageAsset("setup-3p-01") ::
        ImageAsset("setup-3p-02") ::
        ImageAsset("setup-3p-03") ::
        ImageAsset("setup-3p-04") ::
        ImageAsset("setup-4p-01") ::
        ImageAsset("setup-4p-02") ::
        ImageAsset("setup-4p-03") ::
        ImageAsset("setup-4p-04") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "leader")(
        ImageAsset("leader01") ::
        ImageAsset("leader02") ::
        ImageAsset("leader03") ::
        ImageAsset("leader04") ::
        ImageAsset("leader05") ::
        ImageAsset("leader06") ::
        ImageAsset("leader07") ::
        ImageAsset("leader08") ::
        ImageAsset("leader09") ::
        ImageAsset("leader10") ::
        ImageAsset("leader11") ::
        ImageAsset("leader12") ::
        ImageAsset("leader13") ::
        ImageAsset("leader14") ::
        ImageAsset("leader15") ::
        ImageAsset("leader16") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "lore")(
        ImageAsset("lore01") ::
        ImageAsset("lore02") ::
        ImageAsset("lore03") ::
        ImageAsset("lore04") ::
        ImageAsset("lore05") ::
        ImageAsset("lore06") ::
        ImageAsset("lore07") ::
        ImageAsset("lore08") ::
        ImageAsset("lore09") ::
        ImageAsset("lore10") ::
        ImageAsset("lore11") ::
        ImageAsset("lore12") ::
        ImageAsset("lore13") ::
        ImageAsset("lore14") ::
        ImageAsset("lore15") ::
        ImageAsset("lore16") ::
        ImageAsset("lore17") ::
        ImageAsset("lore18") ::
        ImageAsset("lore19") ::
        ImageAsset("lore20") ::
        ImageAsset("lore21") ::
        ImageAsset("lore22") ::
        ImageAsset("lore23") ::
        ImageAsset("lore24") ::
        ImageAsset("lore25") ::
        ImageAsset("lore26") ::
        ImageAsset("lore27") ::
        ImageAsset("lore28") ::
        ImageAsset("lore29") ::
        ImageAsset("lore30") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "empire")(
        ImageAsset("aid01a") ::
        ImageAsset("aid01b") ::
        ImageAsset("aid02") ::
        ImageAsset("aid03") ::
        ImageAsset("aid04") ::
        ImageAsset("aid05") ::
        ImageAsset("aid06a") ::
        ImageAsset("aid06b") ::
        ImageAsset("aid07a") ::
        ImageAsset("aid07b") ::
        ImageAsset("aid08a") ::
        ImageAsset("aid08b") ::
        ImageAsset("aid09a") ::
        ImageAsset("aid09b") ::
        ImageAsset("aid10a") ::
        ImageAsset("aid10b") ::
        ImageAsset("aid11a") ::
        ImageAsset("aid11b") ::
        ImageAsset("aid12a") ::
        ImageAsset("aid12b") ::
        ImageAsset("aid13a") ::
        ImageAsset("aid13b") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "fate")(
        ImageAsset("no-fate") ::
        ImageAsset("fate01") ::
        ImageAsset("fate02") ::
        ImageAsset("fate03") ::
        ImageAsset("fate04") ::
        ImageAsset("fate05") ::
        ImageAsset("fate06") ::
        ImageAsset("fate07") ::
        ImageAsset("fate08") ::
        ImageAsset("fate09") ::
        ImageAsset("fate10") ::
        ImageAsset("fate11") ::
        ImageAsset("fate12") ::
        ImageAsset("fate13") ::
        ImageAsset("fate14") ::
        ImageAsset("fate15") ::
        ImageAsset("fate16") ::
        ImageAsset("fate17") ::
        ImageAsset("fate18") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f01")(
        ImageAsset("f01-01a") ::
        ImageAsset("f01-01b") ::
        ImageAsset("f01-02") ::
        ImageAsset("f01-03") ::
        ImageAsset("f01-04") ::
        ImageAsset("f01-05") ::
        ImageAsset("f01-06") ::
        ImageAsset("f01-07") ::
        ImageAsset("f01-08") ::
        ImageAsset("f01-09") ::
        ImageAsset("f01-10") ::
        ImageAsset("f01-11") ::
        ImageAsset("f01-12a") ::
        ImageAsset("f01-12b") ::
        ImageAsset("f01-13") ::
        ImageAsset("f01-14") ::
        ImageAsset("f01-15") ::
        ImageAsset("f01-16") ::
        ImageAsset("f01-17") ::
        ImageAsset("f01-18") ::
        ImageAsset("f01-19") ::
        ImageAsset("f01-20") ::
        ImageAsset("f01-21") ::
        ImageAsset("f01-22a") ::
        ImageAsset("f01-22b") ::
        ImageAsset("f01-23") ::
        ImageAsset("f01-24") ::
        ImageAsset("f01-25") ::
        ImageAsset("f01-26") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f02")(
        ImageAsset("f02-01a") ::
        ImageAsset("f02-01b") ::
        ImageAsset("f02-02") ::
        ImageAsset("f02-03") ::
        ImageAsset("f02-04") ::
        ImageAsset("f02-05") ::
        ImageAsset("f02-06") ::
        ImageAsset("f02-07") ::
        ImageAsset("f02-08") ::
        ImageAsset("f02-09") ::
        ImageAsset("f02-10") ::
        ImageAsset("f02-11") ::
        ImageAsset("f02-12a") ::
        ImageAsset("f02-12b") ::
        ImageAsset("f02-13") ::
        ImageAsset("f02-14") ::
        ImageAsset("f02-15") ::
        ImageAsset("f02-16") ::
        ImageAsset("f02-17") ::
        ImageAsset("f02-18") ::
        ImageAsset("f02-19") ::
        ImageAsset("f02-20") ::
        ImageAsset("f02-21") ::
        ImageAsset("f02-22") ::
        ImageAsset("f02-23") ::
        ImageAsset("f02-24") ::
        ImageAsset("f02-25a") ::
        ImageAsset("f02-25b") ::
        ImageAsset("f02-26") ::
        ImageAsset("f02-27") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f03")(
        ImageAsset("f03-01a") ::
        ImageAsset("f03-01b") ::
        ImageAsset("f03-02") ::
        ImageAsset("f03-03") ::
        ImageAsset("f03-04") ::
        ImageAsset("f03-05") ::
        ImageAsset("f03-06") ::
        ImageAsset("f03-07") ::
        ImageAsset("f03-08") ::
        ImageAsset("f03-09") ::
        ImageAsset("f03-10") ::
        ImageAsset("f03-11") ::
        ImageAsset("f03-12a") ::
        ImageAsset("f03-12b") ::
        ImageAsset("f03-13") ::
        ImageAsset("f03-14") ::
        ImageAsset("f03-15") ::
        ImageAsset("f03-16") ::
        ImageAsset("f03-17") ::
        ImageAsset("f03-18") ::
        ImageAsset("f03-19") ::
        ImageAsset("f03-20") ::
        ImageAsset("f03-21") ::
        ImageAsset("f03-22") ::
        ImageAsset("f03-23") ::
        ImageAsset("f03-24") ::
        ImageAsset("f03-25") ::
        ImageAsset("f03-25a") ::
        ImageAsset("f03-25b") ::
        ImageAsset("f03-26a") ::
        ImageAsset("f03-26b") ::
        ImageAsset("f03-27") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f04")(
        ImageAsset("f04-01a") ::
        ImageAsset("f04-01b") ::
        ImageAsset("f04-02") ::
        ImageAsset("f04-03") ::
        ImageAsset("f04-04") ::
        ImageAsset("f04-05") ::
        ImageAsset("f04-06") ::
        ImageAsset("f04-07") ::
        ImageAsset("f04-08") ::
        ImageAsset("f04-09") ::
        ImageAsset("f04-10") ::
        ImageAsset("f04-11") ::
        ImageAsset("f04-12a") ::
        ImageAsset("f04-12b") ::
        ImageAsset("f04-13") ::
        ImageAsset("f04-14") ::
        ImageAsset("f04-15") ::
        ImageAsset("f04-16") ::
        ImageAsset("f04-17") ::
        ImageAsset("f04-18") ::
        ImageAsset("f04-19") ::
        ImageAsset("f04-20") ::
        ImageAsset("f04-21") ::
        ImageAsset("f04-22") ::
        ImageAsset("f04-23a") ::
        ImageAsset("f04-23b") ::
        ImageAsset("f04-24") ::
        ImageAsset("f04-25") ::
        ImageAsset("f04-26") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f05")(
        ImageAsset("f05-01a") ::
        ImageAsset("f05-01b") ::
        ImageAsset("f05-02") ::
        ImageAsset("f05-03") ::
        ImageAsset("f05-04") ::
        ImageAsset("f05-05") ::
        ImageAsset("f05-06") ::
        ImageAsset("f05-07") ::
        ImageAsset("f05-08") ::
        ImageAsset("f05-09") ::
        ImageAsset("f05-10") ::
        ImageAsset("f05-11") ::
        ImageAsset("f05-12a") ::
        ImageAsset("f05-12b") ::
        ImageAsset("f05-13") ::
        ImageAsset("f05-14") ::
        ImageAsset("f05-15") ::
        ImageAsset("f05-16") ::
        ImageAsset("f05-17") ::
        ImageAsset("f05-18") ::
        ImageAsset("f05-19") ::
        ImageAsset("f05-20a") ::
        ImageAsset("f05-20b") ::
        ImageAsset("f05-21") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f06")(
        ImageAsset("f06-01a") ::
        ImageAsset("f06-01b") ::
        ImageAsset("f06-02") ::
        ImageAsset("f06-03") ::
        ImageAsset("f06-04") ::
        ImageAsset("f06-05") ::
        ImageAsset("f06-06") ::
        ImageAsset("f06-07") ::
        ImageAsset("f06-08") ::
        ImageAsset("f06-09") ::
        ImageAsset("f06-10") ::
        ImageAsset("f06-11") ::
        ImageAsset("f06-12") ::
        ImageAsset("f06-13") ::
        ImageAsset("f06-14") ::
        ImageAsset("f06-15a") ::
        ImageAsset("f06-15b") ::
        ImageAsset("f06-16") ::
        ImageAsset("f06-17") ::
        ImageAsset("f06-18") ::
        ImageAsset("f06-19") ::
        ImageAsset("f06-20") ::
        ImageAsset("f06-21") ::
        ImageAsset("f06-22") ::
        ImageAsset("f06-23") ::
        ImageAsset("f06-24a") ::
        ImageAsset("f06-24b") ::
        ImageAsset("f06-25") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f07")(
        ImageAsset("f07-01a") ::
        ImageAsset("f07-01b") ::
        ImageAsset("f07-02") ::
        ImageAsset("f07-03") ::
        ImageAsset("f07-04") ::
        ImageAsset("f07-05") ::
        ImageAsset("f07-06") ::
        ImageAsset("f07-07") ::
        ImageAsset("f07-08") ::
        ImageAsset("f07-09") ::
        ImageAsset("f07-10") ::
        ImageAsset("f07-11") ::
        ImageAsset("f07-12a") ::
        ImageAsset("f07-12b") ::
        ImageAsset("f07-13") ::
        ImageAsset("f07-14") ::
        ImageAsset("f07-15") ::
        ImageAsset("f07-16") ::
        ImageAsset("f07-17") ::
        ImageAsset("f07-18") ::
        ImageAsset("f07-19a") ::
        ImageAsset("f07-19b") ::
        ImageAsset("f07-20") ::
        ImageAsset("f07-21") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "f08")(
        ImageAsset("f08-01a") ::
        ImageAsset("f08-01b") ::
        ImageAsset("f08-02") ::
        ImageAsset("f08-03") ::
        ImageAsset("f08-04") ::
        ImageAsset("f08-05") ::
        ImageAsset("f08-06") ::
        ImageAsset("f08-07") ::
        ImageAsset("f08-08") ::
        ImageAsset("f08-09") ::
        ImageAsset("f08-10") ::
        ImageAsset("f08-11") ::
        ImageAsset("f08-12") ::
        ImageAsset("f08-13") ::
        ImageAsset("f08-14") ::
        ImageAsset("f08-15") ::
        ImageAsset("f08-16") ::
        ImageAsset("f08-17") ::
        ImageAsset("f08-18") ::
        ImageAsset("f08-19") ::
        ImageAsset("f08-20a") ::
        ImageAsset("f08-20b") ::
        ImageAsset("f08-21") ::
        ImageAsset("f08-22a") ::
        ImageAsset("f08-22b") ::
        ImageAsset("f08-23") ::
        ImageAsset("f08-24") ::
        ImageAsset("f08-25") ::
        ImageAsset("f08-26") ::
        ImageAsset("f08-27") ::
        ImageAsset("f08-28") ::
        ImageAsset("f08-29a") ::
        ImageAsset("f08-29b") ::
        ImageAsset("f08-30") ::
        ImageAsset("f08-31") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "ambition", scale = 41.4)(
        ImageAsset("ambition-values-6-3") ::
        ImageAsset("ambition-values-9-4") ::
        ImageAsset("ambition-values-3-2") ::
        ImageAsset("ambition-values-4-2") ::
        ImageAsset("ambition-values-5-3") ::
        ImageAsset("ambition-values-2-0") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "figure", scale = 11)(
        ImageAsset("ship-empty") ::
        ImageAsset("b-ship") ::
        ImageAsset("b-ship-damaged") ::
        ImageAsset("r-ship") ::
        ImageAsset("r-ship-damaged") ::
        ImageAsset("w-ship") ::
        ImageAsset("w-ship-damaged") ::
        ImageAsset("y-ship") ::
        ImageAsset("y-ship-damaged") ::
        ImageAsset("imperial-ship") ::
        ImageAsset("imperial-ship-damaged") ::
        ImageAsset("imperial-ship-empty") ::
        ImageAsset("blight") ::
        ImageAsset("blight-damaged") ::
        ImageAsset("blight-empty") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "figure", scale = 11)(
        ImageAsset("agent-empty") ::
        ImageAsset("b-agent") ::
        ImageAsset("b-agent-damaged") ::
        ImageAsset("b-agent-a") ::
        ImageAsset("b-agent-b") ::
        ImageAsset("b-agent-c") ::
        ImageAsset("r-agent") ::
        ImageAsset("r-agent-damaged") ::
        ImageAsset("r-agent-a") ::
        ImageAsset("r-agent-b") ::
        ImageAsset("r-agent-c") ::
        ImageAsset("w-agent") ::
        ImageAsset("w-agent-damaged") ::
        ImageAsset("w-agent-a") ::
        ImageAsset("w-agent-b") ::
        ImageAsset("w-agent-c") ::
        ImageAsset("y-agent") ::
        ImageAsset("y-agent-damaged") ::
        ImageAsset("y-agent-a") ::
        ImageAsset("y-agent-b") ::
        ImageAsset("y-agent-c") ::
    $) ::
    ConditionalAssetsList((factions : $[F], options : $[O]) => true, "figure", scale = 38)(
        ImageAsset("building-empty-keys-1") ::
        ImageAsset("building-empty-keys-2") ::
        ImageAsset("building-empty-keys-1-3") ::
        ImageAsset("building-empty-plus-2") ::
        ImageAsset("building-empty-plus-3") ::

        ImageAsset("city-empty",     "building-empty") ::
        ImageAsset("starport-empty", "building-empty") ::
        ImageAsset("starport-alt-empty") ::
        ImageAsset("b-city") ::
        ImageAsset("b-city-damaged") ::
        ImageAsset("b-starport") ::
        ImageAsset("b-starport-damaged") ::
        ImageAsset("b-starport-alt") ::
        ImageAsset("b-starport-alt-damaged") ::
        ImageAsset("r-city") ::
        ImageAsset("r-city-damaged") ::
        ImageAsset("r-starport") ::
        ImageAsset("r-starport-damaged") ::
        ImageAsset("r-starport-alt") ::
        ImageAsset("r-starport-alt-damaged") ::
        ImageAsset("w-city") ::
        ImageAsset("w-city-damaged") ::
        ImageAsset("w-starport") ::
        ImageAsset("w-starport-damaged") ::
        ImageAsset("w-starport-alt") ::
        ImageAsset("w-starport-alt-damaged") ::
        ImageAsset("y-city") ::
        ImageAsset("y-city-damaged") ::
        ImageAsset("y-starport") ::
        ImageAsset("y-starport-damaged") ::
        ImageAsset("y-starport-alt") ::
        ImageAsset("y-starport-alt-damaged") ::
        ImageAsset("free-city") ::
        ImageAsset("free-city-damaged") ::
        ImageAsset("free-starport") ::
        ImageAsset("free-starport-damaged") ::
        ImageAsset("free-starport-alt") ::
        ImageAsset("free-starport-alt-damaged") ::
    $) ::
    $

    override def extLinks = $(
    ) ++ super.extLinks

}
