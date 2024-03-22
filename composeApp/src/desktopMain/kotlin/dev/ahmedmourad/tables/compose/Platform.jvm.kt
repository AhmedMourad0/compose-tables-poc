package dev.ahmedmourad.tables.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun BoxScope.VerticalScrollbar(state: LazyListState, modifier: Modifier) {
    androidx.compose.foundation.VerticalScrollbar(
        adapter = rememberScrollbarAdapter(state),
        modifier = modifier.align(Alignment.CenterEnd).fillMaxHeight()
    )
}
