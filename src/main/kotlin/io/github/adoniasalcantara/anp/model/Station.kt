package io.github.adoniasalcantara.anp.model

data class Station(
    val key: String,
    val company: String,
    val address: String,
    val neighborhood: String,
    val city: String,
    val brand: String,
    val fuels: List<Fuel>
)
