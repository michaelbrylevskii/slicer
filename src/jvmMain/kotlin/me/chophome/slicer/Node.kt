package me.chophome.slicer

interface Node<E : Entity<E>> {
    val field: Field<E, *>
}

open class ScalarNode<E : Entity<E>>(override val field: Field<E, *>) : Node<E>

class ObjectNode<E : Entity<E>, TE : Entity<TE>>(override val field: Field<E, *>, nodes: Iterable<Node<TE>>) : Node<E> {
    val children: MutableSet<Node<TE>> = LinkedHashSet()

    init {
        children.addAll(nodes)
    }
}

fun <E : Entity<E>> ScalarField<E, *>.asNode(): Node<E> {
    return ScalarNode(this)
}

fun <E : Entity<E>, TE : Entity<TE>> EmbeddedField<E, TE>.asNode(vararg nodes: Node<TE>): ObjectNode<E, TE> {
    return ObjectNode(this, nodes.asIterable())
}

//a..b	a.rangeTo(b)

operator fun <E : Entity<E>, TE : Entity<TE>> EmbeddedField<E, TE>.rangeTo(field: ScalarField<TE, *>): ObjectNode<E, TE>  {
    return this.asNode().apply { children.add(field.asNode()) }
}
operator fun <E : Entity<E>, TE : Entity<TE>> EmbeddedField<E, TE>.rangeTo(field: EmbeddedField<TE, *>): ObjectNode<E, TE>  {
    return this.asNode().apply { children.add(field.asNode()) }
}
//operator fun <E : Entity<E>, TE : Entity<TE>> ObjectNode<E, TE>.rangeTo(field: Scalar<TE, *>): ObjectNode<E, TE>  {
//    return this.asNode().apply { children.add(field.asNode()) }
//}