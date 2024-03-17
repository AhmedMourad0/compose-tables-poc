package dev.ahmedmourad.tables.compose.table

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

fun Int.toDp(density: Density) = with(density) { this@toDp.toDp() }

fun Dp.toPx(density: Density) = with(density) { this@toPx.toPx() }
