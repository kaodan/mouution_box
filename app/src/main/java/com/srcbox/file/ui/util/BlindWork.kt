package com.srcbox.file.ui.util
import com.alibaba.fastjson.JSON
import com.srcbox.file.util.EggIO
import java.io.InputStream
import java.util.*

class BlindWork() {
    fun speak(inputStream: InputStream, title: String, length: Long):String {
        var content = ""
        val worldTab = EggIO.readFile(inputStream)
        val jsonO = JSON.parseObject(worldTab)
        val famous = JSON.parseArray(jsonO.getString("famous"))
        val before = JSON.parseArray(jsonO.getString("before"))
        val after = JSON.parseArray(jsonO.getString("after"))
        val bosh = JSON.parseArray(jsonO.getString("bosh"))
        while (content.length < length) {
            content += when (rand(100)) {
                10 -> {
                    "\r\n"
                }
                20 -> {
                    famous.getString(rand(famous.size))
                        .replace("a", before.getString(rand(before.size)))
                        .replace("b", after.getString(rand(after.size)))
                }
                else -> {
                    bosh.getString(rand(bosh.size)).replace("x", title)
                }
            }
        }
        return content
    }

    private fun rand(bound: Int): Int {
        return Random().nextInt(bound)
    }
}