package com.srcbox.file.presenter

import com.alibaba.fastjson.JSONArray
import com.srcbox.file.contract.MainContract
import com.srcbox.file.model.MainModel

class MainPresenter(val v: MainContract.View) : MainContract.Presenter {
    private val mainModel: MainContract.Model = MainModel()
    override fun getNetWorkAppTypes() {
        /*val joIt = mainModel.getNetWorkAppTypes()
        val appTypesJsonObject = joIt.getJSONObject("apptypes")
        val allJ = JSONArray()
        val osJ = JSONArray()
        allJ.add("all")
        osJ.add("os")
        appTypesJsonObject["全部\$\ue610"] =
            allJ
        appTypesJsonObject["系统\$\ue64c"] =
            osJ*/
    }

    override fun checkAppUpdateAndNotificationInfo() {

    }
}

/*
    val appTypesName = "appTypes.json"
    val version = 1
    val currentVersion = jsonObject.getInt("version")
    if (currentVersion > version) {}
*/