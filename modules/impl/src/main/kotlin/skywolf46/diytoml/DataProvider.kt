package skywolf46.diytoml

interface DataProvider {
    fun provide(): String

    fun hasMoreContents() : Boolean

    fun clean()
}