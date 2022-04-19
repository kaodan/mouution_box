package com.srcbox.file.contract

import android.content.Context
import com.srcbox.file.base.BaseContract
import com.srcbox.file.data.UserAppData

interface AppListContract {
    interface Model : BaseContract.Model {
        fun getInstallAppInfo(context: Context): ArrayList<UserAppData>
    }

    interface View : BaseContract.View {
        fun start()
        fun listApp(arrayList: ArrayList<UserAppData>)
        fun loading()
    }

    interface Presenter : BaseContract.Presenter {
        fun installAppInfo(sortType: Int)
    }
}