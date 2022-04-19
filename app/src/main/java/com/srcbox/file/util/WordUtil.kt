package com.srcbox.file.util

import org.jsoup.Jsoup

class WordUtil(val word: String) {
    fun find(): WordMessage {
        val j = Jsoup.connect("https://dict.baidu.com/s?wd=$word")
            .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Mobile Safari/537.36")
            .execute()
        var spell = ""
        var spellAudioUrl = ""
        var basicMean = ""
        var grammar = ""
        val synonyms = ArrayList<String>()
        val antonyms = ArrayList<String>()
        val zaojus = ArrayList<String>()
        val detailMeans = ArrayList<String>()

        val parse = j.parse()
        val r = parse.getElementById("results")?.apply {
            getElementsByClass("term-pinyin")?.apply {
                spell = select("b").text()
                spellAudioUrl = select("a").attr("url")
            }

            if (spell.isEmpty()) {
                getElementById("pinyin").apply {
                    spell = select("b").text()
                    spellAudioUrl = select("a").attr("url")
                }
            }

            basicMean = getElementById("base-mean").select("dl").select("dd").text()
            getElementById("detail-mean")?.select("li")?.forEach {
                detailMeans.add(it.text())
            }

            grammar = getElementsByClass("grammar").text()

            getElementsByClass("zaoju-mean")?.select("div")?.forEach {
                kotlin.run {
                    val t = it.text()
                    if (t == "查看更多") {
                        return@run
                    }
                    zaojus.add(t)
                }

            }

            if (zaojus.isNotEmpty()) {
                zaojus.removeAt(0)
            }

            getElementsByClass("synonym-content").select("a").forEach {
                synonyms.add(it.text())
                println(it.text())
            }

            getElementsByClass("antonym-content").select("a").forEach {
                antonyms.add(it.text())
            }

        }

        if (r == null) {
            spell = parse.getElementsByClass("zici-item-name").text().replace(word, "")
            basicMean = parse.getElementsByClass("zici-item-def").text()
        }
        return WordMessage(
            spell,
            spellAudioUrl,
            basicMean,
            detailMeans,
            grammar,
            zaojus,
            synonyms,
            antonyms
        )
    }

    data class WordMessage(
        val spell: String,
        val spellAudioUrl: String,
        val basicMean: String,
        val detailMean: ArrayList<String>,
        val grammar: String,
        val zaojuMean: ArrayList<String>,
        val synonyms: ArrayList<String>,
        val antonyms: ArrayList<String>
    )
}

fun main() {
    WordUtil("一成不变").find()
}