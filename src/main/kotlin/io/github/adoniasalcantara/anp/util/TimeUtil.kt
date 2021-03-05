package io.github.adoniasalcantara.anp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")

fun timestamp(): String = LocalDateTime.now().format(formatter)