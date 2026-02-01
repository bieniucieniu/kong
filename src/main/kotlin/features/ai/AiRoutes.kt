package com.bieniucieniu.features.ai

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.streaming.StreamFrame
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Prompt(val message: String)

fun Route.aiRoutes() {
    route("/ai") {
        post("chat") {
            val p = call.receive<Prompt>()
            val f = llm().executeStreaming(
                prompt = prompt("chat") {
                    system("You are a helpful assistant that clarifies questions with as long as possible answer")
                    user(p.message)
                },
                model = GoogleModels.Gemini2_5Flash
            )

            call.respondOutputStream(contentType = ContentType.Text.EventStream) {
                f.collect { chunk ->
                    val str = when (chunk) {
                        is StreamFrame.Append ->
                            ("append: ${chunk.text}\n")

                        is StreamFrame.End ->
                            ("end: $chunk\n\n")


                        is StreamFrame.ToolCall ->
                            ("tool call: $chunk")

                    }
                    println(str)
                    write(str.toByteArray())
                    flush()
                }
            }
        }
    }
}