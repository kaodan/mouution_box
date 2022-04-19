package com.srcbox.file.ui.popup

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.adapter.ChangeThemeItem
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.shit_smooth.view.*

class ChangeThemePopup(context: Context) : BottomPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        val l = findViewById<RecyclerView>(R.id.theme_list)
        l.layoutManager = LinearLayoutManager(context)
        l.adapter =
            ChangeThemeItem(
                this,
                JSON.parseArray(EggIO.readFile(context.assets.open("json/themes.json")))
            )
        l.setHasFixedSize(true)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.change_theme_popup
    }
}