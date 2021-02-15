package io.github.adoniasalcantara.anp.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val code: Int,
    val name: String
)