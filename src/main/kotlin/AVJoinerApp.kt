package no.tornado.fxsample.login

import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

class JoinerApp : App(AVScreen::class) {
    private val avController: AVController by inject()

    override fun start(stage: Stage) {
        importStylesheet(Styles::class)
        stage.isResizable = false
        super.start(stage)
        avController.init()
    }
}

fun main(args: Array<String>) {
    Application.launch(JoinerApp::class.java, *args)
}