package com.srcbox.file.presenter

import android.content.Context
import com.srcbox.file.contract.ContractShitSmooth
import com.srcbox.file.model.ShitSmoothModel
import com.srcbox.file.util.EggUtil

class ShitSmoothPresenter(val view: ContractShitSmooth.View) : ContractShitSmooth.Presenter {
    private val shitSmoothModel = ShitSmoothModel()
    override fun makeShitSmooth() {
        if (view.getTitleV().isEmpty() || view.getLen() == 0L) {
            EggUtil.toast("参数不能为空")
        }
        view.resultShitSmooth(
            shitSmoothModel.getShitSmooth(
                view.getWordTab(),
                view.getTitleV(),
                view.getLen()
            )
        )
    }
}