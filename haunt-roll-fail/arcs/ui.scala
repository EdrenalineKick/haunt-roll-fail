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

import org.scalajs.dom

import hrf.canvas._

import hrf.web._
import hrf.ui._

import hrf.elem._
import hrf.html._

import arcs.elem._

import hrf.ui.again._
import hrf.ui.sprites._

import scalajs.js.timers.setTimeout


object UI extends BaseUI {
    val mmeta = Meta

    def create(uir : ElementAttachmentPoint, arity : Int, options : $[hrf.meta.GameOption], resources : Resources, title : String, callbacks : hrf.Callbacks) = new UI(uir, arity, options, resources, callbacks)
}

class UI(val uir : ElementAttachmentPoint, arity : Int, val options : $[hrf.meta.GameOption], val resources : Resources, callbacks : hrf.Callbacks) extends MapGUI {
    def factionElem(f : Faction) = f.name.styled(f)

    override def randomTip() = Meta.tips.shuffle.starting

    var tip = randomTip()

    val statuses = 1.to(arity)./(i => newPane("status-" + i, Content, styles.status, styles.fstatus, ExternalStyle("hide-scrollbar")))

    val campaign : Boolean = options.of[CampaignOption].any

    def starport : String = callbacks.settings.has(StarStarports).?("starport-alt").|("starport")

    val court : CanvasPane = new CanvasPaneX(newPane("court", Content), 2/2, Inside)(resources) {
        moveSpeedUp = 3.8

        val n = 4 + campaign.??(1)
        val d = 12

        val margins = Margins(d, d, d, d)

        object card {
            val width = 744
            val height = 1039
        }

        val width = card.width * n + d * n - d
        val bleed = campaign.??(d * 5)
        val height = card.height + campaign.??(d) + bleed

        def makeScene() = {
            val cards = new OrderedLayer

            if (game.market.any) {
                0.until(n).foreach { i =>
                    if (i < game.market.num) {
                        val c = game.market.$(i)

                        cards.add(Sprite($(ImageRect(new RawImage(img(c.id)), Rectangle(0, 0, card.width, card.height), 1.0)), $(Rectangle(0, 0, card.width, card.height)), $(c)))(card.width * i + d * i, card.height * 0 + bleed)

                        if (c == ImperialCouncilDecided)
                            game.decided.foreach { f =>
                                val scale = 12.6/2
                                cards.add(Sprite($(ImageRect(new RawImage(img(f.short + "-agent")), Rectangle(-21*scale, -21*scale, 42*scale, 68*scale), 0.6)), $))(card.width * i + d * i + card.width / 2, 400)
                            }
                    }
                    else {
                        cards.add(Sprite($(ImageRect(new RawImage(img("bc31")), Rectangle(0, 0, card.width, card.height), 0.5)), $(Rectangle(0, 0, card.width, card.height)), $()))(card.width * i + d * i, card.height * 0 + bleed)
                        cards.add(Sprite($(ImageRect(new RawImage(img("bc30")), Rectangle(0, 0, card.width, card.height), 0.5)), $(Rectangle(0, 0, card.width, card.height)), $()))(card.width * i + d * i, card.height * -1 - d + bleed)
                    }
                }

                0.until(5).foreach { i =>
                    if (i < game.market.num) {
                        val c = game.market.$(i)

                        val l = game.figures.get(Influence(c))

                        val scale = 2.6
                        val shadow = 3

                        l.foreach { u =>
                            val w = (l.num < 7).?(112).|(card.width / l.num)
                            cards.add(Sprite($(ImageRect(new RawImage(img("agent-background")), Rectangle(0, 0, (42+shadow+shadow)*scale, (68+shadow+shadow)*scale), 1.0)), $))(card.width * i + d * i + card.width / 2 - w / 2 * l.num + w * l.indexOf(u) - shadow*scale, 400 - shadow*scale + bleed)
                            cards.add(Sprite($(ImageRect(new RawImage(img(u.faction.short + "-agent")), Rectangle(0, 0, 42*scale, 68*scale), 1.0)), $))(card.width * i + d * i + card.width / 2 - w / 2 * l.num + w * l.indexOf(u), 400 + bleed)
                        }
                    }
                }
            }

            val scene = new Scene($(cards), width, height, margins)

            |(scene)
        }

        def processHighlight(target : $[Any], xy : XY) {
        }

        def processTargetClick(target : $[Any], xy : XY) {
            onClick(target)
        }

        def adjustCenterZoom() {
            dY -= zoomBase * 2.5

            zoomBase = 0

            dX = 0
            dY = dY.clamp(0, card.height + d - bleed)
        }

        dY = card.height / 2 * 0
    }

    val ambitions : |[CanvasPane] = campaign.?(new CanvasPaneX(newPane("ambitions", Content), 2/2, Inside)(resources) {
        def makeScene() = {
            val mp = img("ambitions")

            val ququ = 96

            val background = new OrderedLayer
            background.add(Sprite($(ImageRect(new RawImage(mp), Rectangle(0, -ququ, mp.width, mp.height), 1.0)), $))(0, 0)

            val tokens = new OrderedLayer

            game.ambitionable.lift(0)./(m => tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(20, 82-ququ))
            game.ambitionable.lift(1)./(m => tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(154, 82-ququ))
            game.ambitionable.lift(2)./(m => tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(288, 82-ququ))

            game.declared.get(Tycoon)./(l => l.indexed./{ (m, i) =>
                tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(154 + 134*(2*i - l.num + 1)/2, 270-ququ)
            })
            game.declared.get(Tyrant)./(l => l.indexed./{ (m, i) =>
                tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(154 + 134*(2*i - l.num + 1)/2, 470-ququ)
            })
            game.declared.get(Warlord)./(l => l.indexed./{ (m, i) =>
                tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(154 + 134*(2*i - l.num + 1)/2, 668-ququ)
            })
            game.declared.get(Keeper)./(l => l.indexed./{ (m, i) =>
                tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(154 + 134*(2*i - l.num + 1)/2, 867-ququ)
            })
            game.declared.get(Empath)./(l => l.indexed./{ (m, i) =>
                tokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 123, 139), 1.0)), $))(154 + 134*(2*i - l.num + 1)/2, 1067-ququ)
            })

            background.add(Sprite($(ImageRect(new RawImage(img("resources")), Rectangle(0, 0, 431, 76), 1.0)), $))(0, 1267-ququ)

            if (game.factions.forall(game.states.contains)) {
                if (game.factions.exists(_.can(MaterialCartel)).not)
                1.to(game.availableNum(Material)).foreach { i =>
                    tokens.add(Sprite($(ImageRect(new RawImage(img(Material.id)), Rectangle(-35, -35, 70, 70), 1.0)), $))(217 - 75*2, 1310-ququ + i * 75)
                }

                if (game.factions.exists(_.can(FuelCartel)).not)
                1.to(game.availableNum(Fuel)).foreach { i =>
                    tokens.add(Sprite($(ImageRect(new RawImage(img(Fuel.id    )), Rectangle(-35, -35, 70, 70), 1.0)), $))(217 - 75*1, 1310-ququ + i * 75)
                }

                1.to(game.availableNum(Weapon)).foreach { i =>
                    tokens.add(Sprite($(ImageRect(new RawImage(img(Weapon.id  )), Rectangle(-35, -35, 70, 70), 1.0)), $))(217 + 75*0, 1310-ququ + i * 75)
                }

                1.to(game.availableNum(Relic)).foreach { i =>
                    tokens.add(Sprite($(ImageRect(new RawImage(img(Relic.id   )), Rectangle(-35, -35, 70, 70), 1.0)), $))(217 + 75*1, 1310-ququ + i * 75)
                }

                1.to(game.availableNum(Psionic)).foreach { i =>
                    tokens.add(Sprite($(ImageRect(new RawImage(img(Psionic.id )), Rectangle(-35, -35, 70, 70), 1.0)), $))(217 + 75*2, 1310-ququ + i * 75)
                }
            }

            background.add(Sprite($(ImageRect(new RawImage(img("edicts")), Rectangle(0, 0, 431, 76), 1.0)), $))(0, 1760-ququ)

            object card {
                val width = 431
                val height = 602
            }

            game.edicts.indexed./ { (e, i) =>
                tokens.add(Sprite($(ImageRect(new RawImage(img(e.id)), Rectangle(0, 0, card.width, card.height), 1.0)), $(Rectangle(0, 0, card.width, card.height)), $(e)))(0, 1850-ququ + i*610)
            }


            val scene = new Scene($(background, tokens), 431, 1600, Margins(0, 0, 0, 0))

            |(scene)
        }

        def processHighlight(target : $[Any], xy : XY) {
        }

        def processTargetClick(target : $[Any], xy : XY) {
            onClick(target)
        }

        def adjustCenterZoom() {
            dY -= zoomBase * 2.5

            zoomBase = 0

            dX = 0
        }
    })

    object regions {
        val centers = Map[System, XY](
            System(1, Gate) -> XY(1300, 550),
            System(1, Arrow) -> XY(1050, 360),
            System(1, Crescent) -> XY(1320, 130),
            System(1, Hex) -> XY(1630, 400),
            System(2, Gate) -> XY(1630, 780),
            System(2, Arrow) -> XY(1810, 580),
            System(2, Crescent) -> XY(1920, 730),
            System(2, Hex) -> XY(1900, 900),
            System(3, Gate) -> XY(1590, 1110),
            System(3, Arrow) -> XY(1860, 1060),
            System(3, Crescent) -> XY(2110, 1420),
            System(3, Hex) -> XY(1670, 1370),
            System(4, Gate) -> XY(1170, 1260),
            System(4, Arrow) -> XY(1350, 1660),
            System(4, Crescent) -> XY(940, 1700),
            System(4, Hex) -> XY(570, 1630),
            System(5, Gate) -> XY(870, 990),
            System(5, Arrow) -> XY(640, 1230),
            System(5, Crescent) -> XY(240, 1160),
            System(5, Hex) -> XY(530, 900),
            System(6, Gate) -> XY(910, 690),
            System(6, Arrow) -> XY(630, 730),
            System(6, Crescent) -> XY(630, 520),
            System(6, Hex) -> XY(830, 450),
        )

        val gates = Map[System, $[XY]](
            System(1, Arrow) -> $(XY(1049, 223), XY(1166, 170)),
            System(1, Crescent) -> $(XY(1434, 212)),
            System(1, Hex) -> $(XY(1745, 147), XY(1846, 228)),
            System(2, Arrow) -> $(XY(2010, 440)),
            System(2, Crescent) -> $(XY(2300, 618)),
            System(2, Hex) -> $(XY(2116, 880), XY(2221, 936)),
            System(3, Arrow) -> $(XY(2186, 1127)),
            System(3, Crescent) -> $(XY(1846, 1249)),
            System(3, Hex) -> $(XY(1929, 1534), XY(1830, 1610)),
            System(4, Arrow) -> $(XY(1529, 1573), XY(1430, 1497)),
            System(4, Crescent) -> $(XY(1060, 1584), XY(1159, 1660)),
            System(4, Hex) -> $(XY(776, 1505)),
            System(5, Arrow) -> $(XY(255, 1458)),
            System(5, Crescent) -> $(XY(434, 1101)),
            System(5, Hex) -> $(XY(223, 876), XY(125, 952)),
            System(6, Arrow) -> $(XY(431, 683)),
            System(6, Crescent) -> $(XY(397, 313), XY(299, 389)),
            System(6, Hex) -> $(XY(678, 228)),
        )

        lazy val place = new IndexedImageRegions[System](new RawImage(img("map-regions")), 0, 0, centers)

        lazy val select = new IndexedImageRegions[System](new RawImage(img("map-regions-select")), 0, 0, centers)
    }

    lazy val pieces = new FitLayer[System, Figure](regions.place, FitOptions())

    lazy val outOfPlay = new OrderedLayer

    lazy val highlighted = new OrderedLayer

    lazy val ambTokens = new OrderedLayer

    val width = 2528
    val height = 1776
    val margins = Margins(0, 0, 0, 0)

    lazy val scene = {
        val mp = img("map-no-slots")
        val mr = img("map-regions")
        val ms = img("map-regions-select")

        val background = new OrderedLayer
        background.add(Sprite($(ImageRect(new RawImage(mp), Rectangle(0, 0, mp.width, mp.height), 1.0)), $))(0, 0)

        val areas = new HitLayer(regions.select)

        new Scene($(background, outOfPlay, highlighted, areas, pieces, ambTokens), mp.width, mp.height, margins)
    }

    override def adjustCenterZoomX() {
        zoomBase = zoomBase.clamp(-990, 990*2)

        val qX = (width + margins.left + margins.right) * (1 - 1 / zoom) / 2
        val minX = -qX + margins.right - zoomBase / 5
        val maxX = qX - margins.left + zoomBase / 5
        dX = dX.clamp(minX, maxX)

        val qY = (height + margins.top + margins.bottom) * (1 - 1 / zoom) / 2
        val minY = -qY + margins.bottom - zoomBase / 5
        val maxY = qY - margins.top + zoomBase / 5
        dY = dY.clamp(minY, maxY)
    }

    var highlightCoordinates : |[XY] = None
    var highlightAssassinate = $[Figure]()
    var highlightFire = $[Figure]()
    var highlightBuy = $[Figure]()
    var highlightMove = $[Figure]()
    var highlightRemoveTrouble = $[Figure]()
    var highlightSpreadTrouble = $[System]()
    var highlightPlaceMinion = Map[System, $[Color]]()
    var highlightBuild = Map[System, $[Color]]()
    var highlightUseBuilding = $[Figure]()
    var highlightAreas = $[System]()

    def processRightClick(target : $[Any], xy : XY) {
        // lastActions.of[Cancel].single.foreach(onClick)
    }

    def processHighlight(target : $[Any], xy : XY) {
        highlightCoordinates = |(xy)
    }

    def processTargetClick(target : $[Any], xy : XY) {
        lastActions.of[Cancel].single.foreach { a =>
            return onClick(a)
        }

        println("processTargetClick unresolved " + target)
    }

    case class Plaque(area : System)

    def makeScene() : |[Scene] = {
        if (img("map-no-slots").complete.not | img("map-regions").complete.not | img("map-regions-select").complete.not)
            return None

        outOfPlay.clear()

        1.to(6).diff(game.board.clusters).foreach { i =>
            val mo = img("map-out-" + i)

            outOfPlay.add(Sprite($(ImageRect(new RawImage(mo), Rectangle(0, 0, mo.width, mo.height), 1.0)), $))(0, 0)
        }

        1.to(6).diff(game.board.clusters).intersect($(3)).$.starting.foreach { i =>
            val am = img("map-ambitions-" + i)

            outOfPlay.add(Sprite($(ImageRect(new RawImage(am), Rectangle(0, 0, am.width, am.height), 1.0)), $))(0, 0)
        }

        pieces.flush()

        case class ResourceMarker(s : System) extends Piece

        systems.reverse.foreach { s =>
            game.resources(s).%(_ => s.gate.not).foreach { r =>
                val (x, y) = s @@ {
                    case System(1, Arrow) => (1045, 125)
                    case System(1, Crescent) => (1455, 120)
                    case System(1, Hex) => (1860, 115)
                    case System(2, Arrow) => (2100, 390)
                    case System(2, Crescent) => (2405, 585)
                    case System(2, Hex) => (2300, 880)
                    case System(3, Arrow) => (2300, 1175)
                    case System(3, Crescent) => (1965, 1315)
                    case System(3, Hex) => (1965, 1680)
                    case System(4, Arrow) => (1445, 1645)
                    case System(4, Crescent) => (1050, 1690)
                    case System(4, Hex) => (685, 1580)
                    case System(5, Arrow) => (160, 1525)
                    case System(5, Crescent) => (330, 1130)
                    case System(5, Hex) => (140, 800)
                    case System(6, Arrow) => (330, 660)
                    case System(6, Crescent) => (255, 310)
                    case System(6, Hex) => (610, 185)
                    case _ => (1150 + random(100), 800 + random(100))
                }

                val rect = Rectangle(-32, -32, 64, 64)
                val hit = Rectangle(-24, -24, 48, 48)

                pieces.addFixed(s, Figure(Free, ResourceMarker(s), 1), 0)(Sprite($(ImageRect(new RawImage(img(r.id)), rect, 1.0)), $(hit)))(x, y)
            }
        }

        val shipScale = callbacks.settings.of[ShipsSizeOption].starting @@ {
            case Some(StandardShipsSize) | None => 100
            case Some(SmallShipsSize) => 92
            case Some(SmallerShipsSize) => 84
            case Some(SmallestShipsSize) => 76
        } / 100.0

        systems.reverse.foreach { s =>
            var figures = game.at(s)
            var gates = regions.gates.get(s).|(Nil).sortBy(_.y)

            var extra : $[Figure] = $

            if (game.leaders.any) {
                game.starting.lazyZip(factions).foreach { case ((a, b, c), f) =>
                    if (a == s)
                        pieces.addFixed(s, Figure(f, Agent, 1), 5)(Sprite($(ImageRect(img(f.short + "-agent-a"), 21, 66/2, 2)), $))(gates.first.x, gates.first.y)

                    if (b == s)
                        pieces.addFixed(s, Figure(f, Agent, 2), 5)(Sprite($(ImageRect(img(f.short + "-agent-b"), 21, 66/2, 2)), $))(gates.first.x, gates.first.y)

                    if (c.has(s))
                        pieces.addFloat(s, Figure(f, Agent, 3), 5)(Sprite($(ImageRect(img(f.short + "-agent-c"), 21, 66/2, 2)), $))
                }
            }

            import hrf.ui.sprites._

            (extra ++ figures ++ 1.to(game.freeSlots(s))./(i => Figure(Free, Slot, systems.indexOf(s) * 10 + i))).foreach { p =>
                def prefix = p.faction.as[Faction]./(_.short.toLowerCase + "-") || (p.faction == Empire).?("imperial-") || (p.faction == Free).?("free-") | ""

                val target = false

                val selected = extra.has(p)

                val status = (p.piece != Slot).??(p.faction.damaged.has(p).??("-damaged"))

                val a = p.piece match {
                    case Slot => $(ImageRect(img("city-empty"), 61, 61, 1.0 + 0.4 * selected.??(1)).copy(alpha = 0.4))
                    case City => $(ImageRect(img(prefix + "city" + status), 61, 61, 1.0 + 0.4 * selected.??(1)))
                    case Starport => $(ImageRect(img(prefix + starport + status), 61, 61, 1.0 + 0.4 * selected.??(1)))
                    case Ship => $(ImageRect(img(prefix + "ship" + status), 97, 71, shipScale + 0.4 * selected.??(1)))
                    case Blight => $(ImageRect(img("blight" + status), 43, 79, 1.0 + 0.4 * selected.??(1)))
                    case Agent => $(ImageRect(img(prefix + "agent" + status), 21, 66, 1.4))
                }

                var q = p.piece match {
                    case Slot | City | Starport => Sprite(a, $(Rectangle(-60, 21, 120, 28), Rectangle(-46, -7, 92, 28), Rectangle(-32, -35, 64, 28), Rectangle(-18, -54, 36, 19)), $((s, p)))
                    case Ship => Sprite(a, $(Rectangle(-70, -10, 135, 20), Rectangle(-97, -30, 194, 20), Rectangle(5, -50, 75, 20), Rectangle(35, -70, 35, 20)), $((s, p)))
                    case _ => Sprite(a, $(a.head.rect))
                }

                var z = p.piece match {
                    case Ship | Blight | Agent => 4
                    case Slot | City | Starport => 3
                }

                if (extra.has(p)) {
                    q = q.copy(images = q.images./(i => i.copy(alpha = 0.7)), hitboxes = $)
                    pieces.addFixed(s, p, z + 8)(q)(highlightCoordinates.get.x, highlightCoordinates.get.y)
                }
                else
                if (p.piece == Starport && gates.any && game.unslotted.has(p).not) {
                    gates.starting.foreach { g =>
                        pieces.addFixed(s, p, z)(q)(g.x, g.y)
                    }
                    gates = gates.dropFirst
                }
                else
                if (p.piece.is[Building] && gates.any && game.unslotted.has(p).not) {
                    gates.ending.foreach { g =>
                        pieces.addFixed(s, p, z)(q)(g.x, g.y)
                    }
                    gates = gates.dropLast
                }
                else {
                    val xy = pieces.addFloat(s, p, z)(q)
                }
            }
        }

        highlighted.clear()

        ambTokens.clear()

        systems.reverse.foreach { s =>
            game.overridesHard.get(s).foreach { r =>
                val (x, y) = s @@ {
                    case System(1, Arrow) => (1045, 125)
                    case System(1, Crescent) => (1455, 120)
                    case System(1, Hex) => (1860, 115)
                    case System(2, Arrow) => (2100, 390)
                    case System(2, Crescent) => (2405, 585)
                    case System(2, Hex) => (2300, 880)
                    case System(3, Arrow) => (2300, 1175)
                    case System(3, Crescent) => (1965, 1315)
                    case System(3, Hex) => (1965, 1680)
                    case System(4, Arrow) => (1445, 1645)
                    case System(4, Crescent) => (1050, 1690)
                    case System(4, Hex) => (685, 1580)
                    case System(5, Arrow) => (160, 1525)
                    case System(5, Crescent) => (330, 1130)
                    case System(5, Hex) => (140, 800)
                    case System(6, Arrow) => (330, 660)
                    case System(6, Crescent) => (255, 310)
                    case System(6, Hex) => (610, 185)
                    case _ => (1150 + random(100), 800 + random(100))
                }

                ambTokens.add(Sprite($(ImageRect(new RawImage(img(r.id)), Rectangle(-32, -32, 64, 64), 1.0)), $), 1.28, 10)(x, y)
            }
        }

        1.to(6).diff(game.board.clusters).intersect($(3)).$.starting.foreach { i =>
            val x1 = 1724
            val y1 = 1017

            game.ambitionable.lift(0)./(m => ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))( 34+x1, 82+y1))
            game.ambitionable.lift(1)./(m => ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(154+x1, 82+y1))
            game.ambitionable.lift(2)./(m => ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(274+x1, 82+y1))

            game.declared.get(Tycoon)./(l => l.indexed./{ (m, i) =>
                ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(154 + 120*(2*i - l.num + 1)/2+x1, 250+y1)
            })

            val x2 = 1724 + 391
            val y2 = 1017 - 359
            game.declared.get(Tyrant)./(l => l.indexed./{ (m, i) =>
                ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(154 + 120*(2*i - l.num + 1)/2+x2, 430+y2)
            })
            game.declared.get(Warlord)./(l => l.indexed./{ (m, i) =>
                ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(154 + 120*(2*i - l.num + 1)/2+x2, 608+y2)
            })
            game.declared.get(Keeper)./(l => l.indexed./{ (m, i) =>
                ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(154 + 120*(2*i - l.num + 1)/2+x2, 787+y2)
            })
            game.declared.get(Empath)./(l => l.indexed./{ (m, i) =>
                ambTokens.add(Sprite($(ImageRect(new RawImage(img("ambition-values-" + m.high + "-" + m.low)), Rectangle(0, 0, 111, 125), 1.0)), $))(154 + 120*(2*i - l.num + 1)/2+x2, 967+y2)
            })

            if (game.factions.forall(game.states.contains)) {
                if (game.factions.exists(_.can(MaterialCartel)).not)
                1.to(game.availableNum(Material)).foreach { i =>
                    ambTokens.add(Sprite($(ImageRect(new RawImage(img(Material.id)), Rectangle(-32, -32, 64, 64), 1.0)), $))(1933 - 68*2, 1390 + i * 68)
                }

                if (game.factions.exists(_.can(FuelCartel)).not)
                1.to(game.availableNum(Fuel)).foreach { i =>
                    ambTokens.add(Sprite($(ImageRect(new RawImage(img(Fuel.id    )), Rectangle(-32, -32, 64, 64), 1.0)), $))(1933 - 68*1, 1390 + i * 68)
                }

                1.to(game.availableNum(Weapon)).foreach { i =>
                    ambTokens.add(Sprite($(ImageRect(new RawImage(img(Weapon.id  )), Rectangle(-32, -32, 64, 64), 1.0)), $))(1933 + 68*0, 1390 + i * 68)
                }

                1.to(game.availableNum(Relic)).foreach { i =>
                    ambTokens.add(Sprite($(ImageRect(new RawImage(img(Relic.id   )), Rectangle(-32, -32, 64, 64), 1.0)), $))(1933 + 68*1, 1390 + i * 68)
                }

                1.to(game.availableNum(Psionic)).foreach { i =>
                    ambTokens.add(Sprite($(ImageRect(new RawImage(img(Psionic.id )), Rectangle(-32, -32, 64, 64), 1.0)), $))(1933 + 68*2, 1390 + i * 68)
                }
            }
        }

        |(scene)
    }

    def factionStatus(f : Faction) {
        val container = statuses(game.seating.indexOf(f))

        val name = resources.getName(f).|(f.name)

        if (!game.states.contains(f)) {
            container.replace(Div(Div(name).styled(f), styles.smallname, xlo.pointer), resources)
            return
        }

        val initative = game.seized.%(_ == f)./(_ => DoubleDagger).||((game.factions.first == f && game.seized.none).?(Dagger)).|("")
        val bonus = (f.pooled(City) < 1).?(DoubleAsterisk).||((f.pooled(City) < 2).?(LowAsterisk)).|("")

        val title = (initative.styled(styles.title)(styles.initative) ~ name).div.styled(f).div(styles.smallname)(xlo.pointer)
        val hand = Hint("Hand: " + f.hand.num + " cards",
            f.hand.none.?("~~~".txt ~ Image("card-back-small", styles.fund, xlo.hidden)).|(
                (f.hand.num > 55).?(
                    (f.hand.num / 5).times(Image("card-back-5", styles.fund)) ~ (f.hand.num % 5).times(Image("card-back-small", styles.fund))
                ).|(
                    (f.hand.num).times(Image("card-back-small", styles.fund)).merge
                )
            ).spn(styles.hand)
        )

        val powerHand = (bonus.styled(styles.bonus) ~ f.power.power ~ " ".pre ~ hand).div(xstyles.larger110)

        val outrage = f.outraged./(r => Image(r.name + "-outrage", styles.token)).merge.div(styles.outrageLine)

        val ww = max(f.resourceSlots, f.resources.num)
        val keys = (f.keys.take(f.resourceSlots)./(n => Image("half-keys-" + n, styles.token)) ++ (ww - f.resourceSlots).times(Image("half-keys-1", styles.token)(xstyles.hidden))).merge.div(styles.keyLine)
        val res = (f.resources./(r => Image(r.name, styles.token)).merge ~ (ww - f.resources.num).times(Image("nothingness", styles.token))).div
        val pieces = (
            (
                (5 - f.pooled(City)).hlb.styled(xstyles.smaller85) ~ "×" ~ Image(f.short + "-city", styles.building) ~ " " ~
                (5 - f.pooled(Starport)).hlb.styled(xstyles.smaller85) ~ "×" ~ Image(f.short + "-" + starport, styles.building)
            ).& ~ " " ~
            (
                (15 - f.pooled(Ship)).hlb.styled(xstyles.smaller85) ~ "×" ~ Image(f.short + "-ship", styles.ship) ~ " " ~
                (10 - f.pooled(Agent) - f.outraged.num).hlb.styled(xstyles.smaller85) ~ "×" ~ Image(f.short + "-agent", styles.ship)
            ).&
        ).div

        val trophies = f.trophies./( u =>
            Image(u.faction.short + "-" + u.piece.name + "-damaged", u.piece.is[Building].?(styles.building).|(styles.ship))
        )./(_.&).join(" ").div

        val captives = f.captives./( u =>
            Image(u.faction.short + "-" + u.piece.name, u.piece.is[Building].?(styles.building).|(styles.ship))
        )./(_.&).join(" ").div

        val leader = f.leader.$./(l => l.elem.div(xstyles.smaller75)(styles.cardName).pointer.onClick.param(l)).merge
        val subtitle0 = game.campaign.?((f.primus.?("\u0158").||(f.regent.?("\u0158")).|("\u00D8")).styled(f.regent.?(Empire).|(Free)).spn(styles.title)(styles.cardName).pointer.onClick.param(None))
        val fate = f.fates./(l => subtitle0 ~ " " ~ l.elem.pointer.onClick.param(l) ~ (" " ~ "\u23F1".hlb ~ f.progress.hl).pointer.onClick.param(f.objective))./(_.div(xstyles.smaller75)(styles.cardName)).merge
        val lores = f.lores./(l => l.elem.div(xstyles.smaller75)(styles.cardName).pointer.onClick.param(l)).merge
        val subtitles = (game.campaign && f.primus).?((f.primus.?("First Regent").||(f.regent.?("Regent")).|("Outlaw")).styled(f.regent.?(Empire).|(Free))(styles.title).div(xstyles.smaller75)(styles.cardName).pointer.onClick.param(None))

        val loyal = f.loyal.$.of[GuildCard]./(c => ((Image(c.suit.name, styles.tokenTop) ~ c.elem ~ (c.keys < 999).?(Image("keys-" + c.keys, styles.tokenTop))).div(xstyles.smaller75) ~ (c.effect @@ {
            case MaterialCartel => game.availableNum(Material).times(Image(Material.name, styles.tokenTop)).merge
            case FuelCartel => game.availableNum(Fuel).times(Image(Fuel.name, styles.tokenTop)).merge
            case _ => Empty
        })).pointer.onClick.param(c)).merge

        val play =
            f.played.starting./ { d => (
                f.zeroed.?(Image("zeroed", styles.plaque)) ~
                (f.lead && f.zeroed.not).?(Image(d.suit.name + "-number-" + d.strength, styles.plaque)) ~
                f.mirror.?(Image("event-number", styles.plaque)) ~
                f.surpass.?(Image(d.suit.name + "-number-" + d.strength, styles.plaque)) ~
                f.pivot.?(Image(d.suit.name + "-number-" + d.strength, styles.plaque)) ~

                f.lead.?(Image(d.suit + "-pips-" + d.pips, styles.plaque)) ~
                f.surpass.?(Image(d.suit + "-pips-" + d.pips, styles.plaque)) ~
                f.mirror.?(Image(game.lead.get.suit + "-pips-" + game.lead.get.pips, styles.plaque)) ~
                f.pivot.?(Image(d.suit + "-pips-pivot", styles.plaque)) ~

                f.mirror.not.?(Image(d.suit + "-plaque", styles.plaque)) ~
                f.mirror.?(Image(game.lead.get.suit + "-plaque", styles.plaque))
            ).div(styles.plaqueContainer) }.||(
            f.blind.starting./ { d =>
                (Image("hidden", styles.plaque) ~ Image(game.lead.get.suit + "-pips-copy", styles.plaque) ~ Image(game.lead.get.suit + "-plaque", styles.plaque)).div(styles.plaqueContainer)
            }).|(
                Empty
            )

        val seized = game.seized.%(_ == f)./(_ => "Seized Initative".hl.div(styles.title)(xstyles.smaller50)).||((game.factions(0) == f && game.seized.none).?("Initative".hl.div(styles.title)(xstyles.smaller50))).|(Empty)

        val content = ((title ~ powerHand ~ leader ~ fate ~ lores ~ subtitles ~ outrage ~ keys ~ res ~ loyal ~ trophies ~ captives).div ~ "~".txt.div(xstyles.hidden) ~ play).div(styles.statusUpper)(xlo.flexVX)(ExternalStyle("hide-scrollbar")).pointer.onClick.param(f) ~
            play.div(styles.play)

        container.replace(content, resources, {
            case f : Faction => onFactionStatus(f, false)
            case x => onClick(x)
        })

        if (f == game.current && game.isOver)
            container.attach.parent.style.background = f @@ {
                case Red => "#680016"
                case Yellow => "#684f19"
                case Blue => "#05274c"
                case White => "#666666"
            }
        else
        if (f == game.current)
            container.attach.parent.style.outline = "2px solid #aaaaaa"
        else
        if (game.highlightFaction.has(f))
            container.attach.parent.style.outline = "2px dashed #aaaaaa"
        else
            container.attach.parent.style.outline = ""
    }

    def onFactionStatus(implicit f : Faction, isMore : Boolean) : Unit = {
        def desc(l : Any*) = game.desc(l : _*).div
        def more(l : Any*) = isMore.?(desc(l : _*))
        def less(l : Any*) = isMore.not.?(desc(l : _*))
        def moreGap = isMore.?(HGap)
        def lessGap = isMore.not.?(HGap)

        def info() =
            less(("More Info".hh).div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(new CustomStyle(rules.width("60ex"))(new StylePrefix("test")){}).pointer.onClick.param(f, !isMore)) ~
            more(("Less Info".hh).div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(new CustomStyle(rules.width("60ex"))(new StylePrefix("test")){}).pointer.onClick.param(f, !isMore))

        val ww = max(f.resourceSlots, f.resources.num)

        showOverlay(overlayScrollX((
            HGap ~
            HGap ~
            HGap ~
            f.elem.larger.larger.larger.styled(xstyles.bold) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            desc("Power".hl.larger) ~
            more("(victory points)") ~
            HGap ~
            HGap ~
            desc(f.power.power.larger.larger) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            (f.leader.any).?{
                desc("Leader".hl.larger) ~
                HGap ~
                HGap ~
                desc(f.leader./(l => OnClick(l, l.img))) ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap
            } ~
            (f.lores.any).?{
                desc("Lore".hl.larger) ~
                HGap ~
                HGap ~
                f.lores./(l => OnClick(l, Div(l.img, styles.cardX, xstyles.xx, styles.inline, styles.nomargin, xlo.pointer))).merge.div ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap
            } ~
            desc("Cards".hl.larger) ~
            HGap ~
            desc(f.hand.num.times(Image("card-back", styles.token3x))) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            (f.outraged.any).?{
                desc("Outrage".hl.larger) ~
                more("(resources of these type can't be used for actions)") ~
                HGap ~
                HGap ~
                desc(f.outraged./(r => Image(r.name + "-outrage", styles.token3x))) ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap
            } ~
            desc("Resources".hl.larger) ~
            HGap ~
            HGap ~
            desc(f.keys.take(f.resourceSlots)./(n => Image("keys-" + n, styles.token3x)) ++ (ww - f.resourceSlots).times(Image("discard-resource", styles.token3x))) ~
            HGap ~
            desc(f.resources./(r => Image(r.name, styles.token3x)) ++ (ww - f.resources.num).times(Image("nothingness", styles.token3x))) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            desc("Cities".hl.larger) ~
            HGap ~
            HGap ~
            desc(systems./~(f.at(_).cities)./(u => Image(u.faction.short + "-city" + f.damaged.has(u).??("-damaged"), styles.token3x)),
                $(
                    Image("building-empty-keys-1", styles.token3x),
                    Image("building-empty-keys-2", styles.token3x),
                    Image("building-empty-keys-1-3", styles.token3x),
                    Image("building-empty-plus-2", styles.token3x),
                    Image("building-empty-plus-3", styles.token3x),
                ).drop(5 - f.pooled(City))
            ) ~
            (f.pooled(City) < 2).?(desc("Total bonus for won ambitions", "+" ~ ((f.pooled(City) < 2).??(2) + (f.pooled(City) < 1).??(3)).power)) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            desc("Starports".hl.larger) ~
            HGap ~
            HGap ~
            desc(systems./~(f.at(_).starports)./(u => Image(u.faction.short + "-" + starport + f.damaged.has(u).??("-damaged"), styles.token3x)), f.pooled(Starport).times(Image(starport + "-empty", styles.token3x))) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            desc("Ships".hl.larger) ~
            HGap ~
            HGap ~
            (systems./~(f.at(_).ships)./(u => Image(u.faction.short + "-ship" + f.damaged.has(u).??("-damaged"), styles.ship3x)) ++ f.pooled(Ship).times(Image("ship-empty", styles.ship3x))).grouped(5)./(desc) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            desc("Agents".hl.larger) ~
            HGap ~
            HGap ~
            desc(game.market./~(Influence(_).$).%(_.faction == f)./(u => Image(u.faction.short + "-agent", styles.ship3x)), f.pooled(Agent).times(Image("agent-empty", styles.ship3x))) ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            (f.trophies.any).? {
                desc("Trophies".hl.larger) ~
                HGap ~
                HGap ~
                f.trophies./(u => Image(u.faction.short + "-" + u.piece.name + "-damaged", (u.piece == Ship).?(styles.ship3x).|(styles.ship3x))).grouped(5)./(desc) ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap
            } ~
            (f.captives.any).? {
                desc("Captives".hl.larger) ~
                HGap ~
                HGap ~
                desc(f.captives./(u => Image(u.faction.short + "-" + u.piece.name, (u.piece == Ship).?(styles.ship3x).|(styles.ship3x)))) ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap
            } ~
            (f.loyal.any).? {
                desc("Loyalists".hl.larger) ~
                HGap ~
                f.loyal./(c => OnClick(c, Div(Image(c.id, styles.courtCard), styles.cardX, xstyles.xx, styles.inline, styles.nomargin, xlo.pointer))).merge.div ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap
            } ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            info() ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap ~
            HGap
        ).div(xlo.flexvcenter)(styles.infoStatus)), {
            case (f : Faction, more : Boolean) => onFactionStatus(f, more)
            case (c : CourtCard) => onClick(c)
            case (l : Leader) => onClick(l)
            case (l : Lore) => onClick(l)
            case _ =>
                overlayPane.invis()
                overlayPane.clear()
        })
    }

    def updateStatus() {
        0.until(arity).foreach { n =>
            factionStatus(game.seating(n))
        }

        if (overlayPane.visible)
            overlayPane.vis()
        else
            overlayPane.invis()

        drawMap()
        court.draw()
        ambitions.foreach(_.draw())
    }

    val layoutZoom = 0.49 * 0.88

    val kkk = 1.18

    def layouts = $(Layout("base",
        $(
            BasicPane("status", 5*kkk*arity, 22*kkk, Priorities(top = 3, left = 2, maxXscale = 1.8*11111111, maxYscale = 1.8, grow = 1)),
            BasicPane("court", 15.375*kkk*(4 + campaign.??(1)), 22*kkk*(1 + campaign.??(1)*0.0620), Priorities(top = 3, right = 3, maxXscale = 1.0, maxYscale = 1.0, grow = 0)),
            BasicPane("log", 32+6, 13+3, Priorities(right = 1)),
            BasicPane("map-small", 71, 50, Priorities(top = 2, left = 1, grow = -1)),
            BasicPane("action-a", 64, 36, Priorities(bottom = 1, right = 3, grow = 1)),
            BasicPane("action-b", 55, 47, Priorities(bottom = 1, right = 3, grow = 1, maxXscale = 1.2))
        ).++(
            campaign.?(BasicPane("ambitions", 16, 60, Priorities(grow = -4)))
        )
       ./(p => p.copy(kX = p.kX * layoutZoom, kY = p.kY * layoutZoom))
    ))./~(l =>
        l.copy(name = l.name + "-fulldim", panes = l.panes./{
            case p : BasicPane if p.name == "map-small" => FullDimPane(p.name, p.kX, p.kY, p.pr)
            case p => p
        }, boost = 1.2) ::
        l.copy(name = l.name + "-plus20", panes = l.panes./{
            case p : BasicPane if p.name == "map-small" => BasicPane(p.name, p.kX * 1.2, p.kY * 1.2, p.pr)
            case p => p
        }, boost = 1.1) ::
        l.copy(name = l.name + "-normal") ::
        $
    )./~(l =>
        callbacks.settings.has(VerticalFactionPanes).not.$(
            l.copy(name = l.name + "-horizontal", boost = l.boost * 1.02, panes = l.panes./{
                case p : BasicPane if p.name == "status" => p.copy(name = "status-horizontal", kX = p.kX * arity)
                case p => p
            })
        ) ++
        callbacks.settings.has(HorizontalFactionPanes).not.$(
            l.copy(name = l.name + "-vertical", panes = l.panes./{
                case p : BasicPane if p.name == "status" => p.copy(name = "status-vertical", kY = p.kY * arity)
                case p => p
            })
        )
    )./~(l =>
        l.copy(name = l.name + "-actionA", panes = l.panes./~{
            case p : BasicPane if p.name == "action-a" => Some(p.copy(name = "action"))
            case p : BasicPane if p.name == "action-b" => None
            case p => Some(p)
        }) ::
        l.copy(name = l.name + "-actionB", panes = l.panes./~{
            case p : BasicPane if p.name == "action-a" => None
            case p : BasicPane if p.name == "action-b" => Some(p.copy(name = "action"))
            case p => Some(p)
        }) ::
        $
    )

    def layouter = Layouter(layouts,
    _./~{
        case f if f.name == "action" => $(f, f.copy(name = "undo"), f.copy(name = "settings"))
        case f if f.name == "status-horizontal" => 1.to(arity)./(n => f.copy(name = "status-" + n, x = f.x + ((n - 1) * f.width  /~/ arity), width  = (n * f.width  /~/ arity) - ((n - 1) * f.width  /~/ arity)))
        case f if f.name == "status-vertical"   => 1.to(arity)./(n => f.copy(name = "status-" + n, y = f.y + ((n - 1) * f.height /~/ arity), height = (n * f.height /~/ arity) - ((n - 1) * f.height /~/ arity)))
        case f => $(f)
    },
    x => x,
    ff => ff :+ Fit("map-small-overlay", ff./(_.x).min, ff./(_.y).min, ff./(_.right).max - ff./(_.x).min, ff./(_.bottom).max - ff./(_.y).min))

    val settingsKey = Meta.settingsKey

    def layoutKey = "v" + 4 + "." + campaign.?("campaign").|("base") + "." + callbacks.settings.has(VerticalFactionPanes).??("ver.") + callbacks.settings.has(HorizontalFactionPanes).??("hor.") + "arity-" + arity

    def overlayScrollX(e : Elem) = overlayScroll(e)(styles.seeThroughInner).onClick
    def overlayFitX(e : Elem) = overlayFit(e)(styles.seeThroughInner).onClick

    def showOverlay(e : Elem, onClick : Any => Unit) {
        overlayPane.vis()
        overlayPane.replace(e, resources, onClick, _ => {}, _ => {})
    }

    override def onClick(a : Any) = a @@ {
        case ("notifications", Some(f : Faction)) =>
            shown = $
            showNotifications($(f))

        case ("notifications", None) =>
            shown = $
            showNotifications(game.factions)

        case card : DeckCard =>
            showOverlay(overlayFitX(Image(card.id, styles.artwork)).onClick, onClick)

        case card : CourtCard =>
            showOverlay(overlayFitX(Image(card.id, styles.artwork)).onClick, onClick)

        case fate : Fate =>
            showOverlay(overlayFitX(Image(fate.id, styles.artwork)).onClick, onClick)

        case leader : Leader =>
            showOverlay(overlayFitX(Image(leader.id, styles.artwork)).onClick, onClick)

        case lore : Lore =>
            showOverlay(overlayFitX(Image(lore.id, styles.artwork)).onClick, onClick)

        case edict : Edict =>
            showOverlay(overlayFitX(Image(edict.id, styles.artwork)).onClick, onClick)

        case objective : Objective =>
            showOverlay(overlayFitX(Image(objective.id, styles.artwork)).onClick, onClick)

        case ability : Ability =>
            showOverlay(overlayFitX(Image(ability.id, styles.artwork)).onClick, onClick)

        case law : Law =>
            showOverlay(overlayFitX(Image(law.id, styles.artwork)).onClick, onClick)

        case $(f : Faction, x) =>
            onClick(x)

        case "discourt" =>
            showOverlay(overlayScrollX(Div("Court Cards Discard Pile") ~
                game.discourt./(c => OnClick(c, Div(Image(c.id, styles.courtCard), styles.cardX, xstyles.xx, styles.inline, styles.nomargin, xlo.pointer))).merge
            ).onClick, onClick)

        case "discard" =>
            showOverlay(overlayScrollX(Div("Action Cards Discard Pile") ~
                game.discard./(c => OnClick(c, Div(Image(c.id, styles.card), styles.cardX, xstyles.xx, styles.inline, styles.nomargin, xlo.pointer))).merge
            ).onClick, onClick)

        case "seen" =>
            showOverlay(overlayScrollX(Div("Played Action Cards".hl) ~
                game.seen.groupBy(_._1).$.sortBy(_._1)./{ case (n, l) =>
                    Div("Round " ~ n.hh) ~
                    l./{ case (_, f, d) =>
                        OnClick(d, Div(Image(d./(_.id).|("card-back"), styles.card), xstyles.choice, xstyles.xx, styles.cardI, elem.borders.get(f), styles.inline, xlo.pointer))
                    }.merge
                }.merge
            ).onClick, onClick)

        case "readout" =>
            showOverlay(overlayScrollX((
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                HGap ~
                systems./~(s =>
                    $(
                    game.factions.%(_.rules(s)).single./(f => s.unstyledElem.styled(f)).|(s.name.txt ~ s.smb.txt).larger.styled(xstyles.bold),
                    HGap,
                    (s.gate.not || s.$.cities.any).?(game.desc(game.resources(s)./(r => ResourceRef(r, None)).intersperse(" ")).div).|(Empty),
                    HGap
                    ) ++
                    game.factions./(_.at(s)).%(_.any).sortBy(l => l.buildings.num * 20 + l.ships.num).reverse./(_.sortBy(_.piece.is[Building].not)./(u => Image(u.faction.short + "-" + u.piece.name + u.faction.damaged.has(u).??("-damaged"), (u.piece == Ship).?(styles.ship3x).|(styles.token3x))))./(game.desc(_).div(styles.figureLine)) ++
                    $(game.desc(game.freeSlots(s).times(Image("city-empty", styles.token3x))).div(styles.figureLine)) ++
                    $(
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap,
                    HGap
                    )
                )
            ).div(xlo.flexvcenter)(styles.infoStatus)), onClick)

        case "readout" =>
            showOverlay(overlayScrollX((
                systems./(s =>
                    s.elem.div ~
                    HGap ~
                    HGap ~
                    (game.at(s)./(u => Image(u.faction.short + "-" + u.piece.name + "-damaged", (u.piece == Ship).?(styles.ship3x).|(styles.ship3x)))).merge.div ~
                    HGap ~
                    HGap ~
                    HGap ~
                    HGap ~
                    HGap ~
                    HGap
                ).merge ~
                "".hl.div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(new CustomStyle(rules.width("60ex"))(new StylePrefix("test")){}).pointer.onClick
            ).div(xlo.flexvcenter)(styles.infoStatus)), onClick)

        case action : Action if lastThen != null =>
            clearOverlay()

            highlightAssassinate = $
            highlightFire = $
            highlightBuy = $
            highlightMove = $
            highlightRemoveTrouble = $
            highlightSpreadTrouble = $
            highlightPlaceMinion = Map()
            highlightBuild = Map()

            val then = lastThen
            lastThen = null
            lastActions = $
            keys = $

            asker.clear()

            then(action.as[UserAction].||(action.as[ForcedAction]./(_.as("Do Action On Click"))).|(throw new Error("non-user non-forced action in on click handler")))

        case Nil =>
            clearOverlay()

        case Left(x) => onClick(x)
        case Right(x) => onClick(x)
        case Some(x) => onClick(x)
        case List(x) => onClick(x)

        case x =>
            println("unknown onClick: " + x)
    }

    def clearOverlay() {
        overlayPane.invis()
        overlayPane.clear()
    }

    override def info(self : |[Faction], aa : $[UserAction]) = {
        val ii = currentGame.info($, self, aa)
        ii.any.??($(ZOption(Empty, Break)) ++ convertActions(self.of[Faction], ii)) ++
            (options.has(SplitDiscardPile)).$(ZBasic(Break ~ Break, "Action Cards Discard Pile".hh, () => { onClick("discard") }).copy(clear = false)) ++
            $(ZBasic(Break ~ Break, "Played Action Cards".hh, game.seen.any.??(() => { onClick("seen") })).copy(clear = false)) ++
            $(ZBasic(Break ~ Break, "Court Cards Discard Pile".hh, game.discourt.any.??(() => { onClick("discourt") })).copy(clear = false)) ++
            $(ZBasic(Break ~ Break, "Map Readout".hh, () => { onClick("readout") }).copy(clear = false)) ++
            (currentGame.isOver && hrf.HRF.flag("replay").not).$(
                ZBasic(Break ~ Break ~ Break, "Save Replay As File".hh, () => {
                    showOverlay(overlayScrollX("Saving Replay...".hl.div).onClick, null)

                    callbacks.saveReplay {
                        overlayPane.invis()
                        overlayPane.clear()
                    }
                }).copy(clear = false)
            ) ++
            (hrf.HRF.param("lobby").none && hrf.HRF.offline.not).$(
                ZBasic(Break ~ Break ~ Break, "Save Game Online".hh, () => {
                    showOverlay(overlayScrollX("Save Game Online".hlb(xstyles.larger125) ~
                        ("Save".hlb).div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(xstyles.width60ex).pointer.onClick.param("***") ~
                        ("Save and replace bots with humans".hh).div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(xstyles.width60ex).pointer.onClick.param("///") ~
                        ("Save as a single-player multi-handed game".hh).div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(xstyles.width60ex).pointer.onClick.param("###") ~
                        ("Cancel".txt).div.div(xstyles.choice)(xstyles.xx)(xstyles.chm)(xstyles.chp)(xstyles.thu)(xlo.fullwidth)(xstyles.width60ex).pointer.onClick.param("???")
                    ).onClick, {
                        case "***" => callbacks.saveReplayOnline(false, false) { url => onClick(Nil) }
                        case "///" => callbacks.saveReplayOnline(true , false) { url => onClick(Nil) }
                        case "###" => callbacks.saveReplayOnline(true , true ) { url => onClick(Nil) }
                        case _ => onClick(Nil)
                    })
                }).copy(clear = false)
            ) ++
            $(ZBasic(Break ~ Break ~ Break, "Notifications".spn, () => { onClick("notifications", self) }).copy(clear = false)).%(_ => self.any || game.isOver).%(_ => false) ++
            $(ZBasic(Break, tip.|(Empty).spn, () => { tip = randomTip() }, ZBasic.infoch).copy(clear = false)).%(_ => callbacks.settings.has(hrf.HideTips).not) ++
            $(ZBasic(Break, "Settings".spn, () => {
                tip = randomTip()
                val old = callbacks.settings.of[FactionPanesOption]
                callbacks.editSettings {
                    if (old != callbacks.settings.of[FactionPanesOption])
                        resize()
                    else
                        updateStatus()
                }
            }).copy(clear = false))
    }

    var shown : $[Notification] = $

    override def showNotifications(self : $[F]) : Unit = {
        val newer = game.notifications
        val older = shown

        shown = game.notifications

        val display = newer.diff(older).%(_.factions.intersect(self).any)./~(n => convertActions(self.single, n.infos)).some./~(_ :+ ZOption(Empty, Break))

        if (display.none)
            return

        overlayPane.vis()

        overlayPane.attach.clear()

        val ol = overlayPane.attach.appendContainer(overlayScrollX(Content), resources, onClick)

        val asker = new NewAsker(ol, img)

        asker.zask(display)(resources)
    }

    override def wait(self : $[F], factions : $[F]) {
        lastActions = $
        lastThen = null

        showNotifications(self)

        super.wait(self, factions)
    }

    var lastActions : $[UserAction] = $
    var lastThen : UserAction => Unit = null

    var keys = $[Key]()

    override def ask(faction : |[F], actions : $[UserAction], then : UserAction => Unit) {
        lastActions = actions
        lastThen = then

        showNotifications(faction.$)

        keys = actions./~(a => a.as[Key] || a.unwrap.as[Key])

        keys ++= actions.of[SoftKeys].%(_.isSoft)./~(game.performContinue(None, _, false).continue match {
            case Ask(f, l) if faction.has(f) => l./~(a => a.as[Key] || a.unwrap.as[Key])
            case _ => $()
        })

        updateStatus()

        lazy val choice = actions./~{
            case _ : Info => None
            case _ : Hidden => None
            case _ : DeadlockAction => None
            case a => Some(a)
        }

        lazy val expand = actions./~{
            case a : HalfExplode => a.expand(None)
            case _ => $
        }./~{
            case _ : Info => None
            case _ : Hidden => None
            case a => Some(a)
        }

        if (choice.num == 1 && actions./(_.unwrap).of[DeadlockAction].any) {
            scalajs.js.timers.setTimeout(0) { then(choice(0)) }
            return
        }

        if (choice.num == 1 && actions./(_.unwrap).of[EndTurnAction].any && (callbacks.settings.has(AutoEndOfTurn))) {
            scalajs.js.timers.setTimeout(0) { then(choice(0)) }
            return
        }

        super.ask(faction, actions, a => {
            clearOverlay()
            keys = $
            then(a)
        })
    }

    override def fixActionOption(e : Elem) : Elem = e @@ {
        case Div(e, l) => Div(fixActionOption(e), l)
        case Span(e, l) => Span(fixActionOption(e), l)
        case Concat(a, b) => Concat(fixActionOption(a), fixActionOption(b))
        case ElemList(l, e) => ElemList(l./(fixActionOption), fixActionOption(e))
        case Image(ImageId(i), s, d) => Image(ImageId(callbacks.settings.has(StarStarports).?(i.replace("starport", "starport-alt")).|(i)), s, d)
        case e => e
    }

    override def styleAction(faction : |[F], actions : $[UserAction], a : UserAction, unavailable : Boolean, view : |[Any]) : $[Style] =
        view @@ {
            case _ if unavailable.not => $()
            case Some(_ : Figure) => $(styles.unquasi)
            case Some(_) => $(xstyles.unavailableCard)
            case _ => $(xstyles.unavailableText)
        } ++
        a @@ {
            case _ if view.any && view.get.is[Resource] => $(styles.card0, styles.circle)
            case _ : Info => $(xstyles.info)
            case _ if unavailable => $(xstyles.info)
            case _ => $(xstyles.choice)
        } ++
        a @@ {
            case _ if view.any && view.get.is[Resource] => $()
            case _ => $(xstyles.xx, xstyles.chp, xstyles.chm)
        } ++
        faction @@ {
            case Some(f : Faction) => $(elem.borders.get(f))
            case _ => $()
        } ++
        a @@ {
            case a : Selectable if a.selected => $(styles.selected)
            case _ => $()
        } ++
        view @@ {
            case Some(_ : Figure)                    => $(styles.inline, styles.quasi) ++
                a @@ {
                    case a : XXSelectObjectAction[_] => $($(), $(styles.selfigure1), $(styles.selfigure2))(a.selecting.count(a.n))
                    case a : XXDeselectObjectAction[_] => $($(), $(styles.selfigure1), $(styles.selfigure2))(a.selecting.count(a.n))
                    case _ => $()
                }
            case Some(_) => $(styles.inline)
            case _ => $(xstyles.thu, xstyles.thumargin, xlo.fullwidth)
        } ++
        a @@ {
            case _ if unavailable => $()
            case _ : Extra[_] => $()
            case _ : Choice | _ : Cancel | _ : Back | _ : OnClickInfo => $(xlo.pointer)
            case _ => $()
        }

}
