import me.chophome.slicer.*

object OneFS : Entity<OneFS>("OneFS") {
    val id = scalar("id", -1L, isKey = true)
    val number = scalar("number", null as Int?)
    val text = scalar("text", "---")
    val rndInit = scalar("rndInit") { (0..100).random() }
    val rndCalc = scalar("rndCalc", isCalculated = true) { (0..100).random() }
    val emb = embedded("emb", TwoFS) { TwoFS.createRecord() }
    val scalarList = registerField(ScalarListField(this, "scalarList", false, false, { arrayListOf<Int>() }))
    val objectList = registerField(ObjectListField(this, "objectList", false, false, { arrayListOf() }, ThreeFS))
    val obj1 = obj("obj1", ThreeFS, null as TypedRecord<ThreeFS>?)
    val obj2 = obj("obj2", ThreeFS) { ThreeFS.createRecord() }
}

object TwoFS : Entity<TwoFS>("TwoFS") {
    val aaa = scalar("aaa", "", isKey = true)
    val bbb = scalar("bbb", "", isKey = true)
    val ccc = scalar("ccc", "", isKey = true)
    val xxx = embedded("xxx", ThreeFS) { ThreeFS.createRecord() }
}

object ThreeFS : Entity<ThreeFS>("ThreeFS") {
    val aaa = scalar("aaa", "", isKey = true)
    val bbb = scalar("bbb", "", isKey = true)
    val ccc = scalar("ccc", "", isKey = true)
}

fun main() {
    val record = OneFS.createRecord()
    println(record)
    record[OneFS.text] = "Новый текст"
    record[OneFS.number] = 555
    record[OneFS.emb][TwoFS.aaa] = "AAA"
    repeat(5) { println(record) }


    val list1 = record[OneFS.scalarList]
    record[OneFS.scalarList].add(123)
    record[OneFS.scalarList].add(456)
    record[OneFS.scalarList].add(789)

    val list2 = record[OneFS.objectList]
    record[OneFS.objectList].add(ThreeFS.createRecord().apply { this[ThreeFS.aaa] = "Один" })
    record[OneFS.objectList].add(ThreeFS.createRecord().apply { this[ThreeFS.aaa] = "Два" })
    record[OneFS.objectList].add(ThreeFS.createRecord().apply { this[ThreeFS.aaa] = "Три" })

    println(record)

    OneFS.fields.values.forEach {
        val value = record[it]
        println(value)
    }

//    val store = CsvStore()
//    println(store)

    val query = OneFS.query()
        .select(
            OneFS.id.asNode(),
            OneFS.number.asNode(),
            OneFS.emb.asNode(
                TwoFS.aaa.asNode(),
                TwoFS.ccc.asNode(),
                TwoFS.xxx.asNode(
                    ThreeFS.aaa.asNode(),
                    ThreeFS.bbb.asNode(),
                    ThreeFS.ccc.asNode()
                )
            )
        )//.condition((OneFS.number eq 10) and (Test1.ccc eq true))


    //val path = OneFS.emb..TwoFS.aaa

}
