package me.chophome.slicer

abstract class Entity<E : Entity<E>>(val code: String) {
    private val _keys: MutableFieldsMap<E> = LinkedHashMap()
    private val _fields: MutableFieldsMap<E> = LinkedHashMap()
    val keys: FieldsMap<E> = _keys
    val fields: FieldsMap<E> = _fields

    fun createRecord(): Record<E> {
        val record = Record(this as E)
        record.resetToDefault()
        return record
    }

    protected fun <T, F : Field<E, T>> add(field: F): F {
        if (field.isKey) _keys[field.code] = field
        _fields[field.code] = field
        return field
    }

    protected fun <T> scalar(
        code: String,
        isKey: Boolean = false,
        isCalculated: Boolean = false,
        calculator: (Record<E>) -> T
    ): Scalar<E, T> {
        return add(Scalar(this as E, code, isKey, isCalculated, calculator))
    }

    protected fun <T> scalar(
        code: String,
        initial: T,
        isKey: Boolean = false
    ): Scalar<E, T> {
        return add(Scalar(this as E, code, isKey, initial))
    }

    protected fun <TE : Entity<TE>> embedded(
        code: String,
        target: TE,
        isKey: Boolean = false,
        isCalculated: Boolean = false,
        calculator: (Record<E>) -> Record<TE>
    ): Embedded<E, TE> {
        return add(Embedded(this as E, code, isKey, isCalculated, calculator, target))
    }

    protected fun <TE : Entity<TE>> embedded(
        code: String,
        target: TE,
        initial: Record<TE>,
        isKey: Boolean = false
    ): Embedded<E, TE> {
        return add(Embedded(this as E, code, isKey, initial, target))
    }
}

//class Union(val entities : Set<Entity<*>>)


//object QQQ : Entity<QQQ>("qqq") {
//    val qText = scalar("qText", "q")
//}
//
//object WWW : Entity<WWW>("www") {
//    val wText = scalar("wText", "w")
//}
//
//fun test() {
//    val un = Union(setOf(QQQ, WWW))
//    un[QQQ.qText] = "qq"
//    un[WWW.wText] = "ww"
//}


