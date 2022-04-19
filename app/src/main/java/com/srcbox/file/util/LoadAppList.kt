package com.srcbox.file.util

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.srcbox.file.data.UserAppData
import java.io.File

class LoadAppList(val context: Activity) {
    //    private val appListF = File(AppStorageData.appStorageData?.getAppConfigStorage(),"apptypeslist.json")
    companion object {
        var appInfoList = ArrayList<UserAppData>()
        const val START_STATE = 0
        const val END_STATE = 1
        var state = END_STATE
    }



    private fun loadAppList() {
        val packageManager = context.packageManager
        val applications =
            packageManager.getInstalledPackages(0)

        applications.forEach {
            appInfoList.add(
                UserAppData(
                    File(it.applicationInfo.sourceDir),
                    it.applicationInfo.loadIcon(packageManager),
                    it.applicationInfo.loadLabel(packageManager).toString(),
                    (File(it.applicationInfo.sourceDir).length() / 1024).toInt(),
                    it.packageName,
                    it.firstInstallTime,
                    EggUtil.isOsApp(it.applicationInfo)
                )
            )
        }
    }

    fun getApps(): ArrayList<UserAppData> {
        state = START_STATE
        if (appInfoList.size == 0) {
            loadAppList()
        }
        state = END_STATE
        return appInfoList
    }
}


/*isC: Boolean = false*/
/*

 */
/*if (appListF.exists()) {
      println(EggIO.readFile(appListF))
  }*/

//        EggIO.writeFile(appListF, JSONArray.toJSONString(appInfoList.toArray()))


/*fun getSortMediaApp(jsonObject: JSONObject): ArrayList<UserAppData> {
    val typeName = "media"
    val jsonArray = jsonObject.getJSONArray(typeName)

    if (appInfoList.size == 0) {
        loadAppList()
    }
    var i = 0
    appInfoList.filter {
        val fName = jsonArray.get(i)
        i++
        fName == it.name
    }
    return appInfoList
}*/