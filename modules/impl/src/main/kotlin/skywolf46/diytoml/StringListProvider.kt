package skywolf46.diytoml

class StringListProvider(val iterator: Iterator<String>) : DataProvider {
    override fun provide(): String {
        return iterator.next()
    }

    override fun hasMoreContents(): Boolean {
        return iterator.hasNext()
    }

    override fun clean() {
        // Do nothing
    }

}