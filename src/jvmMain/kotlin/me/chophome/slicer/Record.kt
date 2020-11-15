package me.chophome.slicer

class Record<E : Entity<E>>(
    val entity: E
) {
    private val _data: MutableRecordData = LinkedHashMap()
    val data: RecordData = _data

    operator fun <R> get(field: Field<E, R>): R {
        checkField(field)
        return if (field.isCalculated) field.calculator(this) else _data[field] as R
    }

    operator fun <T> set(field: Field<out E, T>, value: T) {
        checkField(field)
        if (!field.isCalculated) _data[field] = value
    }

    operator fun get(fieldCode: String): Any? {
        val field = checkField(getField(fieldCode))
        return if (field.isCalculated) field.calculator(this) else _data[field]
    }

    operator fun set(fieldCode: String, value: Any?) {
        val field = checkField(getField(fieldCode))
        if (!field.isCalculated) _data[field] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Record : keys[")
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
        val field = getField(fieldCode)
        if (!field.isCalculated)
            set(fieldCode, field.calculator(this))
    }

    fun resetToDefault() {
        entity.fields.values.forEach { resetToDefault(it) }
    }

    private fun getField(fieldCode: String): Field<E, *> {
        return entity.fields[fieldCode]
            ?: throw IllegalArgumentException("Field '$fieldCode' is not exists in entity '${entity.code}'!")
    }

    private fun <F : Field<*, *>> checkField(field: F): F {
        if (field.entity != entity)
            throw IllegalArgumentException("Passed field entity '${field.entity.code}' not equals with record entity '${entity.code}'!")
        return field
    }
}