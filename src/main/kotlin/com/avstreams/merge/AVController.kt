package com.avstreams.merge

import javafx.application.Platform
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class AVController(private val avScreen: AVScreen) {

    /**
     * Initialize the tool. Checks for 'ffmpeg' in the environment PATH.
     */
    fun init() {
        val cmd = arrayOf("ffmpeg", "-version")
        try {
            val process = Runtime.getRuntime().exec(cmd)
            val stdError = BufferedReader(InputStreamReader(process.errorStream))

            // Check if ffmpeg outputs an error or fails to run
            if (stdError.lines().count() > 0 && process.waitFor() != 0) {
                showMainScreen("[FAIL] An unknown issue with 'ffmpeg'. Please try after reinstalling 'ffmpeg'.")
                disableMultiplexBtn()
            } else {
                showMainScreen("Please select and join streams...")
            }
        } catch (_: IOException) {
            showMainScreen("[FAIL] You need to have 'ffmpeg' in your 'path'.")
            disableMultiplexBtn()
        }
    }

    /**
     * Writes messages to the log area and optionally triggers a shake effect.
     */
    private fun showMainScreen(message: String, shake: Boolean = false) {
        Platform.runLater {
            val stage = avScreen.root.scene?.window as? javafx.stage.Stage
            stage?.let {
                it.sizeToScene()
                it.centerOnScreen()
            }
            avScreen.video.requestFocus()
            avScreen.message.text = message
            if (shake) avScreen.shakeStage()
        }
    }

    /**
     * Disables the 'Multiplex' button if an error occurs with 'ffmpeg'.
     */
    private fun disableMultiplexBtn(shake: Boolean = true) {
        Platform.runLater {
            avScreen.message.requestFocus()
            avScreen.multiplexBtn.isDisable = true
            if (shake) avScreen.shakeStage()
        }
    }

    /**
     * Video and audio multiplexing using 'ffmpeg'.
     */
    fun tryMultiplexing(video: String, audio: String) {

        val isValid = video.trim().isNotEmpty() && audio.trim().isNotEmpty()

        if (!isValid) {
            showMainScreen("[FAIL] Please select streams...", true)
            return
        }

        avScreen.clear()

        if (!video.contains(".") && !audio.contains(".")) {
            avScreen.message.text = "[ FAIL ] Invalid stream"
            return
        }

        val output = video.substring(0, video.lastIndexOf('.')) + "_" + video.substring(video.lastIndexOf('.'))

        if (File(output).exists()) {
            avScreen.message.clear()
            avScreen.message.text = "[ EX ] The output file is already present.\n"
            return
        }

        avScreen.message.clear()
        avScreen.message.appendText("The selected video file is: $video\n")
        avScreen.message.appendText("The selected audio file is: $audio\n")

        // Run ffmpeg on a background thread to keep the UI responsive
        Thread.ofVirtual().start {
            try {
                val cmd = arrayOf("ffmpeg", "-i", video, "-i", audio, "-codec", "copy", "-shortest", output)
                // ProcessBuilder with merged streams avoids deadlocks on separate input/error streams
                val process = ProcessBuilder(*cmd).redirectErrorStream(true).start()

                process.inputStream.bufferedReader().use { reader ->
                    reader.forEachLine { line ->
                        Platform.runLater {
                            avScreen.message.appendText(line + "\n")
                        }
                    }
                }

                val exitCode = process.waitFor()
                Platform.runLater {
                    if (exitCode == 0) {
                        avScreen.message.appendText("The output file is: $output.\n")
                        avScreen.message.appendText("[ OK ] Successfully multiplexed the streams.\n")
                    } else {
                        avScreen.message.appendText("[ FAIL ] Multiplexing failed with exit code $exitCode.\n")
                    }
                }
            } catch (e: IOException) {
                Platform.runLater {
                    avScreen.message.appendText("[ FAIL ] Error executing ffmpeg: ${e.message}\n")
                }
            }
        }
    }
}
