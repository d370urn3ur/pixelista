package the.autarch.pixelista

import androidx.compose.ui.graphics.Color

class History {

    private val operations = mutableListOf<Operation>()

    fun addOperation(op: Operation) {
        operations.add(op)
    }

    fun undo(pxField: List<List<Color>>): List<List<Color>> {
        return if (operations.lastIndex != -1) {
            val prev = operations.removeAt(operations.lastIndex)
            List(pxField.size) { row ->
                List(pxField.firstOrNull()?.size ?: 0) { col ->
                    if (row == prev.row && col == prev.col) {
                        prev.oldColor
                    } else {
                        pxField[row][col]
                    }
                }
            }
        } else {
            pxField
        }
    }

    fun clear() {
        operations.clear()
    }
}

data class Operation(val row: Int, val col: Int, val oldColor: Color)