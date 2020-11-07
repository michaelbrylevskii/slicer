import me.chophome.slicer.Entity
import me.chophome.slicer.Record
import me.chophome.slicer.ScalarList

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

}

