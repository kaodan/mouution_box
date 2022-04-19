package com.srcbox.file.presenter

import android.content.Context
import com.srcbox.file.contract.AppListContract
import com.srcbox.file.model.AppListModel
import com.srcbox.file.ui.AppList

class AppListPresenter(val v: AppListContract.View) : AppListContract.Presenter {
    private val appListModel = AppListModel()
    private var t: Thread? = null
    override fun installAppInfo(sortType: Int) {
        v.start()
        t = Thread {
            v.loading()
            val arrL = appListModel.getInstallAppInfo(v as Context)
            when (sortType) {
                AppList.sortSizeType -> {
                    arrL.sortByDescending { it.appSize }
                }

                AppList.sortTimeType -> {
                    arrL.sortByDescending { it.appInstallTime }
                }
            }
            v.listApp(arrL)
        }
        t?.start()
    }

}