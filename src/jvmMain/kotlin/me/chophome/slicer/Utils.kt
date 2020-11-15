package me.chophome.slicer

inline fun <C : Collection<T>, T> C.ifIsEmpty(action: (C) -> Unit) {
    if (this.isEmpty())
        action(this)
}

inline fun <C : Collection<T>, T> C.ifIsNotEmpty(action: (C) -> Unit) {
    if (this.isNotEmpty())
        action(this)
}