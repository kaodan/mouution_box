package com.srcbox.file.util

import cn.leancloud.AVUser
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observer

object LeanQQUtil {
    fun result(
        json: JSONObject,
        isAssociate: Boolean,
        iUiListener: Observer<AVUser>
    ) {
        val thirdPartyData = java.util.HashMap<String, Any>()
        thirdPartyData["openid"] = json.getString("openid")
        thirdPartyData["expires_in"] = json.getString("expires_in")
        thirdPartyData["access_token"] = json.getString("access_token")
        if (isAssociate) {
            println(json)
            AVUser.currentUser().associateWithAuthData(thirdPartyData, "qq").subscribe(iUiListener)
        } else {
            AVUser.loginWithAuthData(thirdPartyData, "qq").subscribe(iUiListener)
        }
//        user.loginWithAuthData(thirdPartyData, "qq", true).subscribe(iUiListener)
    }
}