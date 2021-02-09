package io.github.adoniasalcantara.anp.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Fuel(
    val type: FuelType,
    val updatedAt: LocalDateTime,
    val price: BigDecimal
)
