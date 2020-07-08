package com.avstreams.merge

import javafx.application.Platform
import tornadofx.Controller
import tornadofx.FX
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.IOException

class AVController : Controller() {

    private val avScreen: AVScreen by inject()

    /**
     * Initialize the tool. Checks for the 'ffmpeg' in the (environment) path
     */
    fun init() {
        with (config) {
            if (containsKey(video) && containsKey(audio)) {
                string(video)?.let { string(audio)?.let { it1 -> tryMultiplexing(it, it1) } }
            }
            else {
                val cmd = "ffmpeg -version"
                try {
                    val process = Runtime.getRuntime().exec(cmd)
                    val stdError = BufferedReader(InputStreamReader(process.errorStream))
                    if (stdError.lines().count() > 0) {
                        showMainScreen("Error! An unknown issue with 'ffmpeg'. Please try after reinstalling 'ffmpeg")
                    }
                    else {
                        showMainScreen("Please select and join streams")
                    }
                }
                catch (e: IOException) {
                    showMainScreen("Error! You need to have 'ffmpeg' in your 'path'")
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
     * Video and audio multiplexing using the 'ffmpeg'
     */
    fun tryMultiplexing(video: String, audio: String) {
        runAsync {
            video.trim() != "" && audio.trim() != ""
        } ui { success ->
            if (success) {
                avScreen.clear()
                val output = video.subSequence(0, video.lastIndexOf(".")).toString() + "_" +
                    video.subSequence(video.lastIndexOf("."), video.length)
                val cmd = "ffmpeg -i \"${video}\" -i \"${audio}\" -codec copy -shortest \"${output}\""
                val process = Runtime.getRuntime().exec(cmd)

                val stdInput = BufferedReader(InputStreamReader(process.inputStream))
                val stdError = BufferedReader(InputStreamReader(process.errorStream))

                // read the output from the command
                stdInput.lines().forEach { line -> avScreen.message.appendText(line + "\n") }
                // read any errors from the attempted command
                stdError.lines().forEach { line -> avScreen.message.appendText(line + "\n") }

                if (stdError.lines().count() <= 0) {
                    avScreen.message.appendText("[ OK ] Successfully multiplexed the streams.\n")
                }
                stdInput.close()
                stdError.close()
            }
            else {
                showMainScreen("[FAIL] Please select streams", true)
            }
        }
    }

    companion object {
        const val video = "Video"
        const val audio = "Audio"
    }
}
