package com.avstreams.merge

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.util.Duration
import java.io.File
import java.util.*

class AVScreen {

    val root = GridPane().apply {
        styleClass.add("av-screen")
        hgap = 10.0
        vgap = 10.0
        padding = Insets(10.0)
    }

    val video = TextField()
    val audio = TextField()
    val message = TextArea().apply {
        isEditable = false
        prefRowCount = 10
    }

    val multiplexBtn = Button("Multiplex").apply {
        isDefaultButton = true
    }

    private var lastDir: File = File(System.getProperty("user.home"))

    private val videoFilters = listOf(
        FileChooser.ExtensionFilter("WebM Files", "*.webm"),
        FileChooser.ExtensionFilter("MP4 Files", "*.mp4"),
        FileChooser.ExtensionFilter("Theora Files", "*.ogg"),
        FileChooser.ExtensionFilter("OGG Video Files", "*.ogv"),
        FileChooser.ExtensionFilter("Matroska Video Files", "*.mkv")
    )

    private val audioFilters = listOf(
        FileChooser.ExtensionFilter("OPUS Files", "*.opus"),
        FileChooser.ExtensionFilter("AAC Files", "*.aac"),
        FileChooser.ExtensionFilter("M4A Files", "*.m4a"),
        FileChooser.ExtensionFilter("Vorbis Files", "*.ogg"),
        FileChooser.ExtensionFilter("WebM Audio Files", "*.webm"),
        FileChooser.ExtensionFilter("MP4 Audio Files", "*.mp4"),
        FileChooser.ExtensionFilter("OGG Audio Files", "*.oga"),
        FileChooser.ExtensionFilter("Matroska Audio Files", "*.mka")
    )

    init {
        with(root) {
            // ==========================================
            // Row 0: Video File Selection (Label, TextField, Browse Button)
            // ==========================================
            add(Label("Video"), 0, 0)
            add(video, 1, 0)
            add(Button("^").apply {
                setOnAction {
                    val fileChooser = FileChooser().apply {
                        initialDirectory = lastDir
                        extensionFilters.addAll(videoFilters)
                    }
                    val showOpenDialog = fileChooser.showOpenDialog(root.scene?.window)
                    if (Objects.nonNull(showOpenDialog)) {
                        video.text = showOpenDialog.canonicalPath
                        lastDir = File(video.text).parentFile ?: lastDir
                    }
                }
            }, 2, 0)

            // ==========================================
            // Row 1: Audio File Selection (Label, TextField, Browse Button)
            // ==========================================
            add(Label("Audio"), 0, 1)
            add(audio, 1, 1)
            add(Button("^").apply {
                setOnAction {
                    val fileChooser = FileChooser().apply {
                        initialDirectory = lastDir
                        extensionFilters.addAll(audioFilters)
                    }
                    val showOpenDialog = fileChooser.showOpenDialog(root.scene?.window)
                    if (Objects.nonNull(showOpenDialog)) {
                        audio.text = showOpenDialog.canonicalPath
                        lastDir = File(audio.text).parentFile ?: lastDir
                    }
                }
            }, 2, 1)

            // ==========================================
            // Row 2: Action Button (Multiplex)
            // ==========================================
            add(multiplexBtn, 1, 2)

            // ==========================================
            // Row 3: Console Output Area (Label, TextArea)
            // ==========================================
            add(Label("Console"), 0, 3)
            add(message, 1, 3)
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
        val move = 10.0
        val keyframeDuration = Duration.seconds(0.04)

        val stage = root.scene?.window as? javafx.stage.Stage ?: return

        val timelineX = Timeline(KeyFrame(keyframeDuration, {
            if (x == 0) { stage.x += move; x = 1 } else { stage.x -= move; x = 0 }
        })).apply { this.cycleCount = cycleCount; isAutoReverse = false }

        val timelineY = Timeline(KeyFrame(keyframeDuration, {
            if (y == 0) { stage.y += move; y = 1 } else { stage.y -= move; y = 0 }
        })).apply { this.cycleCount = cycleCount; isAutoReverse = false }

        timelineX.play()
        timelineY.play()
    }
}
