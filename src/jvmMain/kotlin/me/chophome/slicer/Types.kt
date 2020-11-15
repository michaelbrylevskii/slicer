package me.chophome.slicer

typealias FieldsMap<FS> = Map<String, Field<FS, *>>
typealias MutableFieldsMap<FS> = MutableMap<String, Field<FS, *>>
typealias RecordData = Map<Field<*, *>, Any?>
typealias MutableRecordData = MutableMap<Field<*, *>, Any?>