package me.chophome.slicer

class Query<E : Entity<E>>(val entity: E) {
    val nodes: MutableList<Node<E>> = ArrayList()
    var condition: Condition? = null
}

fun <E : Entity<E>> E.query(): Query<E> {
    return Query(this)
}

fun <E : Entity<E>> Query<E>.select(vararg nodes: Node<E>): Query<E> {
    this.nodes.ifIsNotEmpty { it.clear() }
    this.nodes.addAll(nodes)
    return this
}

fun <E : Entity<E>> Query<E>.condition(condition: Condition): Query<E> {
    TODO()
}
