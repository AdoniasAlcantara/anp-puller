package io.github.adoniasalcantara.anp.model

import kotlinx.serialization.Serializable

@Serializable
data class Fuel(
    val type: FuelType,
    val updatedAt: String,
    val price: Float
)
