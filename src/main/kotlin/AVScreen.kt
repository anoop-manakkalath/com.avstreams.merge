package no.tornado.fxsample.login

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.util.Duration
import no.tornado.fxsample.login.Styles.Companion.avScreen
import tornadofx.*

class AVScreen : View() {
    override val root = GridPane()
    private val avController: AVController by inject()

    var video: TextField by singleAssign()
    var audio: TextField by singleAssign()
    var message: TextArea by singleAssign()

    init {
        title = "Join Video & Audio Streams"

        with (root) {
            addClass(avScreen)

            row("Video") {
                video = textfield()
                button("^") {
                    setOnAction {
                        val fileChooser = FileChooser()
                        video.text = fileChooser.showOpenDialog(null).absolutePath
                    }
                }
            }

            row("Audio") {
                audio = textfield()
                button("^") {
                    setOnAction {
                        val fileChooser = FileChooser()
                        audio.text = fileChooser.showOpenDialog(null).absolutePath
                    }

                }
            }

            row {
                button("Multiplex") {
                    isDefaultButton = true

                    setOnAction {
                        avController.tryMultiplexing(
                                video.text,
                                audio.text
                        )
                    }
                }
            }

            row("Log") {
                message = textarea()
            }

        }
    }

    fun clear() {
        video.clear()
        audio.clear()
    }

    fun shakeStage() {
        var x = 0
        var y = 0
        val cycleCount = 10
        val move = 10
        val keyframeDuration = Duration.seconds(0.04)

        val stage = FX.primaryStage

        val timelineX = Timeline(KeyFrame(keyframeDuration, EventHandler {
            if (x == 0) {
                stage.x = stage.x + move
                x = 1
            } else {
                stage.x = stage.x - move
                x = 0
            }
        }))

        timelineX.cycleCount = cycleCount
        timelineX.isAutoReverse = false

        val timelineY = Timeline(KeyFrame(keyframeDuration, EventHandler {
            if (y == 0) {
                stage.y = stage.y + move
                y = 1
            } else {
                stage.y = stage.y - move
                y = 0
            }
        }))

        timelineY.cycleCount = cycleCount
        timelineY.isAutoReverse = false

        timelineX.play()
        timelineY.play()
    }
}
