package io.github.adoniasalcantara.anp.model

import java.math.BigDecimal
import java.time.LocalDate

data class Fuel(
    val type: FuelType,
    val updatedAt: LocalDate,
    val price: BigDecimal
)
