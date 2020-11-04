import me.chophome.slicer.FieldSet

object OneFS : FieldSet<OneFS>("OneFS") {
    val id = scalar("id", -1L, isKey = true)
    val number = scalar("number", null as Int?)
    val text = scalar("text", "---")
    val rndInit = scalar("rndInit") { (0..100).random() }
    val rndCalc = scalar("rndCalc", isCalculated = true) { (0..100).random() }

    val emb = embedded("emb", TwoFS) { TwoFS.createRecord() }
}

object TwoFS : FieldSet<TwoFS>("TwoFS") {
    val aaa = scalar("aaa", "", isKey = true)
    val bbb = scalar("bbb", "", isKey = true)
    val ccc = scalar("ccc", "", isKey = true)
}

fun main() {
    val r = OneFS.createRecord()
    println(r)
    r[OneFS.text] = "Новый текст"
    r[OneFS.number] = 555
    repeat(5) { println(r) }

}

