package com.avstreams.merge

import javafx.application.Platform
import tornadofx.Controller
import tornadofx.FX
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.File
import java.io.IOException

class AVController : Controller() {

    private val avScreen: AVScreen by inject()

    /**
     * Initialize the tool. Checks for the 'ffmpeg' in the (environment) path
     */
    fun init() {
        with (config) {
            if (containsKey(VIDEO) && containsKey(AUDIO)) {
                string(VIDEO)?.let { string(AUDIO)?.let { it1 -> tryMultiplexing(it, it1) } }
            }
            else {
                val cmd = arrayOf("ffmpeg", "-version")
                try {
                    val process = Runtime.getRuntime().exec(cmd)
                    val stdError = BufferedReader(InputStreamReader(process.errorStream))
                    if (stdError.lines().count() > 0) {
                        showMainScreen("[FAIL] An unknown issue with 'ffmpeg'. Please try after reinstalling 'ffmpeg'.")
                        disableMultiplexBtn()
                    }
                    else {
                        showMainScreen("Please select and join streams...")
                    }
                }
                catch (e: IOException) {
                    showMainScreen("[FAIL] You need to have 'ffmpeg' in your 'path'.")
                    disableMultiplexBtn()
                }
            }
        }
    }

    /**
     * Writes the messages to the 'log' of the tool.
     * It calls the shakes function, if any error is there
     */
    private fun showMainScreen(message: String, shake: Boolean = false) {
        if (FX.primaryStage.scene.root != avScreen.root) {
            FX.primaryStage.scene.root = avScreen.root
            FX.primaryStage.sizeToScene()
            FX.primaryStage.centerOnScreen()
        }

        Platform.runLater {
            avScreen.video.requestFocus()
            avScreen.message.text = message
            if (shake) avScreen.shakeStage()
        }
    }

    /**
     * Disables the 'Multiplex' button, if any error is there with 'ffmpeg'
     */
    private fun disableMultiplexBtn(shake: Boolean = true) {
        Platform.runLater {
            avScreen.message.requestFocus()
            avScreen.multiplexBtn.isDisable = true
            if (shake) avScreen.shakeStage()
        }
    }

    /**
     * Video and audio multiplexing using the 'ffmpeg'
     */
    fun tryMultiplexing(video: String, audio: String): Unit {
        runAsync {
            video.trim() != "" && audio.trim() != ""
        } ui { success ->
            if (success) {
                avScreen.clear()
                if (!video.contains(".") && !audio.contains(".")) {
                    avScreen.message.text = "[ FAIL ] Invalid stream"
                } else {
                    val output = video.subSequence(0, video.lastIndexOf(".")).toString() + "_" +
                            video.subSequence(video.lastIndexOf("."), video.length)
                    if (File(output).exists()) {
                        avScreen.message.clear()
                        avScreen.message.text = "[ EX ] The output file is already present.\n"
                    }
                    else {
						avScreen.message.clear()
                        runAsync {
                            avScreen.message.appendText("The selected video file is: $video\n")
                            avScreen.message.appendText("The selected audio file is: $audio\n")
                        }
                        val cmd = arrayOf("ffmpeg", "-i", video, "-i", audio, "-codec", "copy", "-shortest", output)
                        val process = Runtime.getRuntime().exec(cmd)

                        val stdInput = process.inputStream.bufferedReader()
                        val stdError = process.errorStream.bufferedReader()

                        // Read the output from the command
                        runAsync { stdInput.forEachLine { avScreen.message.appendText(it + "\n") } }

                        // Read any errors from the attempted command
                        runAsync { stdError.forEachLine { avScreen.message.appendText(it + "\n") } }

                        if (stdError.lines().count() < 1) {
                            avScreen.message.appendText("The output file is: $output.\n")
                            avScreen.message.appendText("[ OK ] Successfully multiplexed the streams.\n")
                        }

                        stdInput.close()
                        stdError.close()
                    }
                }
            }
            else {
                showMainScreen("[FAIL] Please select streams...", true)
            }
        }
    }

    companion object {
        const val VIDEO = "Video"
        const val AUDIO = "Audio"
    }
}
