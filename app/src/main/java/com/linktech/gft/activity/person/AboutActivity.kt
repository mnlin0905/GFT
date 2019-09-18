package com.linktech.gft.activity.person

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.LatestVersionBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.window.AlertCloseDialogFragment
import com.linktech.gft.window.ProgressDialog
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_about.*
import permissions.dispatcher.*
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Failed
import zlc.season.rxdownload3.core.Succeed
import java.io.File

/**
 * function---- AboutActivity
 *
 * Created(Gradle default create) by LinkTech on 2018/01/22 05:45:55 (+0000).
 */
@RuntimePermissions
@Route(path = ARouterConst.Activity_AboutActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_about)
@InjectActivityTitle(titleRes = R.string.label_about)
class AboutActivity : BaseActivity<BasePresenter<AboutActivity>>(), LineMenuListener {

    /**
     * 更新类
     */
    @Autowired(name = Const.MODEL_VERSION_UPDATE, required = false)
    @JvmField
    var dataBean: LatestVersionBean? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //设置版本号
        tv_version.text = getString(R.string.activity_about_version, AppUtils.getAppVersionName())

        //如果需要更新,则开始下载
        tv_version.post { dataBean?.let { checkFinish(it) } }
    }

    override fun performSelf(v: LineMenuView) {
        when (v.getTag(LMVConfigs.TAG_POSITION) as Int) {
            0 -> // 服务协议
                ARouter.getInstance()
                        .build(ARouterConst.Activity_CommonAgreementActivity)
                        .withString(Const.KEY_COMMON_VALUE, getString(R.string.common_paws_ecology_service_agreement))
                        .withInt(Const.KEY_COMMON_VALUE_FOUR, 2)
                        .navigation()
                        .empty(comment = "协议内容")
            1 -> // 版权信息
                ARouter.getInstance()
                        .build(ARouterConst.Activity_CommonAgreementActivity)
                        .withString(Const.KEY_COMMON_VALUE, getString(R.string.common_version_agreement))
                        .withString(Const.KEY_COMMON_VALUE_TWO, "")
                        .navigation()
                        .empty(comment = "版权信息")
            2 -> // 检查更新
                toast(R.string.activity_about_latest_version).empty {
                    mPresenter.findLatestVersion {
                        checkFinish(it)
                    }
                }
        }
    }

    /**
     * 最新版本
     */
    private fun checkFinish(bean: LatestVersionBean?) {
        bean?.let {
            dataBean = it
            it.version
        }?.apply {
            if (AppUtils.getAppVersionCode() < this) { //版本号比较小,则去下载
                AlertCloseDialogFragment()
                        .setConfirmText(getString(R.string.activity_about_download))
                        .setTitle(getString(R.string.activity_about_progress, this / 100, this / 10 % 10, this % 10, dataBean!!.updateTime))
                        .setOnAlertListener(object : AlertCloseDialogFragment.OnAlertListener {
                            override fun onClose(dialog: Dialog): Boolean {
                                //如果强制更新,则在关闭弹出框后需要结束整个应用
                                if (dataBean!!.forced == true) {
                                    ActivityUtils.finishAllActivities()
                                    toast(R.string.activity_about_exit)
                                }
                                return false
                            }

                            override fun onConfirm(dialog: Dialog): Boolean {
                                dataBean!!.downlink?.let {
                                    startDownloadWithPermissionCheck(it)
                                } ?: toast(R.string.activity_about_address_error)
                                return false
                            }
                        }).show(supportFragmentManager, "version")
            } else {
                toast(R.string.activity_about_latest_version)
            }
        } ?: toast(R.string.activity_about_get_version_error)
    }

    /**
     * 开启下载最新版本
     */
    @SuppressLint("CheckResult")
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun startDownload(url: String) {
        RxDownload.create(url, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single())
                .doOnSubscribe {
                    //如果开始时候文件已经存在,则直接安装
                    url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().let {
                        it[it.size - 1].run {
                            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this).exists().onTrue {
                                install(this)
                                throw RuntimeException(getString(R.string.activity_about_has_download))
                            }.onFalse {
                                //显示下载进度
                                ProgressDialog.getInstance(this@AboutActivity)
                                        .setMessage(getString(R.string.activity_about_please_wait))
                                        .setOnBackPress {
                                            toast(R.string.activity_about_downloading)
                                            true
                                        }
                                        .show()
                                toast(R.string.activity_about_downloading)
                            }
                        }
                    }
                }
                .subscribe({ status ->
                    Logger.e(getString(R.string.activity_about_progress_is, status))

                    if (status is Failed) {
                        toast(R.string.activity_about_download_fail)
                        ActivityUtils.finishAllActivities()
                        return@subscribe
                    }

                    //  下载的百分比
                    val percent = status!!.percent()
                    ProgressDialog.getInstance(this@AboutActivity)
                            .setMessage(getString(R.string.activity_about_progress_is, percent))
                            .setMessageBackgroundVisible(true)
                            .show()

                    //下载完成,状态变更,且进度为100%
                    if (status is Succeed && percent.replace("\\s".toRegex(), "").matches(".*100.*".toRegex())) {
                        toast(R.string.activity_about_install_ing)
                        val split = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        install(split[split.size - 1])
                    }
                }, {
                    Logger.e(it.message)
                }) {
                    Logger.e(getString(R.string.activity_about_down_install_finish))
                }
    }

    /**
     * 安装apk
     */
    private fun install(child: String) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), child)
        if (file.exists() && file.length() > 1024) {
            val intent = Intent(Intent.ACTION_VIEW)
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                val apkUri = FileProvider.getUriForFile(this, getString(R.string.file_provider), file)
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            }
            startActivity(intent)

            ProgressDialog.getInstance(this@AboutActivity).dismiss()
            ActivityUtils.finishAllActivities()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onShowRationale(request: PermissionRequest) {
        AlertCloseDialogFragment()
                .setTitle(getString(R.string.activity_about_you_should_known))
                .setConfirmText(getString(R.string.function_known))
                .setOnAlertListener {
                    request.proceed()
                    false
                }.show(supportFragmentManager, "download")
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onPermissionDenied() {
        toast(R.string.activity_about_permission_fail)
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onNeverAskAgain() {
        toast(R.string.activity_about_cannot_get_permission)
    }
}