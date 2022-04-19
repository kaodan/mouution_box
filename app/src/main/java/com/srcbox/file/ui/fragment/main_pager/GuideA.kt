package com.srcbox.file.ui.fragment.main_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.util.EggUtil

class GuideA : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = LayoutInflater.from(context).inflate(R.layout.guide_page_a, container, false)
        Glide.with(this).load(R.mipmap.guide_p1).into(v.findViewById(R.id.guide_p1))
        EggUtil.setViewRadius(v, "#ffffff", 1, "#ffffff", EggUtil.dp2px(EggApplication.context,8f).toFloat())
        v.setOnClickListener {
            EggUtil.toast("请左右滑动")
        }
        return v
    }
}