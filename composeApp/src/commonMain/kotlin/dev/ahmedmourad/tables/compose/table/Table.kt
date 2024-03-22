package dev.ahmedmourad.tables.compose.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ahmedmourad.tables.compose.*

@Composable
fun <T> Table(
    itemsProvider: () -> List<T>, //TODO: add to compose stable list
    modifier: Modifier = Modifier,
    headerStyle: RowStyle = TableDefaults.HeaderStyle,
    rowStyleProvider: (index: Int, item: T) -> RowStyle = { _, _ -> TableDefaults.RowStyle },
    rowDividerStyleProvider: (index: Int) -> DividerStyle = { TableDefaults.RowDividerStyle },
    columnDividerStyleProvider: (index: Int) -> DividerStyle = { TableDefaults.ColumnDividerStyle },
    tableStyle: TableStyle = TableDefaults.TableStyle,
    content: TableScope<T>.() -> Unit
) {
    @Suppress("NAME_SHADOWING") val itemsProvider by rememberUpdatedState(itemsProvider)
    @Suppress("NAME_SHADOWING") val rowStyleProvider by rememberUpdatedState(rowStyleProvider)
    @Suppress("NAME_SHADOWING") val rowDividerStyleProvider by rememberUpdatedState(rowDividerStyleProvider)
    @Suppress("NAME_SHADOWING") val columnDividerStyleProvider by rememberUpdatedState(columnDividerStyleProvider)
    @Suppress("NAME_SHADOWING") val tableStyle by rememberUpdatedState(tableStyle)
    @Suppress("NAME_SHADOWING") val content by rememberUpdatedState(content)
    val provider by derivedStateOf(referentialEqualityPolicy()) {
        TableScope<T>().apply(content)
    }
//    LaunchedEffect(content) {
//        provider = TableScope<T>().apply(content)
//    }
    val density = LocalDensity.current
    val dividersWidth = remember(provider.columns.size, columnDividerStyleProvider, density) {
        provider.columns.indices.toList().dropLast(1).fold(0.dp) { acc, index ->
            acc + columnDividerStyleProvider(index).fullWidth()
        }.toPx(density)
    }
    val decorationsWidth = remember(dividersWidth, tableStyle.horizontalPadding, density) {
        tableStyle.horizontalPadding.times(2).toPx(density).plus(dividersWidth)
    }
    BoxWithConstraints(modifier.fillMaxWidth()
        .wrapContentHeight()
        .widthIn(min = 10.dp * provider.columns.size)
        .clip(tableStyle.shape)
        .background(tableStyle.background, tableStyle.shape)
        .border(tableStyle.borderThickness, tableStyle.borderColor, tableStyle.shape)
    ) {
        Column(Modifier.fillMaxWidth()) {
            val constraints = this@BoxWithConstraints.constraints //TODO: LookaheadLayout
            var prevConstraints by remember { mutableStateOf(constraints) }
            val widthOfColumns = remember(provider.columns) {
                val width = constraints.maxWidth.minus(decorationsWidth).div(provider.columns.size)
                mutableStateListOf(*List(provider.columns.size) { width }.toTypedArray())
            }
            LaunchedEffect(prevConstraints.maxWidth, constraints.maxWidth) {
                val scale = constraints.maxWidth.minus(decorationsWidth) / prevConstraints.maxWidth.minus(decorationsWidth)
                if (scale > 0f && scale.isFinite()) {
                    widthOfColumns.indices.forEach { index ->
                        widthOfColumns[index] = widthOfColumns[index].times(scale)
                    }
                    prevConstraints = constraints
                }
            }
            TableRow(
                provider = provider,
                getColumnWidth = { index -> widthOfColumns[index] },
                setColumnWidth = { index, width -> widthOfColumns[index] = width },
                styleProvider = { headerStyle },
                columnDividerStyleProvider = columnDividerStyleProvider,
                horizontalPadding = { tableStyle.horizontalPadding },
                cellContent = { _, cell -> cell.headerContent() }
            )
            HorizontalDivider(styleProvider = { rowDividerStyleProvider(0) })
            Box(Modifier.fillMaxWidth()) {
                val scrollState = rememberLazyListState()
                Column(Modifier.fillMaxWidth()) {
                    val items = itemsProvider()
                    LazyColumn(state = scrollState, modifier = Modifier.fillMaxWidth()) {
                        itemsIndexed(
                            items = items,
                            key = { index, _ -> index },
                            contentType = { _, _ -> "TableRow" }
                        ) { rowIndex, item ->
                            TableRow(
                                provider = provider,
                                getColumnWidth = { index -> widthOfColumns[index] },
                                setColumnWidth = { index, width -> widthOfColumns[index] = width },
                                styleProvider = { rowStyleProvider(rowIndex, item) },
                                columnDividerStyleProvider = columnDividerStyleProvider,
                                horizontalPadding = { tableStyle.horizontalPadding },
                                cellContent = { _, cell -> cell.cellContent(item) }
                            )
                            HorizontalDivider(styleProvider = { rowDividerStyleProvider(rowIndex + 1) })
                        }
                    }
                    TableRow(
                        provider = provider,
                        getColumnWidth = { index -> widthOfColumns[index] },
                        setColumnWidth = { index, width -> widthOfColumns[index] = width },
                        styleProvider = { TableDefaults.RowStyle },
                        columnDividerStyleProvider = columnDividerStyleProvider,
                        horizontalPadding = { tableStyle.horizontalPadding },
                        cellContent = { _, _ ->  },
                        modifier = Modifier.weight(1f)
                    )
                }
                VerticalScrollbar(scrollState)
            }
        }
    }
}

@Composable
private fun <T> TableRow(
    provider: TableScope<T>,
    getColumnWidth: (index: Int) -> Float,
    setColumnWidth: (index: Int, width: Float) -> Unit,
    styleProvider: () -> RowStyle,
    columnDividerStyleProvider: (index: Int) -> DividerStyle,
    horizontalPadding: () -> Dp,
    modifier: Modifier = Modifier,
    cellContent: @Composable (cellIndex: Int, cell: TableColumn<T>) -> Unit
) {
    @Suppress("NAME_SHADOWING") val provider by rememberUpdatedState(provider)
    @Suppress("NAME_SHADOWING") val getColumnWidth by rememberUpdatedState(getColumnWidth)
    @Suppress("NAME_SHADOWING") val setColumnWidth by rememberUpdatedState(setColumnWidth)
    @Suppress("NAME_SHADOWING") val styleProvider by rememberUpdatedState(styleProvider)
    @Suppress("NAME_SHADOWING") val columnDividerStyleProvider by rememberUpdatedState(columnDividerStyleProvider)
    @Suppress("NAME_SHADOWING") val cellContent by rememberUpdatedState(cellContent)
    val style = styleProvider()
    Row(modifier = modifier
        .height(IntrinsicSize.Max)
        .background(style.background)
        .padding(horizontal = horizontalPadding())
    ) {
        provider.columns.forEachIndexed { cellIndex, cell ->
            key(cellIndex) {
                val density = LocalDensity.current
                Box(Modifier.width(getColumnWidth(cellIndex).toDp(density))) {
                    cellContent(cellIndex, cell)
                }
                if (cellIndex != provider.columns.lastIndex) {
                    VerticalDivider(
                        styleProvider = { columnDividerStyleProvider(cellIndex) },
                        modifier = Modifier.draggable(state = rememberDraggableState {
                            val delta = it
                            setColumnWidth(cellIndex, getColumnWidth(cellIndex) + delta)
                            setColumnWidth(cellIndex + 1, getColumnWidth(cellIndex + 1) - delta)
                        }, orientation = Orientation.Horizontal)
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalDivider(
    styleProvider: () -> DividerStyle,
    modifier: Modifier = Modifier
) {
    val style = styleProvider()
    Spacer(modifier
        .fillMaxWidth()
        .padding(vertical = style.padding)
        .height(style.thickness)
        .background(style.color)
    )
}

@Composable
private fun VerticalDivider(
    styleProvider: () -> DividerStyle,
    modifier: Modifier = Modifier
) {
    val style = styleProvider()
    Spacer(modifier.fillMaxHeight()
        .pointerHoverIcon(PointerIcon.Crosshair)
        .padding(horizontal = style.padding)
        .width(style.thickness)
        .background(style.color)
    )
}

@Composable
fun TableHeaderCell(name: String, modifier: Modifier = Modifier) {
    TableCell(
        text = name,
        modifier = modifier
    )
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.Black,
//        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxSize().background(Color.White)
    )
}
