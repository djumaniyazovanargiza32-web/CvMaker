package com.nargiza.cvmaker.backend.config

import java.io.File
import java.util.Properties

object AppConfig {
    val port: Int = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val groqModel: String
    val groqApiKey: String

    init {
        val props = Properties()
        val localPropsFile = File("local.properties").takeIf { it.exists() }
            ?: File("../local.properties").takeIf { it.exists() }

        if (localPropsFile != null) {
            localPropsFile.inputStream().use { props.load(it) }
        }

        groqApiKey = System.getenv("GROQ_API_KEY")
            ?: props.getProperty("GROQ_API_KEY")
            ?: ""

        groqModel = System.getenv("GROQ_MODEL")
            ?: props.getProperty("GROQ_MODEL")
            ?: "openai/gpt-oss-120b"
    }
}
