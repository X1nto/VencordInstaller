import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class)
fun <T> optionSelector(
    title: String,
    @BuilderInference
    builder: OptionSelector<T>.() -> Unit
) = OptionSelector<T>(title).apply(builder)

class OptionSelector<T>(private val title: String) {

    private val options = mutableListOf<Pair<String, T>>()

    fun addOption(option: Pair<String, T>) {
        options.add(option)
    }

    fun requestSelect(): T {
        println(toString())

        var selected = readln().toIntOrNull()

        while (selected !in 1..options.size) {
            println("Invalid option")
            selected = readln().toIntOrNull()
        }

        return options[selected!! - 1].second
    }

    override fun toString(): String {
        return """
            |$title
            ${options.mapIndexed { i, s -> "|${i + 1}. ${s.first}" }.joinToString("\n")}
        """.trimMargin()
    }
}