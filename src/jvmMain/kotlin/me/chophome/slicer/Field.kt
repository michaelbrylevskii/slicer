package me.chophome.slicer

/**
 * Все реализации:
 * ScalarField
 * EmbeddedField
 * ObjectField
 * DynamicField
 * KeyReferenceField
 * ScalarReferenceField
 * KeyBackReferenceField
 * ScalarBackReferenceField
 * ScalarListField
 * ObjectListField
 * DynamicListField
 */
sealed class Field<E : Entity<E>, V>(
    val entity: E,
    val code: String,
    val isKey: Boolean,
    val isCalculated: Boolean,
    val calculator: (TypedRecord<E>) -> V
) {
    val additional: MutableMap<String, Any?> = LinkedHashMap()
}

/**
 * Поле с одиночными знаениями, может быть вычисляемым
 */
class ScalarField<E : Entity<E>, KT>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> KT
) : Field<E, KT>(entity, code, isKey, isCalculated, calculator) {
    constructor(entity: E, code: String, isKey: Boolean, initial: KT)
            : this(entity, code, isKey, false, { initial })
}

/**
 * Поле со встроенной записью Entity, может быть вычисляемым
 */
class EmbeddedField<E : Entity<E>, TE : Entity<TE>>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> TypedRecord<TE>,
    val targetEntity: TE
) : Field<E, TypedRecord<TE>>(entity, code, isKey, isCalculated, calculator) {
    constructor(entity: E, code: String, isKey: Boolean, initial: TypedRecord<TE>, targetEntity: TE)
            : this(entity, code, isKey, false, { initial }, targetEntity)
}

/**
 * Поле с записью Entity, может быть вычисляемым
 */
class ObjectField<E : Entity<E>, TE : Entity<TE>, R : TypedRecord<TE>?>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> R,
    val targetEntity: TE
) : Field<E, R>(entity, code, isKey, isCalculated, calculator) {
    constructor(entity: E, code: String, isKey: Boolean, initial: R, targetEntity: TE)
            : this(entity, code, isKey, false, { initial }, targetEntity)
}

/**
 * Поле с линачической записью, может быть вычисляемым
 */
class DynamicField<E : Entity<E>, TR : DynamicRecord?>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> TR
) : Field<E, TR>(entity, code, isKey, isCalculated, calculator) {
    constructor(entity: E, code: String, isKey: Boolean, initial: TR)
            : this(entity, code, isKey, false, { initial })
}

/**
 * Прямая ссылка по ключам, МНОГИЕ -> ОДИН
 */
class KeyReferenceField<E : Entity<E>, TE : Entity<TE>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val targetEntity: TE,
) : Field<E, TypedRecord<TE>?>(entity, code, isKey, false, { null })

/**
 * Прямая ссылка по скалярному полю, МНОГИЕ -> ОДИН
 */
class ScalarReferenceField<E : Entity<E>, TE : Entity<TE>, F : ScalarField<TE, *>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val targetFields: Set<F>
) : Field<E, TypedRecord<TE>?>(entity, code, isKey, false, { null }) {
    val targetEntity: TE = targetFields.first().entity
}

/**
 * Обратная ссылка по ключам, ОДИН -> МНОГИЕ
 */
class KeyBackReferenceField<E : Entity<E>, RE : Entity<RE>, RF : KeyReferenceField<RE, E>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val reference: RF
) : Field<E, List<TypedRecord<RE>>>(entity, code, isKey, false, { listOf() })

/**
 * Обратная ссылка по скалярному полю, ОДИН -> МНОГИЕ
 */
class ScalarBackReferenceField<E : Entity<E>, RE : Entity<RE>, F : ScalarField<E, *>, RF : ScalarReferenceField<RE, E, F>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val reference: RF
) : Field<E, List<TypedRecord<RE>>>(entity, code, isKey, false, { listOf() })

/**
 * Лист из скалярных значений, может быть вычисляемым
 */
class ScalarListField<E : Entity<E>, KT>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> MutableList<KT>
) : Field<E, MutableList<KT>>(entity, code, isKey, isCalculated, calculator)

/**
 * Лист из типизированных объектов, может быть вычисляемым
 */
class ObjectListField<E : Entity<E>, TE : Entity<TE>>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> MutableList<TypedRecord<TE>>,
    val targetEntity: TE
) : Field<E, MutableList<TypedRecord<TE>>>(entity, code, isKey, isCalculated, calculator)

/**
 * Лист из динамических объектов, может быть вычисляемым
 */
class DynamicListField<E : Entity<E>>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (TypedRecord<E>) -> MutableList<DynamicRecord>
) : Field<E, MutableList<DynamicRecord>>(entity, code, isKey, isCalculated, calculator)

/* ------------------------------------------- */
/* --- Расширения создания полей из Entity --- */
/* ------------------------------------------- */

fun <E : Entity<E>, T> E.scalar(
    code: String,
    isKey: Boolean = false,
    isCalculated: Boolean = false,
    calculator: (TypedRecord<E>) -> T
): ScalarField<E, T> {
    return registerField(ScalarField(this, code, isKey, isCalculated, calculator))
}

fun <E : Entity<E>, T> E.scalar(
    code: String,
    initial: T,
    isKey: Boolean = false
): ScalarField<E, T> {
    return registerField(ScalarField(this, code, isKey, initial))
}

fun <E : Entity<E>, TE : Entity<TE>> E.embedded(
    code: String,
    target: TE,
    isKey: Boolean = false,
    isCalculated: Boolean = false,
    calculator: (TypedRecord<E>) -> TypedRecord<TE>
): EmbeddedField<E, TE> {
    return registerField(EmbeddedField(this, code, isKey, isCalculated, calculator, target))
}

fun <E : Entity<E>, TE : Entity<TE>> E.embedded(
    code: String,
    target: TE,
    initial: TypedRecord<TE>,
    isKey: Boolean = false
): EmbeddedField<E, TE> {
    return registerField(EmbeddedField(this, code, isKey, initial, target))
}

fun <E : Entity<E>, TE : Entity<TE>, TR : TypedRecord<TE>?> E.obj(
    code: String,
    target: TE,
    isKey: Boolean = false,
    isCalculated: Boolean = false,
    calculator: (TypedRecord<E>) -> TR
): ObjectField<E, TE, TR> {
    return registerField(ObjectField(this, code, isKey, isCalculated, calculator, target))
}

fun <E : Entity<E>, TE : Entity<TE>, TR : TypedRecord<TE>?> E.obj(
    code: String,
    target: TE,
    initial: TR,
    isKey: Boolean = false
): ObjectField<E, TE, TR> {
    return registerField(ObjectField(this, code, isKey, initial, target))
}

fun <E : Entity<E>, TR : DynamicRecord?> E.dynamic(
    code: String,
    isKey: Boolean = false,
    isCalculated: Boolean = false,
    calculator: (TypedRecord<E>) -> TR
): DynamicField<E, TR> {
    return registerField(DynamicField(this, code, isKey, isCalculated, calculator))
}

fun <E : Entity<E>, TR : DynamicRecord?> E.dynamic(
    code: String,
    initial: TR,
    isKey: Boolean = false
): DynamicField<E, TR> {
    return registerField(DynamicField(this, code, isKey, initial))
}

// TODO:
// KeyReferenceField
// ScalarReferenceField
// KeyBackReferenceField
// ScalarBackReferenceField
// ScalarListField
// ObjectListField
// DynamicListField