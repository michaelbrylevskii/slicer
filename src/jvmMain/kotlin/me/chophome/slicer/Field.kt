package me.chophome.slicer

sealed class Field<FS : FieldSet<FS>, V>(
    val parent: FS,
    val code: String,
    val isKey: Boolean,
    val isCalculated: Boolean,
    val calculator: (Record<FS>) -> V
) {
    val additional: MutableMap<String, Any?> = LinkedHashMap()
}

// Поле с примитивом
class Scalar<FS : FieldSet<FS>, KT>(
    parent: FS,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<FS>) -> KT
) : Field<FS, KT>(parent, code, isKey, isCalculated, calculator) {
    constructor(parent: FS, code: String, isKey: Boolean, initial: KT)
            : this(parent, code, isKey, false, { initial })
}

// Поле со встроенной записью FieldSet
class Embedded<FS : FieldSet<FS>, TFS : FieldSet<TFS>>(
    parent: FS,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<FS>) -> Record<TFS>,
    val targetFieldSet: TFS
) : Field<FS, Record<TFS>>(parent, code, isKey, isCalculated, calculator) {
    constructor(parent: FS, code: String, isKey: Boolean, initial: Record<TFS>, targetFieldSet: TFS)
            : this(parent, code, isKey, false, { initial }, targetFieldSet)
}

/**
 * Прямая ссылка по ключам, МНОГИЕ -> ОДИН
 */
class KeyReference<FS : FieldSet<FS>, TFS : FieldSet<TFS>>(
    parent: FS,
    code: String,
    isKey: Boolean,
    val targetFieldSet: TFS,
) : Field<FS, Record<TFS>?>(parent, code, isKey, false, { null })

/**
 * Прямая ссылка по скалярному полю, МНОГИЕ -> ОДИН
 */
class ScalarReference<FS : FieldSet<FS>, TFS : FieldSet<TFS>, F : Scalar<TFS, *>>(
    parent: FS,
    code: String,
    isKey: Boolean,
    val targetField: F
) : Field<FS, Record<TFS>?>(parent, code, isKey, false, { null }) {
    val targetFieldSet: TFS = targetField.parent
}

/**
 * Обратная ссылка по ключам, ОДИН -> МНОГИЕ
 */
class KeyReferred<FS : FieldSet<FS>, RFS : FieldSet<RFS>, RF : KeyReference<RFS, FS>>(
    parent: FS,
    code: String,
    isKey: Boolean,
    val referencingField: RF
) : Field<FS, List<Record<RFS>>>(parent, code, isKey, false, { listOf() }) {

    /**
     * Ссылающийся филдсет (FieldSet)
     */
    val referencingFieldSet: RFS = referencingField.parent
}

/**
 * Обратная ссылка по скалярному полю, ОДИН -> МНОГИЕ
 */
class ScalarReferred<FS : FieldSet<FS>, RFS : FieldSet<RFS>, F : Scalar<FS, *>, RF : ScalarReference<RFS, FS, F>>(
    parent: FS,
    code: String,
    isKey: Boolean,
    val referencingField: RF
) : Field<FS, List<Record<RFS>>>(parent, code, isKey, false, { listOf() }) {

    /**
     * Ссылающийся филдсет (FieldSet)
     */
    val referencingFieldSet: RFS = referencingField.parent

    /**
     * Поле из родительского (parent) филдсета (FieldSet), по которому ссылается ссылающийся филдсет (referencingFieldSet - FieldSet)
     */
    val referencingTargetField: F = referencingField.targetField
}

// Поле с листом
class ListField<FS : FieldSet<FS>, T>(
    parent: FS,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<FS>) -> List<T> = { listOf() }
) : Field<FS, List<T>>(parent, code, isKey, isCalculated, calculator)
