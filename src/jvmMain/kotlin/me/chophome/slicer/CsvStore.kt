package me.chophome.slicer

class CsvStore : Store<CsvStore>() {
    override fun <E : Entity<E>> find(query: Query<E>): List<Record<E>> {
        TODO()
    }
}
