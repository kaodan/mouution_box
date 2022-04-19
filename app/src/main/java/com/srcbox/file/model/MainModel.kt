package com.srcbox.file.model

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.srcbox.file.contract.MainContract
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import okhttp3.*
import java.io.File
import java.io.IOException

class MainModel : MainContract.Model {
    override fun getAppUpdateAndNotificationInfo(): JSONObject {
        TODO("Not yet implemented")
    }

    override fun getNetWorkAppTypes():JSONObject {
        return JSON.parseObject(EggIO.readFile(AppStorageData.getAssetsIn("json/apptypesx.json")))
    }
}