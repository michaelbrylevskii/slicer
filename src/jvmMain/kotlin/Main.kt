import me.chophome.slicer.*

object OneFS : Entity<OneFS>("OneFS") {
    val id = scalar("id", -1L, isKey = true)
    val number = scalar("number", null as Int?)
    val text = scalar("text", "---")
    val rndInit = scalar("rndInit") { (0..100).random() }
    val rndCalc = scalar("rndCalc", isCalculated = true) { (0..100).random() }
    val emb = embedded("emb", TwoFS) { TwoFS.createRecord() }
    val list = add(ScalarList(this, "list", false, false, { arrayListOf<Record<TwoFS>>() }))
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
    val r = OneFS.createRecord()
    println(r)
    r[OneFS.text] = "Новый текст"
    r[OneFS.number] = 555
    r[OneFS.emb][TwoFS.aaa] = "AAA"
    repeat(5) { println(r) }


    val list = r[OneFS.list]
    r[OneFS.list].add(TwoFS.createRecord())
    println(r)

    OneFS.fields.values.forEach {
        val v = r[it]
        r[it.code] = r[it]
        println(v)
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

}
