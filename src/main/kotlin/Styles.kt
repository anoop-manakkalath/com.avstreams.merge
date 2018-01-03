package no.tornado.fxsample.login

import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val avScreen by cssclass()
    }

    init {
        select(avScreen) {
            padding = box(15.px)
            vgap = 7.px
            hgap = 10.px
        }
    }
}