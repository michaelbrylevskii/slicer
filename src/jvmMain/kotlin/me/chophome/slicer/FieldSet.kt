package me.chophome.slicer

// KT - Kotlin Type
// F - Field
// FS - FieldSet

typealias FieldsMap<FS> = Map<String, Field<FS, *>>
typealias MutableFieldsMap<FS> = MutableMap<String, Field<FS, *>>
typealias RecordData = Map<String, Any?>
typealias MutableRecordData = MutableMap<String, Any?>

abstract class FieldSet<FS : FieldSet<FS>>(
    val code: String
) {
    private val _keys: MutableFieldsMap<FS> = LinkedHashMap()
    private val _fields: MutableFieldsMap<FS> = LinkedHashMap()

    val keys: FieldsMap<FS> = _keys
    val fields: FieldsMap<FS> = _fields

    fun createRecord(): Record<FS> {
        val record = Record(this as FS)
        record.resetToDefault()
        return record
    }

    protected fun <T, F : Field<FS, T>> add(field: F): F {
        if (field.isKey) _keys[field.code] = field
        _fields[field.code] = field
        return field
    }

    protected fun <KT> scalar(
        code: String,
        isKey: Boolean = false,
        isCalculated: Boolean = false,
        calculator: (Record<FS>) -> KT
    ): Scalar<FS, KT> {
        return add(Scalar(this as FS, code, isKey, isCalculated, calculator))
    }

    protected fun <KT> scalar(
        code: String,
        initial: KT,
        isKey: Boolean = false
    ): Scalar<FS, KT> {
        return add(Scalar(this as FS, code, isKey, initial))
    }

    protected fun <TFS : FieldSet<TFS>> embedded(
        code: String,
        target: TFS,
        isKey: Boolean = false,
        isCalculated: Boolean = false,
        calculator: (Record<FS>) -> Record<TFS>
    ): Embedded<FS, TFS> {
        return add(Embedded(this as FS, code, isKey, isCalculated, calculator, target))
    }

    protected fun <TFS : FieldSet<TFS>> embedded(
        code: String,
        target: TFS,
        initial: Record<TFS>,
        isKey: Boolean = false
    ): Embedded<FS, TFS> {
        return add(Embedded(this as FS, code, isKey, initial, target))
    }
}

//object DynamicFieldSet : FieldSet<DynamicFieldSet>("__dynamic__")

class Record<FS : FieldSet<FS>>(
    val fieldSet: FS
) {
    private val data: MutableRecordData = LinkedHashMap()

    operator fun <R> get(field: Field<FS, R>): R {
        return if (field.isCalculated) field.calculator(this) else data[field.code] as R
    }

    operator fun <T> set(field: Field<FS, T>, value: T) {
        if (!field.isCalculated) data[field.code] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Record<${fieldSet.code}> : keys[")
        fieldSet.keys.values.joinTo(sb) { "${it.code}='${get(it)}'" }
        sb.append("] : fields[")
        fieldSet.fields.values.joinTo(sb) { "${it.code}='${get(it)}'" }
        sb.append("]")
        return sb.toString()
    }

    fun <T> resetToDefault(field: Field<FS, T>) {
        if (!field.isCalculated)
            set(field, field.calculator(this))
    }

    fun resetToDefault() {
        fieldSet.fields.values.forEach { resetToDefault(it) }
    }
}

//enum class ValidationLevel {
//    OK, INFO, WARNING, ERROR
//}
//
//class FieldSetValidationResult(
//    val level: ValidationLevel,
//    val messages: MutableList<String> = ArrayList()
//)
//
//interface FieldSetValidator<KT, FS : FieldSet<FS>> {
//    val fieldSet: FS
//    val errorMessage: String
//    fun validate(value: KT): FieldSetValidationResult
//}


