package com.avstreams.merge

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.addStageIcon
import tornadofx.importStylesheet

class AVJoinerApp : App(AVScreen::class) {
	
    private val avController: AVController by inject()

    override fun start(stage: Stage) {
        importStylesheet(Styles::class)
        stage.icons.clear()
        addStageIcon(Image("/projector.png"))
        stage.isResizable = false
        super.start(stage)
        avController.init()
    }
}

/** Main function */
fun main(args: Array<String>) {
    Application.launch(AVJoinerApp::class.java, *args)
}
