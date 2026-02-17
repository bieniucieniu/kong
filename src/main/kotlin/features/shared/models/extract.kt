package com.bieniucieniu.features.shared.models

import ai.koog.prompt.llm.LLModel
import kotlin.reflect.full.memberProperties

fun extractModels(vararg models: Any) =
    models.flatMap { m ->
        m::class.memberProperties
            .mapNotNull {
                try {
                    it.getter.call(m) as? LLModel
                } catch (_: Throwable) {
                    null
                }
            }

    }

