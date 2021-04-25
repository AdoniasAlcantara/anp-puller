package io.github.adoniasalcantara.anp.model

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val key: String,
    val company: String,
    val address: String,
    val neighborhood: String,
    val city: String,
    val brand: String,
    val fuels: Map<FuelType, Fuel>
)
