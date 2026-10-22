package com.avstreams.merge

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.scene.Scene

class AVJoinerApp : Application() {

    private val avScreen = AVScreen()
    private val avController = AVController(avScreen)

    override fun start(stage: Stage) {

        val scene = Scene(avScreen.root)
        AVJoinerApp::class.java.getResource("/styles.css")?.toExternalForm()?.let { cssUrl ->
            scene.stylesheets.add(cssUrl)
        }

        stage.title = "AV Streams Joiner v2.0.0 [ Join Video & Audio Streams ]"
        stage.scene = scene
        stage.icons.clear()
        AVJoinerApp::class.java.getResourceAsStream("/projector.png")?.let { iconStream ->
            stage.icons.add(Image(iconStream))
        }
        stage.isResizable = false

        stage.show()
        avController.init()

        avScreen.multiplexBtn.setOnAction {
            avController.tryMultiplexing(avScreen.video.text, avScreen.audio.text)
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(AVJoinerApp::class.java, *args)
}
