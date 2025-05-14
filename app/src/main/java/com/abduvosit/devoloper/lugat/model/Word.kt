package com.abduvosit.devoloper.lugat.model

import java.io.Serializable

data class Word(
    val id: Int = 0,
    val english: String,
    val uzbek: String,
    val memorized: Boolean,
    val example: String = "yoq",
    val partOfSpeech: String = "yoq",
    ):Serializable
