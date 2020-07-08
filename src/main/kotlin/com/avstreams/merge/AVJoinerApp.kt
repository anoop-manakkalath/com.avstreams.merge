package com.avstreams.merge

import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

class AVJoinerApp : App(AVScreen::class) {
	
    private val avController: AVController by inject()

    override fun start(stage: Stage) {
        importStylesheet(Styles::class)
        stage.isResizable = false
        super.start(stage)
        avController.init()
    }
}

/** Main function */
fun main(args: Array<String>) {
    Application.launch(AVJoinerApp::class.java, *args)
}
