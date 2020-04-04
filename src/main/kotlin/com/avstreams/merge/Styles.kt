package com.avstreams.merge

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
            padding = box(16.px)
            vgap = 8.px
            hgap = 8.px
        }
    }
}
