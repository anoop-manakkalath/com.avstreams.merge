package com.avstreams.merge

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.util.Duration
import com.avstreams.merge.Styles.Companion.avScreen
import tornadofx.*
import java.io.File

class AVScreen : View() {

    override val root = GridPane()
    private val avController: AVController by inject()

    var video: TextField by singleAssign()
    var audio: TextField by singleAssign()
    var message: TextArea by singleAssign()

    private var lastDir: File = File(System.getProperty("user.home"))

    private val videoFilters = listOf(FileChooser.ExtensionFilter("WebM Files", "*.webm"),
            FileChooser.ExtensionFilter("MP4 Files", "*.mp4"), FileChooser.ExtensionFilter("Theora Files", "*.ogg"),
            FileChooser.ExtensionFilter("OGG Video Files", "*.ogv"), FileChooser.ExtensionFilter("Matroska Video Files", "*.mkv"))

    private val audioFilters = listOf(FileChooser.ExtensionFilter("OPUS Files", "*.opus"),
            FileChooser.ExtensionFilter("AAC Files", "*.aac"), FileChooser.ExtensionFilter("M4A Files", "*.m4a"),
            FileChooser.ExtensionFilter("Vorbis Files", "*.ogg"), FileChooser.ExtensionFilter("WebM Audio Files", "*.webm"),
            FileChooser.ExtensionFilter("MP4 Audio Files", "*.mp4"), FileChooser.ExtensionFilter("OGG Audio Files", "*.oga"),
            FileChooser.ExtensionFilter("Matroska Audio Files", "*.mka"))

    init {
        title = "AV Streams Joiner v1.1.2 [ Join Video & Audio Streams ]"

        with (root) {
            addClass(avScreen)

            row("Video") {
                video = textfield()
                button("^") {
                    setOnAction {
                        val fileChooser = FileChooser()
                        fileChooser.initialDirectory = lastDir
                        fileChooser.extensionFilters.addAll(videoFilters)
                        val showOpenDialog = fileChooser.showOpenDialog(null)
                        if (showOpenDialog != null) {
                            video.text = showOpenDialog.absolutePath
                            lastDir = File(video.text).parentFile
                        }
                    }
                }
            }

            row("Audio") {
                audio = textfield()
                button("^") {
                    setOnAction {
                        val fileChooser = FileChooser()
                        fileChooser.initialDirectory = lastDir
                        fileChooser.extensionFilters.addAll(audioFilters)
                        val showOpenDialog = fileChooser.showOpenDialog(null)
                        if (showOpenDialog != null) audio.text = showOpenDialog.absolutePath
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

    /**
     * Shakes the tool UI. This is usually called in case of any errors.
     */
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
            }
            else {
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
            }
            else {
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
