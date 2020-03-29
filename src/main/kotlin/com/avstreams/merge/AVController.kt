package com.avstreams.merge

import javafx.application.Platform
import tornadofx.Controller
import tornadofx.FX
import java.io.InputStreamReader
import java.io.BufferedReader

class AVController : Controller() {
    private val avScreen: AVScreen by inject()

    fun init() {
        with (config) {
            if (containsKey(video) && containsKey(audio)) {
                string(video)?.let { string(audio)?.let { it1 -> tryMultiplexing(it, it1) } }
            }
            else {
                showMainScreen("Please Join Streams")
            }
        }
    }

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

    fun tryMultiplexing(video: String, audio: String) {
        runAsync {
            video.trim() != "" && audio.trim() != ""
        } ui { success ->
            if (success) {
                avScreen.clear()
                val buffer = StringBuilder()
                val output = video.subSequence(0, video.lastIndexOf(".")).toString() + "_" +
                    video.subSequence(video.lastIndexOf("."), video.length)
                val cmd = "ffmpeg -i \"${video}\" -i \"${audio}\" -codec copy -shortest \"${output}\""
                val process = Runtime.getRuntime().exec(cmd)

                val stdInput = BufferedReader(InputStreamReader(process.inputStream))
                val stdError = BufferedReader(InputStreamReader(process.errorStream))

                // read the output from the command
                stdInput.lines().forEach { line ->  buffer.append(line).append("\n") }
                // read any errors from the attempted command
                stdError.lines().forEach { line ->  buffer.append(line).append("\n")}

                val errFlag  = stdError.lines().count() > 0
                if (!errFlag) {
                    buffer.append("[  OK  ] Successfully multiplexed the streams.\n")
                }
                avScreen.message.text = buffer.toString()
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