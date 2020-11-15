package me.chophome.slicer

abstract class Store<S : Store<S>> {
    //    abstract fun <E : Entity<E>> getAll(entity: E): List<Record<E>>
    abstract fun <E : Entity<E>> find(query: Query<E>): List<Record<E>>

//    abstract fun <E : Entity<E>> getPage(query: Query<E>, page: Int, pageSize: Int): List<Record<E>>
//    abstract fun <E : Entity<E>> findAll(query: Query<E>): List<Record<E>>
//
//    abstract fun <E : Entity<E>> getFirst(query: Query<E>): Record<E>
//    abstract fun <E : Entity<E>> findFirst(query: Query<E>): Record<E>?
//
//    abstract fun <E : Entity<E>> getLast(query: Query<E>): Record<E>
//    abstract fun <E : Entity<E>> findLast(query: Query<E>): Record<E>?
//
//    abstract fun <E : Entity<E>> save(record: Record<E>)
//    abstract fun <E : Entity<E>> delete(record: Record<E>)
//    abstract fun <E : Entity<E>> delete(query: Query<E>)
//    abstract fun <E : Entity<E>> deleteAll(entity: E)
}
