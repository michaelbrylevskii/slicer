package me.chophome.slicer

abstract class Record{
    protected val _data: MutableRecordData = LinkedHashMap()
    val data: RecordData = _data

    open operator fun get(fieldCode: String): Any? {
        return _data[fieldCode]
    }

    open operator fun set(fieldCode: String, value: Any?) {
        _data[fieldCode] = value
    }
}

class TypedRecord<E : Entity<E>>(
    val entity: E
) : Record() {
    operator fun <R> get(field: Field<E, R>): R {
        checkField(field)
        return if (field.isCalculated) field.calculator(this) else _data[field.code] as R
    }

    operator fun <T> set(field: Field<out E, T>, value: T) {
        checkField(field)
        if (!field.isCalculated) _data[field.code] = value
    }

    override operator fun get(fieldCode: String): Any? {
        val field = getFieldWithCheck(fieldCode)
        return if (field.isCalculated) field.calculator(this) else _data[field.code]
    }

    override operator fun set(fieldCode: String, value: Any?) {
        val field = getFieldWithCheck(fieldCode)
        if (!field.isCalculated) _data[field.code] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("TypedRecord : keys[")
        entity.keys.values.joinTo(sb) { "${it.code}='${get(it)}'" }
        sb.append("] : fields[")
        entity.fields.values.joinTo(sb) { "${it.code}='${get(it)}'" }
        sb.append("]")
        return sb.toString()
    }

    fun <T> resetToDefault(field: Field<E, T>) {
        if (!field.isCalculated)
            set(field, field.calculator(this))
    }

    fun resetToDefault(fieldCode: String) {
        val field = getFieldWithCheck(fieldCode)
        if (!field.isCalculated)
            set(fieldCode, field.calculator(this))
    }

    fun resetToDefault() {
        entity.fields.values.forEach { resetToDefault(it) }
    }

    private fun getFieldWithCheck(fieldCode: String): Field<E, *> {
        val field = entity.fields[fieldCode]
            ?: throw IllegalArgumentException("Field '$fieldCode' is not exists in entity '${entity.code}'!")
        return checkField(field)
    }

    private fun <F : Field<*, *>> checkField(field: F): F {
        if (field.entity != entity)
            throw IllegalArgumentException("Passed field entity '${field.entity.code}' not equals with record entity '${entity.code}'!")
        return field
    }
}

class DynamicRecord : Record() {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("DynamicRecord : fields[")
        data.entries.joinTo(sb) { "${it.key}='${it.value}'" }
        sb.append("]")
        return sb.toString()
    }
}