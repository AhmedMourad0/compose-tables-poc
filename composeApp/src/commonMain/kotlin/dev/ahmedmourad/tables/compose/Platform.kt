package dev.ahmedmourad.tables.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BoxScope.VerticalScrollbar(state: LazyListState, modifier: Modifier = Modifier)
