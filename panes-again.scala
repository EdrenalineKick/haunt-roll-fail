package hrf.ui.again
//
//
//
//
import logger._, colmat._
//
//
//
//

import scala.collection.mutable

case class FitResult(layout : Layout, panes : List[Fit], fontSize : Double, scaleCheck : Double, score : Double)

case class Layouter(layouts : List[Layout], process : (List[Fit] => List[Fit])*) {
    def get(width : Int, height : Int) = {
        val c = cached.getOrElseUpdate((width, height), generate(width, height))
        c.copy(panes = process.foldLeft(c.panes)((f, p) => p(f)))
    }

    private val cached = mutable.Map[(Int, Int), FitResult]()
    
    private def generate(width : Int, height : Int) : FitResult = {
        class Run(val layout : Layout, var lo : Int, var hi : Int, var scaleCheck : Int, var fit : List[Fit], var score : Double)
    
        var runs = layouts./(l => new Run(l, 24, 280, 0, Nil, 0))
        var best : List[Run] = Nil

        def fs(i : Int) = pow(65 / 64.0, i)
        def sc(i : Int) = pow(17 / 16.0, i)
        
        while (runs.any) {
            var run = runs.maxBy(_.hi)
            
            val mid = (run.hi + run.lo) / 2

            if (mid > run.lo) {
                val fit = Fitter.fit(width, height, run.layout.panes./(_.pane(width, height, fs(mid))))
                    
                if (fit.none) {
                    run.hi = mid
                    if (fs(run.hi - 1) * run.layout.boost < best./(_.score).maxOr(0))
                        runs :-= run
                }
                else {
                    run.lo = mid
                }
            }
            else {
                runs :-= run

                val panes = run.layout.panes./(_.pane(width, height, fs(run.lo)))
                var i = 0
                
                while (run.fit.none) {
                    val fit = Fitter.fit(width, height, panes, sc(i))

                    if (fit.any) {
                        run.fit = fit.get
                        run.scaleCheck = i
                    }
                    else {
                        i += 1
                    }
                }
                
                run.score = fs(run.lo) / sc(run.scaleCheck) * run.layout.boost
            
                Some(run)./(r => 
                    println("LAYOUT: " + r.layout.name.padTo(32, ' ') + "   FS: " + fs(r.lo).formatted("%5.4f") + "   SC: " + sc(r.scaleCheck).formatted("%5.4f") + "   R: " + r.score.formatted("%5.4f"))
                )
                
                if (run.score >= best./(_.score).maxOr(0)) {
                    if (run.score > best./(_.score).maxOr(0)) {
                        best = Nil
                        runs = runs.%(r => fs(r.hi - 1) * r.layout.boost >= run.score)
                    }
                    best :+= run
                }
            }
        }
        
        best.foreach { r => 
            println("BEST: " + r.layout.name.padTo(32, ' ') + "   FS: " + fs(r.lo).formatted("%5.4f") + "   SC: " + sc(r.scaleCheck).formatted("%5.4f") + "   R: " + r.score.formatted("%5.4f"))
        }

        val r = best(0)

        FitResult(r.layout, r.fit, fs(r.lo), sc(r.scaleCheck), r.score)
    }
}



case class Layout(name : String, panes : List[UIPane], boost : Double = 1.0) {
    def fontSize(width : Int, height : Int) = {
        val b = 65 / 64.0
        
        var lo = 24
        var hi = 280

        var los = 0
        var his = Int.MaxValue
        
        while (hi > lo + 1) {
            val mid = (hi + lo) / 2
          
            val l = panes./(_.pane(width, height, pow(b, mid)))
            val s = l./(p => p.width * p.height).sum
            
            if (s == los) {
                lo = mid
            }
            else
            if (s == his) {
                hi = mid
            }
            else
            if (Fitter.fit(width, height, l).none) {
                hi = mid
                his = s
            }
            else {
                lo = mid
                los = s
            }
        }
        
        pow(b, lo)
    }
    
    def scaleCheck(width : Int, height : Int, fontSize : Double) = {
        0.to(1024).view./(pow(17/16.0, _)).find(k => Fitter.fit(width, height, panes./(_.pane(width, height, fontSize)), k).any).|(Double.PositiveInfinity)
    }

    def fit(width : Int, height : Int, fontSize : Double, maxScaleCheck : Double) = {
        Fitter.fit(width, height, panes./(_.pane(width, height, fontSize)), maxScaleCheck)
    }
}

trait UIPane {
    def name : String
    def fontSize(width : Int, height : Int) : Double
    def pane(width : Int, height : Int, fontSize : Double) : Pane
}

case class FullDimPane(name : String, kX : Double, kY : Double, pr : Priorities = Priorities()) extends UIPane {
    def fontSize(width : Int, height : Int) = min(width / kX, height / kY)
    def pane(width : Int, height : Int, fontSize : Double) = {
        val wfh = height / kY * kX
        val hfw = width / kX * kY
 
        if (wfh < width)
            Pane(name, wfh.round.toInt, height, pr)
        else
            Pane(name, width, hfw.round.toInt, pr)
    }
}

case class BasicPane(name : String, kX : Double, kY : Double, pr : Priorities = Priorities()) extends UIPane {
    def fontSize(width : Int, height : Int) = min(width / kX, height / kY)
    def pane(width : Int, height : Int, fontSize : Double) = Pane(name, (fontSize * kX).ceil.toInt, (fontSize * kY).ceil.toInt, pr)
}

case class Priorities(grow : Int = 0, order : Int = 0, hor : Int = 0, ver : Int = 0, left : Int = 0, top : Int = 0, right : Int = 0, bottom : Int = 0, maxXscale : Double = Double.PositiveInfinity, maxYscale : Double = Double.PositiveInfinity)

case class Pane(name : String, width : Int, height : Int, pr : Priorities = Priorities()) {
    override def toString = "[" + name + " " + width + "x" + height + "]"
}

case class Fit(name : String, x : Int, y : Int, width : Int, height : Int) {
    def translate(dx : Int, dy : Int) = Fit(name, x + dx, y + dy, width, height)
    def right = x + width
    def bottom = y + height
}


object Fitter {
    def compare(c : Pane => Int)(a : List[Pane], b : List[Pane]) : Int = {
        if (a.num == 1 && b.num == 1)
            return c(a(0)).compare(c(b(0)))
            
        var aa = a
        var bb = b

        while (aa.any && bb.any) {
            val pa = aa./(c).max
            val pb = bb./(c).max

            if (pa < pb)
                return -1

            if (pa > pb)
                return 1

            aa = aa.%(c(_) != pa)    
            bb = bb.%(c(_) != pb)
        }

        if (aa.any)
            return -1

        if (bb.any)
            return 1

        return 0
    }

    def fitMinWidth(width : Int, height : Int, panes : List[Pane]) : Option[Int] = {
        if (panes.none)
            return None

        if (panes.num == 1)
            return (panes(0).width <= width && panes(0).height <= height).?(panes(0).width)
            
        if (fit(width, height, panes).none)
            return None

        var hi = width
        var lo = panes./(_.width).max - 1

        while (hi > lo + 1) {
            val mid = (hi + lo) / 2
            if (fit(mid, height, panes).any)
                hi = mid
            else
                lo = mid
        }

        Some(hi)
    }

    def fitMinHeight(width : Int, height : Int, panes : List[Pane]) : Option[Int] = {
        if (panes.none)
            return None

        if (panes.num == 1)
            return (panes(0).width <= width && panes(0).height <= height).?(panes(0).height)
            
        if (fit(width, height, panes).none)
            return None

        var hi = height
        var lo = panes./(_.height).max - 1

        while (hi > lo + 1) {
            val mid = (hi + lo) / 2
            if (fit(width, mid, panes).any)
                hi = mid
            else
                lo = mid
        }

        Some(hi)
    }
 
    var nnn = 0

    def fit(width : Int, height : Int, panes : List[Pane], maxScaleCheck : Double = Double.PositiveInfinity) : Option[List[Fit]] = {
        if (panes.none)
            return None

        if (width < panes./(p => p.width).max)
            return None

        if (height < panes./(p => p.height).max)
            return None

        if (width * height < panes./(p => p.width * p.height).sum)
            return None

        if (panes.num == 1) {
            val p = panes(0)

            if (width > p.width * p.pr.maxXscale * maxScaleCheck)
                return None
                
            if (height > p.height * p.pr.maxYscale * maxScaleCheck)
                return None

            if (hrf.HRF.flag("shrink"))
                return Some($(Fit(p.name, (width - p.width) / 2, (height - p.height) / 2, p.width, p.height)))
            else
                return Some($(Fit(p.name, 0, 0, width, height)))
        }
     
        val ppp = 1.to(panes.num - 1)./~(panes.combinations).sortBy(pp => pp./(_.pr.order).max - panes.diff(pp)./(_.pr.order).min)
        
        ppp.foreach { pp =>
            val qq = panes.diff(pp)
          
            if (compare(_.pr.left)(pp, qq) >= 0 && compare(_.pr.right)(pp, qq) <= 0) {
                val pwidth = pp./(_.width).max
                val qwidth = qq./(_.width).max

                if (pwidth + qwidth <= width) {
                    val pmino = fitMinWidth(width - qwidth, height, pp)
                    val qmino = fitMinWidth(width - pwidth, height, qq)
                    
                    if (pmino.any && qmino.any) {
                        val pmin = pmino.get
                        val qmin = qmino.get
                        
                        if (pmin + qmin <= width) {
                            val mid = compare(_.pr.grow)(pp, qq) match {
                                case 0 => pmin + (width - qmin - pmin) / 2
                                case -1 => pmin
                                case 1 => width - qmin
                            }
                            
                            val pr = fit(mid, height, pp, maxScaleCheck)
                            val qr = fit(width - mid, height, qq, maxScaleCheck)./(_./(_.translate(mid, 0)))
                            var r = pr./~(pr => qr./(qr => pr ++ qr))
                            
                            if (r.any)
                                return r
                        }
                    }
                }
            }
        
            if (compare(_.pr.top)(pp, qq) >= 0 && compare(_.pr.bottom)(pp, qq) <= 0) {
                val pheight = pp./(_.height).max
                val qheight = qq./(_.height).max

                if (pheight + qheight <= height) {
                    val pmino = fitMinHeight(width, height - qheight, pp)
                    val qmino = fitMinHeight(width, height - pheight, qq)

                    if (pmino.any && qmino.any) {
                        val pmin = pmino.get
                        val qmin = qmino.get
                        
                        if (pmin + qmin <= height) {
                            val mid = compare(_.pr.grow)(pp, qq) match {
                                case 0 => pmin + (height - qmin - pmin) / 2
                                case -1 => pmin
                                case 1 => height - qmin
                            }
                            
                            val pr = fit(width, mid, pp, maxScaleCheck)
                            val qr = fit(width, height - mid, qq, maxScaleCheck)./(_./(_.translate(0, mid)))
                            var r = pr./~(pr => qr./(qr => pr ++ qr))
                            
                            if (r.any)
                                return r
                        }
                    }
                }
            }
        }

        return None
    }
}
