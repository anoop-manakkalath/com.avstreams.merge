package no.tornado.fxsample.login

import javafx.application.Platform
import tornadofx.Controller
import tornadofx.FX
import java.util.concurrent.TimeUnit
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.InputStreamReader
import java.io.BufferedReader

class AVController : Controller() {
    val avScreen: AVScreen by inject()

    fun init() {
        with (config) {
            if (containsKey(video) && containsKey(audio)) {
                tryMultiplexing(string(video), string(audio))
            } else {
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
            if (shake) avScreen.shakeStage()
        }
    }

    fun tryMultiplexing(video: String, audio: String) {
        runAsync {
            video.trim() != "" && audio.trim() != ""
        } ui { success ->
            if (success) {
                avScreen.clear()
                val buffer = StringBuilder();
                val output = video.subSequence(0, video.lastIndexOf(".")).toString() + "_" +
                    video.subSequence(video.lastIndexOf("."), video.length)
                val cmd = "ffmpeg -i \"${video}\" -i \"${audio}\" -codec copy -shortest \"${output}\""
                val process = Runtime.getRuntime().exec(cmd)

                val stdInput = BufferedReader(InputStreamReader(process.getInputStream()))
                val stdError = BufferedReader(InputStreamReader(process.getErrorStream()))

                // read the output from the command
                stdInput.lines().forEach { line ->  buffer.append(line).append("\n") }
                // read any errors from the attempted command
                stdError.lines().forEach { line ->  buffer.append(line).append("\n")}

                val errFlag  = stdError.lines().count() > 0;
                if (!errFlag) {
                    buffer.append("[  OK  ] Successfully multiplexed the streams.\n")
                }
                avScreen.message.text = buffer.toString()
                stdInput.close()
                stdError.close()
            } else {
                showMainScreen("[FAIL] Please select streams", true)
            }
        }
    }

    companion object {
        val video = "Video"
        val audio = "Audio"
    }

}