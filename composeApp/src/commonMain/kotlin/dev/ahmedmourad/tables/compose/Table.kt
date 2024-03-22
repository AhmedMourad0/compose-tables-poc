package dev.ahmedmourad.tables.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ahmedmourad.tables.compose.table.Table
import dev.ahmedmourad.tables.compose.table.TableCell
import dev.ahmedmourad.tables.compose.table.TableHeaderCell
import kotlin.random.Random

@Stable
data class User(
    val id: Int,
    val name: String,
    val age: Int,
    val height: Double
)

fun randomUser() = User(
    id = Random.nextInt(),
    name = "Hallo",
    age = Random.nextInt(),
    height = Random.nextDouble()
)

@Composable
fun Sample() {
    Column(Modifier.fillMaxSize()) {
        val users = remember { List(10) { randomUser() } }
        var sorters by remember { mutableStateOf("") }
        var filters by remember { mutableStateOf("") }
        var show by remember { mutableStateOf(true) }
        Button({ show = !show }) {
            Text("cccc")
        }
        Table(
            itemsProvider = { users },
            rowStyleProvider = { index, _ ->
                if (index % 2 == 0) {
                    TableDefaults.RowStyle
                } else {
                    TableDefaults.RowStyle//.copy(background = Color.LightGray)
                }
            }, rowDividerStyleProvider = { index ->
                if (index == 0) {
                    TableDefaults.RowDividerStyle
                } else {
                    TableDefaults.RowDividerStyle//.copy(thickness = 0.dp)
                }
            }, columnDividerStyleProvider = {
                TableDefaults.ColumnDividerStyle//.copy(thickness = 0.dp)
            }, modifier = Modifier.padding(32.dp).fillMaxWidth()
        ) {
            if (show) {
                column("id", NumberFilter()) {
                    TableCell(it.id.toString())
                }
            }
            column("name") {
                TableCell(it.name)
            }
            column("age") {
                TableCell(it.age.toString())
            }
            column("height") {
                TableCell(it.height.toString())
            }
        }
    }
}

@Stable
class TableScope<T> {

    val columns = mutableStateListOf<TableColumn<T>>()

    fun column(
        name: String,
        filter: TableFilter? = null,
        key: String = name,
        weight: Float = 1f,
        cellContent: @Composable (T) -> Unit
    ) {
        column(
            key = key,
            filter = filter,
            initialWidth = ColumnWidth.Weight(weight),
            headerContent = { TableHeaderCell(name) },
            cellContent = cellContent
        )
    }

    fun column(
        key: String,
        filter: TableFilter? = null,
        initialWidth: ColumnWidth = ColumnWidth.Weight(1f),
        headerContent: @Composable () -> Unit,
        cellContent: @Composable (T) -> Unit
    ) {
        columns.add(TableColumn(
            key = key,
            initialWidth = initialWidth,
            headerContent = headerContent,
            cellContent = cellContent
        ))
    }
}

sealed interface ColumnWidth {
    data class Weight(val value: Float) : ColumnWidth
    data class Fixed(val value: Dp) : ColumnWidth
    data object WrapContent
}

@Immutable
data class RowStyle(val background: Color = Color.Transparent)

@Immutable
data class DividerStyle(
    val thickness: Dp = 1.dp,
    val padding: Dp,
    val color: Color = Color.LightGray
)

@Stable
fun DividerStyle.fullWidth(): Dp {
    return thickness.plus(padding * 2)
}

@Immutable
data class TableStyle(
    val borderThickness: Dp = 1.dp,
    val borderColor: Color = Color.LightGray,
    val shape: Shape = RoundedCornerShape(8.dp),
    val background: Color = Color.White,
    val horizontalPadding: Dp = 4.dp
)

@Immutable
data class TableColumn<T>(
    val key: String,
    val initialWidth: ColumnWidth,
    val headerContent: @Composable () -> Unit,
    val cellContent: @Composable (T) -> Unit
)


interface TableFilter {

}

class NumberFilter : TableFilter {

}


object TableDefaults {
    val HeaderStyle = RowStyle()
    val RowStyle = RowStyle()
    val RowDividerStyle = DividerStyle(padding = 0.dp)
    val ColumnDividerStyle = DividerStyle(padding = 4.dp)
    val TableStyle = TableStyle()
}
