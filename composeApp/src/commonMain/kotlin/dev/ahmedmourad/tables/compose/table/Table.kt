package dev.ahmedmourad.tables.compose.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.ahmedmourad.tables.compose.RowStyle
import dev.ahmedmourad.tables.compose.TableColumn
import dev.ahmedmourad.tables.compose.TableDefaults
import dev.ahmedmourad.tables.compose.TableScope
import kotlin.math.roundToInt

//class TablsState<T>(content: TableScope<T>.() -> Unit) {
//    var provider by mutableStateOf(TableScope<T>().apply(content))
//    val widths = mutableStateListOf()
//}

@Composable
fun <T> Table(
    items: List<T>, //TODO: add to compose stable list
    modifier: Modifier = Modifier,
    rowStyle: (index: Int, T) -> RowStyle = { _, _ -> TableDefaults.RowStyle },
    content: TableScope<T>.() -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val rowStyle by rememberUpdatedState(rowStyle)
    @Suppress("NAME_SHADOWING")
    val content by rememberUpdatedState(content)
//    var provider by remember { mutableStateOf(TableScope<T>().apply(content)) }
    val provider by derivedStateOf(referentialEqualityPolicy()) {
        TableScope<T>().apply(content)
    }
//    LaunchedEffect(content) {
//        provider = TableScope<T>().apply(content)
//    }
    val dividersWidth = provider.columns.size.minus(1).times(9.dp.toPx(LocalDensity.current))
    val tableShape = RoundedCornerShape(8.dp)
    BoxWithConstraints(modifier.fillMaxWidth()
        .wrapContentHeight()
        .clip(tableShape)
        .background(Color.White, tableShape)
        .border(1.dp, Color.LightGray, tableShape)) {
        Column(Modifier.fillMaxWidth()) {
            val constraints = this@BoxWithConstraints.constraints
            var prevConstraints by remember { mutableStateOf(constraints) }
            val widthOfColumns = remember(provider.columns) {
                val singleWidth = constraints.maxWidth.minus(dividersWidth).div(provider.columns.size)
                mutableStateListOf(*List(provider.columns.size) { singleWidth / constraints.maxWidth }.toTypedArray())
            }
//            LaunchedEffect(constraints.maxWidth) {
//                val scale = constraints.maxWidth.minus(dividersWidth) / prevConstraints.maxWidth.minus(dividersWidth)
//                widthOfColumns.indices.forEach { index ->
//                    widthOfColumns[index] = widthOfColumns[index].times(scale).roundToInt()
//                }
//                prevConstraints = constraints
//            }
            TableHeader(
                provider = provider,
                getColumnWidth = { index -> widthOfColumns[index] },
                setColumnWidth = { index, width ->
                    widthOfColumns[index] += width / constraints.maxWidth.minus(dividersWidth)
                    widthOfColumns[index + 1] -= width / constraints.maxWidth.minus(dividersWidth)
                }
            )
            HorizontalDivider()
            items.forEachIndexed { rowIndex, item ->
                key(rowIndex) {
                    TableRow(
                        provider = provider,
                        getColumnWidth = { index -> widthOfColumns[index] },
                        setColumnWidth = { index, width ->
                            widthOfColumns[index] += width / constraints.maxWidth.minus(dividersWidth)
                            widthOfColumns[index + 1] -= width / constraints.maxWidth.minus(dividersWidth)
                        }
                    ) { _, cell ->
                        cell.content(item)
                    }
                    if (rowIndex != items.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> TableHeader(
    provider: TableScope<T>,
    getColumnWidth: (index: Int) -> Float,
    setColumnWidth: (index: Int, delta: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val provider by rememberUpdatedState(provider)
    val getColumnWidth by rememberUpdatedState(getColumnWidth)
    val setColumnWidth by rememberUpdatedState(setColumnWidth)
    TableRow(
        provider = provider,
        getColumnWidth = getColumnWidth,
        setColumnWidth = setColumnWidth,
        modifier = modifier
    ) { _, cell ->
        Text(cell.name)
    }
}

@Composable
private fun <T> TableRow(
    provider: TableScope<T>,
    getColumnWidth: (index: Int) -> Float,
    setColumnWidth: (index: Int, delta: Float) -> Unit,
    modifier: Modifier = Modifier,
    cellContent: @Composable (cellIndex: Int, cell: TableColumn<T>) -> Unit
) {
    val provider by rememberUpdatedState(provider)
    val getColumnWidth by rememberUpdatedState(getColumnWidth)
    val setColumnWidth by rememberUpdatedState(setColumnWidth)
    val cellContent by rememberUpdatedState(cellContent)
    Row(modifier.height(IntrinsicSize.Max)) {
        provider.columns.forEachIndexed { cellIndex, cell ->
            key(cellIndex) {
                Box(Modifier.fillMaxWidth(getColumnWidth(cellIndex))) {
                    cellContent(cellIndex, cell)
                }
                if (cellIndex != provider.columns.lastIndex) {
                    VerticalDivider(Modifier.draggable(state = rememberDraggableState {
                        val delta = it
                        setColumnWidth(cellIndex, delta)
//                        setColumnWidth(cellIndex + 1, getColumnWidth(cellIndex + 1) - delta)
                    }, orientation = Orientation.Horizontal))
                }
            }
        }
    }
}

@Composable
private fun HorizontalDivider(modifier: Modifier = Modifier) {
    Spacer(modifier.fillMaxWidth().height(1.dp).background(Color.LightGray))
}

@Composable
private fun VerticalDivider(modifier: Modifier = Modifier) {
    Spacer(modifier.fillMaxHeight()
        .pointerHoverIcon(PointerIcon.Crosshair)
        .padding(horizontal = 4.dp)
        .width(1.dp)
        .background(Color.LightGray)
    )
}
