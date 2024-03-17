package dev.ahmedmourad.tables.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ahmedmourad.tables.compose.table.Table
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
        Table(users, Modifier.padding(32.dp).fillMaxWidth()) {
            if (show) {
                column("id", NumberFilter()) {
                    Text(
                        text = it.id.toString(),
                        color = Color.Black
                    )
                }
            }
            column("name") {
                Text(
                    text = it.name,
                    color = Color.Black
                )
            }
            column("age") {
                Text(
                    text = it.age.toString(),
                    color = Color.Black
                )
            }
            column("height") {
                Text(
                    text = it.height.toString(),
                    color = Color.Black
                )
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
        content: @Composable (T) -> Unit
    ) {
        columns.add(TableColumn(name, content))
    }
}

@Immutable
data class RowStyle(val background: Color = Color.Transparent)

@Immutable
data class TableColumn<T>(val name: String, val content: @Composable (T) -> Unit)


interface TableFilter {

}

class NumberFilter : TableFilter {

}


object TableDefaults {
    val RowStyle = RowStyle()
}
