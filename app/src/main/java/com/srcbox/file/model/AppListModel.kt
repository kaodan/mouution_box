package com.srcbox.file.model

import android.app.Activity
import android.content.Context
import com.srcbox.file.contract.AppListContract
import com.srcbox.file.data.UserAppData
import com.srcbox.file.ui.AppList
import com.srcbox.file.util.LoadAppList

class AppListModel : AppListContract.Model {
    override fun getInstallAppInfo(context: Context): ArrayList<UserAppData> {

        val loadAppList = LoadAppList(context as Activity)
        var inf = loadAppList.getApps()


        AppList.searchStr.trim()
        when (AppList.typeAppsMessage?.get(0).toString()) {
            "all" -> {
                inf = inf.filter {
                    (it.name.contains(
                        AppList.searchStr,
                        true
                    ) || it.appPackageName?.contains(AppList.searchStr, true)!!)
                } as ArrayList<UserAppData>
            }

            "os" -> {
                inf = inf.filter {
                    it.isOsApp && (it.name.contains(
                        AppList.searchStr,
                        true
                    ) || it.appPackageName?.contains(
                        AppList.searchStr
                        , true
                    )!!)
                } as ArrayList<UserAppData>
            }

            else -> {
                inf = inf.filter {
                    var b = false
                    if (it.appPackageName?.isNotEmpty()!!) {
                        if (AppList.typeAppsMessage!!.contains(it.appPackageName)) {
                            b =
                                true && (it.name.contains(
                                    AppList.searchStr,
                                    true
                                ) || it.appPackageName.contains(
                                    AppList.searchStr
                                    , true
                                ))
                        }
                    }
                    b
                } as ArrayList<UserAppData>
            }
        }

        return inf
    }
}


/* runOnUiThread {

      }*/
/*  if (AppList.typeAppsMessage?.get(0).toString() != "all") {

        }*/