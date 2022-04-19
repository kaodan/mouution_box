package com.srcbox.file.ui.fragment.main_pager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.srcbox.file.R
import com.srcbox.file.adapter.MeData
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.UserTogetherData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.AboutActivity
import com.srcbox.file.ui.DiyFileTemplateActivity
import com.srcbox.file.ui.GetMemberActivity
import com.srcbox.file.ui.SettingActivity
import com.srcbox.file.ui.login.LoginActivity
import com.srcbox.file.ui.userinfo.UserInfoActivity
import com.srcbox.file.util.*
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_me.*
import java.lang.Exception

class FragmentMe : Fragment() {
    companion object {
        var meFragment: FragmentMe? = null
    }

    private var v: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        meFragment = this
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_me, container, false)
        this.v = v
//        initView()
        return v
    }

    @SuppressLint("SetTextI18n")
    fun initUser() {
        val userLoginOn = this.v?.findViewById<LinearLayout>(R.id.user_login_on)
//        val memberStateText = this.v?.findViewById<TextView>(R.id.member_state_text)
        val userMsg = this.v?.findViewById<LinearLayout>(R.id.user_msg)
        val logOut = this.v?.findViewById<LinearLayout>(R.id.log_out)
        val diyExtractFileOn = this.v?.findViewById<LinearLayout>(R.id.diy_extract_file_on)
        val meMessage = this.v?.findViewById<LinearLayout>(R.id.meMessage)


        if (AVUser.currentUser() != null) {
            userLoginOn?.visibility = View.GONE
            userMsg?.visibility = View.VISIBLE
        } else {
            userLoginOn?.visibility = View.VISIBLE
            userMsg?.visibility = View.GONE
        }

        logOut?.setOnClickListener {
            AVUser.logOut()
            userLoginOn?.visibility = View.VISIBLE
            userMsg?.visibility = View.GONE
        }
        userLoginOn?.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }


        diyExtractFileOn?.setOnClickListener {
            startActivity(Intent(activity, DiyFileTemplateActivity::class.java))
        }

        meMessage?.setOnClickListener {
            startActivity(Intent(activity, UserInfoActivity::class.java))
        }
    }

    private fun initTheme() {
        try {
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.img_about)
            )

            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.qqLoginImg)
            )
            /*EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.img_member)
            )*/
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.img_money)
            )
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.diy_extract_file_icon)
            )
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.img_join_qun)
            )
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById<TextView>(R.id.meMessageIcon)
            )


            GlobUtil.changeTheme(this.v!!.findViewById(R.id.user_login_on_text))
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.img_log_out)
            )
            EggUtil.loadIcon(
                requireActivity(),
                AppSetting.colorStress,
                this.v!!.findViewById(R.id.img_setting)
            )
        } catch (e: Exception) {
        }

        initUser()
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    private fun initView() {
        val dataRecyclerView = this.v?.findViewById<RecyclerView>(R.id.data_recycler_view)
        val titleCardV = this.v?.findViewById<LinearLayout>(R.id.user_message_info)
        val elseAbout = this.v?.findViewById<LinearLayout>(R.id.else_about_on)
        val elseSetting = this.v?.findViewById<LinearLayout>(R.id.else_setting_on)
        val getMemberOn = this.v?.findViewById<LinearLayout>(R.id.get_member_on)
        val joinQunOn = this.v?.findViewById<LinearLayout>(R.id.join_qun_on)
        val qqLogin = this.v?.findViewById<LinearLayout>(R.id.qqLogin)
        val qqLoginText = this.v?.findViewById<TextView>(R.id.qqLoginText)



        /*AVUser.currentUser().fetchIfNeededInBackground().subscribe(object :Observer<AVObject>{
            override fun onComplete() {
                println("刷新完成")
            }

            override fun onSubscribe(d: Disposable) {
                println("刷新关联$d")
            }

            override fun onNext(t: AVObject) {
                println("刷新成功$t")
            }

            override fun onError(e: Throwable) {
                println("刷新发送错误${e.message}")
            }

        })*/
/*
        titleCardV?.shadowColor = Color.parseColor(AppSetting.colorWhiteShadow)
        titleCardV?.cornerRadius = 25
        titleCardV?.shadowAlpha = 100
        titleCardV?.cardBackgroundColor = Color.parseColor("#ffffff")
        titleCardV?.setShadowFluidShape(LCardView.ADSORPTION)*/

        val arrayList = ArrayList<UserTogetherData>()
        arrayList.add(UserTogetherData("11", "个"))
        arrayList.add(UserTogetherData("10", "个"))
        arrayList.add(UserTogetherData("17", "个"))
        arrayList.add(UserTogetherData("12", "个"))
        arrayList.add(UserTogetherData("19", "个"))
        dataRecyclerView?.layoutManager = GridLayoutManager(activity, 3)
        dataRecyclerView?.adapter = MeData(arrayList)




        elseAbout?.setOnClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
        }

        elseSetting?.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }

        getMemberOn?.setOnClickListener {
            startActivity(Intent(activity, GetMemberActivity::class.java))
        }

        joinQunOn?.setOnClickListener {
            EggUtil.joinQQGroup(activity as Context, AppSetting.QUN_KEY)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        println("fragmentMe onDestroy")
    }

    override fun onResume() {
        super.onResume()
        initView()
    }


    override fun onStop() {
        super.onStop()
        println("fragmentMe Stop")
    }

    override fun onStart() {
        super.onStart()
        initTheme()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
    }


}