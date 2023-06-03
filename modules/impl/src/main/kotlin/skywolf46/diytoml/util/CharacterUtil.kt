package skywolf46.diytoml.util

fun collect(chars: Array<Char>, vararg range: CharRange): Array<Char> {
    return chars + range.map { it.toList() }.flatten()
}