package me.chophome.slicer

abstract class Entity<E : Entity<E>>(val code: String) {
    private val _keys: MutableFieldsMap<E> = LinkedHashMap()
    private val _fields: MutableFieldsMap<E> = LinkedHashMap()
    val keys: FieldsMap<E> = _keys
    val fields: FieldsMap<E> = _fields

    fun createRecord(): TypedRecord<E> {
        val record = TypedRecord(this as E)
        record.resetToDefault()
        return record
    }

    fun <T, F : Field<E, T>> registerField(field: F): F {
        if (field.isKey) _keys[field.code] = field
        _fields[field.code] = field
        return field
    }
}
