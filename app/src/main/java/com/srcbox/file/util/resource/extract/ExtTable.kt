package com.srcbox.file.util.resource.extract

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSON.toJSONString
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.UserAppData
import com.srcbox.file.util.EggIO
import java.io.File

object ExtTable {
    private const val extHeadExtractName = "json/extTable/extHead.json"
    private const val extExtractMsgName = "json/extTable/extExtractMsg.json"
    private const val relativeExtMsgName = "json/extTable/relativeExtMsg.json"
    private const val extExtractMsgAppName = "json/extExtractMsg.json"
    private val extExtractsFileP = File(AppStorageData.getFileOutFile(), "json/table")

    /*fun getExtExtractsFile():File {
        extExtractsFileP.let {
            if (!it.exists()){
                it.mkdirs()
            }
        }
        println(extExtractsFileP)
        return extExtractsFileP
    }*/

    fun getExtHeadJson(): JSONObject {
        return JSON.parseObject(
            EggIO.readFile(
                EggApplication.context.assets.open(
                    extHeadExtractName
                )
            )
        )
    }

   /* private fun getExtExtractMsg(position: Int): JSONObject {
        val f = File(getExtExtractsFile(), "${position}.json")
        return JSON.parseObject(EggIO.readFile(f))
    }*/

    fun getRelativeExtMsg(): JSONObject {
        return JSON.parseObject(
            EggIO.readFile(
                EggApplication.context.assets.open(
                    relativeExtMsgName
                )
            )
        )
    }

   /* private fun getWriteJsonFile(position: Int): File {
        return File(getExtExtractsFile(), "${position}.json")
    }*/

   /* private fun writeJsonFile(position: Int, string: String) {
        val appF = getWriteJsonFile(position)
        EggIO.writeFile(appF, string)
    }

    private fun delJsonFile(position: Int) {
        val appF = getWriteJsonFile(position)
        appF.delete()
    }

    private fun getFinallyFileNum(): Int {
        val extExtractsFileList = getExtExtractsFile().list()
        var i = 0
        extExtractsFileList!!.forEach { _ ->
            i++
        }
        return i
    }

    fun alterExtExtractMsg(position: Int, key: String, value: String) {
        val json = getExtExtractMsg(position)
        println(json)
        json[key] = value
        writeJsonFile(position, json.toJSONString())
    }*/

   /* fun addExtExtractMsg(userAppData: UserAppData) {
        val enJson = JSONObject()
        enJson["filePackage"] = userAppData.appPackageName
        enJson["thisEntry"] = 0
//        enJson["version"] = userAppData.appVer
        enJson["state"] = ResourceData.START_STATE
        writeJsonFile(getFinallyFileNum(), enJson.toJSONString())
    }

    fun removeExtExtractMsg(position: Int) {
        delJsonFile(position)
    }*/
}



//    private var appF = File("")

//        val json = getExtExtractMsg()
//        val json = getExtExtractMsg(position)

/*appF = File(AppStorageData.getFileOutFile(),"cc.json")
if (!appF.exists()){
    EggIO.assetsFileToSdPath(AppStorageData.applicationContext!!, extExtractMsgName, appF)
}
//        AppStorageData.applicationContext!!.assets.open(relativeExtMsgName)
val r = EggIO.readFile(appF)
var jsa = JSONArray()
println(r)
try {
    jsa = JSON.parseArray(r)
}catch (e:JSONException){
    println(e.message)
    jsa = JSON.parseArray(EggIO.readFile(AppStorageData.applicationContext!!.assets.open(extExtractMsgName)))
}*/


//        EggIO.writeFile(File(extExtractMsg), json.toJSONString())
