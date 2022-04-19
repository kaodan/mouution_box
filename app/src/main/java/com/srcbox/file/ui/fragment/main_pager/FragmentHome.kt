package com.srcbox.file.ui.fragment.main_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.srcbox.file.R
import com.srcbox.file.adapter.AppTypesAdapter
import com.srcbox.file.adapter.HomeUtilAdapter
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggIO

class FragmentHome : Fragment() {
    private var v: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_home, container, false)
        this.v = v
        initView()
        typeAppsList()
        return v
    }


    private fun typeAppsList() {
        val joIt =
            JSON.parseObject(EggIO.readFile(AppStorageData.getAssetsIn("json/apptypes.json")))
        val appTypesJsonObject = joIt.getJSONObject("apptypes")
        val allJ = JSONArray()
        val osJ = JSONArray()
        allJ.add("all")
        osJ.add("os")
        appTypesJsonObject["全部\$\ue610"] =
            allJ
        appTypesJsonObject["系统\$\ue64c"] =
            osJ

        val homeFunRecyclerV = this.v?.findViewById<RecyclerView>(R.id.home_fun_recycler_v)
        homeFunRecyclerV?.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        homeFunRecyclerV?.adapter = AppTypesAdapter(
            requireContext(),
            appTypesJsonObject
        )
    }

    private fun initView() {
        val homeUtilList = this.v?.findViewById<RecyclerView>(R.id.home_util_list)
        homeUtilList?.layoutManager = GridLayoutManager(context, 2)
//        val ham = HashMap<String, String>()
        homeUtilList?.adapter = HomeUtilAdapter(
            requireContext(),
            arrayOf(
                "屏幕资源采集\$false",
                "网页图片采集\$false",
                "资源嗅探\$false",
                "悬浮取色\$false",
                "可视资源采集\$false",
                "网站源码打包\$false"
            )
        )
    }
}

/*
 val il = AppTypes.appTypesData?.iterator()
 println(il?.next())
 println(il?.next())*/
/*AppTypes.appTypesData?.forEach {
    println(it.key)
}
*/

/*
        val screenGather = this.v?.findViewById<LCardView>(R.id.screen_gather)
        val webResourceGetOn = this.v?.findViewById<LCardView>(R.id.web_resource_get_on)
        if (screenGather != null) {
            GlobUtil.changeTheme(screenGather)
        }

        webResourceGetOn?.setOnClickListener {
//            startActivity(Intent(context, GetWebResource::class.java))
        }
        screenGather?.setOnClickListener {

        }*/

//        val homeUtilRecyclerV = this.v?.findViewById<RecyclerView>(R.id.home_util_fun_recycler_v)
/*homeUtilRecyclerV?.layoutManager =
    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
homeUtilRecyclerV?.adapter =
    MainHomeFunAdaoter(
        activity as Context,
        JSONObject(EggIO.readFile(activity?.assets!!.open("json/main_home_util_fun.json")))
    )*/