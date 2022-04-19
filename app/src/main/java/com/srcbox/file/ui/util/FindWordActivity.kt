package com.srcbox.file.ui.util

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.util.WordUtil
import kotlinx.android.synthetic.main.activity_find_word.*
import java.lang.Exception
import kotlin.concurrent.thread

class FindWordActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_word)
        GlobUtil.changeTitle(this)
        val word = intent.getStringExtra("word")
        wordText.text = word

        EggUtil.loadIcon(this, "#2196f3", audioIcon)



        val asL = XPopup.Builder(this).asLoading()
        asL.show()
        thread {
            try {
                val wordMsg = word?.let { WordUtil(it).find() }
                runOnUiThread {
                    asL.dismiss()
                    if (wordMsg!!.basicMean.isEmpty()) {
                        basicMean.visibility = View.GONE
                    }


                    if (wordMsg.antonyms.isEmpty()) {
                        antonyms.visibility = View.GONE
                    }

                    if (wordMsg.synonyms.isEmpty()) {
                        synonyms.visibility = View.GONE
                    }

                    if (wordMsg.zaojuMean.isEmpty()) {
                        zaojuMean.visibility = View.GONE
                    }

                    if (wordMsg.detailMean.isEmpty()) {
                        detailMean.visibility = View.GONE
                    }

                    if (wordMsg.grammar.isEmpty()) {
                        grammar.visibility = View.GONE
                    }



                    basicMeanContent.text = wordMsg.basicMean


                    audio.setOnClickListener {
                        if (wordMsg.spellAudioUrl.isEmpty()){
                            return@setOnClickListener
                        }
                        val mediaPlayer = MediaPlayer()
                        mediaPlayer.setDataSource(wordMsg.spellAudioUrl)
                        mediaPlayer.prepare()
                        mediaPlayer.start()


                    }
                    detailMeanContent.apply {
                        wordMsg.detailMean.forEach {
                            text = "$text$it\n"
                        }
                    }

                    antonymsContent.apply {
                        wordMsg.antonyms.forEach {
                            text = "$text$it   "
                        }
                    }

                    synonymsContent.apply {
                        wordMsg.synonyms.forEach {
                            text = "$text$it   "
                        }
                    }

                    grammarContent.text = wordMsg.grammar
                    spell.text = wordMsg.spell
//                zaojuMeanContent.text = wordMsg.zaojuMean

                    zaojuMeanContent.apply {
                        wordMsg.zaojuMean.forEach {
                            text = "$text$it\n"
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(spell,"查词失败",Snackbar.LENGTH_LONG).show()
            }

        }
    }
}