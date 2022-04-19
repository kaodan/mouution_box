package com.srcbox.file.contract

import com.alibaba.fastjson.JSONObject
import com.srcbox.file.base.BaseContract

interface MainContract {
    interface Model : BaseContract.Model {
        fun getAppUpdateAndNotificationInfo(): JSONObject
        fun getNetWorkAppTypes(): JSONObject
    }

    interface View : BaseContract.View {

    }

    interface Presenter : BaseContract.Presenter {
        fun getNetWorkAppTypes()
        fun checkAppUpdateAndNotificationInfo()
    }
}