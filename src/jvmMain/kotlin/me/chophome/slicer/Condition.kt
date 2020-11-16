package me.chophome.slicer

class Condition

infix fun <KT> ScalarField<*, KT>.eq(value: KT): Condition {
    TODO()
}

infix fun Condition.and(condition: Condition): Condition {
    TODO()
}

infix fun Condition.or(condition: Condition): Condition {
    TODO()
}