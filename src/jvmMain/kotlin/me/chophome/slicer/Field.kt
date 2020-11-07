package me.chophome.slicer

sealed class Field<E : Entity<E>, V>(
    val entity: E,
    val code: String,
    val isKey: Boolean,
    val isCalculated: Boolean,
    val calculator: (Record<E>) -> V
) {
    val additional: MutableMap<String, Any?> = LinkedHashMap()
}

/**
 * Поле с одиночными знаениями, может быть вычисляемым
 */
class Scalar<E : Entity<E>, KT>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<E>) -> KT
) : Field<E, KT>(entity, code, isKey, isCalculated, calculator) {
    constructor(entity: E, code: String, isKey: Boolean, initial: KT)
            : this(entity, code, isKey, false, { initial })
}

/**
 * Поле со встроенной записью Entity, может быть вычисляемым
 */
class Embedded<E : Entity<E>, TE : Entity<TE>>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<E>) -> Record<TE>,
    val targetEntity: TE
) : Field<E, Record<TE>>(entity, code, isKey, isCalculated, calculator) {
    constructor(entity: E, code: String, isKey: Boolean, initial: Record<TE>, targetEntity: TE)
            : this(entity, code, isKey, false, { initial }, targetEntity)
}

/**
 * Прямая ссылка по ключам, МНОГИЕ -> ОДИН
 */
class KeyReference<E : Entity<E>, TE : Entity<TE>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val targetEntity: TE,
) : Field<E, Record<TE>?>(entity, code, isKey, false, { null })

/**
 * Прямая ссылка по скалярному полю, МНОГИЕ -> ОДИН
 */
class ScalarReference<E : Entity<E>, TE : Entity<TE>, F : Scalar<TE, *>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val targetField: F
) : Field<E, Record<TE>?>(entity, code, isKey, false, { null }) {
    val targetEntity: TE = targetField.entity
}

/**
 * Обратная ссылка по ключам, ОДИН -> МНОГИЕ
 */
class KeyReferred<E : Entity<E>, RE : Entity<RE>, RF : KeyReference<RE, E>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val referencingField: RF
) : Field<E, List<Record<RE>>>(entity, code, isKey, false, { listOf() }) {

    /**
     * Ссылающийся филдсет (Entity)
     */
    val referencingEntity: RE = referencingField.entity
}

/**
 * Обратная ссылка по скалярному полю, ОДИН -> МНОГИЕ
 */
class ScalarReferred<E : Entity<E>, RE : Entity<RE>, F : Scalar<E, *>, RF : ScalarReference<RE, E, F>>(
    entity: E,
    code: String,
    isKey: Boolean,
    val referencingField: RF
) : Field<E, List<Record<RE>>>(entity, code, isKey, false, { listOf() }) {

    /**
     * Ссылающийся филдсет (Entity)
     */
    val referencingEntity: RE = referencingField.entity

    /**
     * Поле из родительского (entity) филдсета (Entity), по которому ссылается ссылающийся филдсет (referencingEntity - Entity)
     */
    val referencingTargetField: F = referencingField.targetField
}

/**
 * Лист из одиночных значений, может быть вычисляемым
 */
class ScalarList<E : Entity<E>, KT>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<E>) -> MutableList<KT>
) : Field<E, MutableList<KT>>(entity, code, isKey, isCalculated, calculator)

/**
 * Лист из записей Entity, может быть вычисляемым
 */
class EmbeddedList<E : Entity<E>, TE : Entity<TE>>(
    entity: E,
    code: String,
    isKey: Boolean,
    isCalculated: Boolean,
    calculator: (Record<E>) -> MutableList<Record<TE>>,
    val targetEntity: TE
) : Field<E, MutableList<Record<TE>>>(entity, code, isKey, isCalculated, calculator)
