package com.linktech.gft.activity.person


import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.R.id.et_nick_name
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.RegexConst
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.activity_change_nick_name.*
import skin.support.content.res.SkinCompatResources


/**
 * function---- ChangeNickNameActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 12:38:25 (+0000).
 */
@Route(path = ARouterConst.Activity_ChangeNickNameActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_change_nick_name)
@InjectMenuRes(menuResId = R.menu.menu_save_text)
@InjectActivityTitle(titleRes = R.string.label_change_nick_name)
class ChangeNickNameActivity : BaseActivity<BasePresenter<ChangeNickNameActivity>>() {

    private var nickName: String by viewBind(R.id.et_nick_name)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        nickName = _nickName ?: ""
        et_nick_name.addFaceFilter()
        iv_clear.visibility = if (nickName.isEmpty()) View.GONE else View.VISIBLE
        iv_clear.dOnClick { nickName = "" }
    }

    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        s.let {
            nickName = it.toString().trim()
            iv_clear.visibility = if (nickName.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //修改字体颜色
        SpannableString(dispatchGetString(R.string.function_save)).let {
            it.setSpan(ForegroundColorSpan(SkinCompatResources.getColor(this,R.color.skin_body1_color)), 0, it.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            menu.getItem(0).setTitle(it)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (verify()) {
            mPresenter.setNickname(_username, _signature, nickName) {
                mPresenter.getMemberInfo(_signature, _username) {
                    toast(dispatchGetString(R.string.activity_change_nick_name_success)).finish()
                }
            }
        }
        return true
    }

    /**
     * @return true表示验证通过
     */
    private fun verify(): Boolean {
        return when {
            nickName.isEmpty() -> false.toast(dispatchGetString(R.string.activity_change_nick_name_no))
            nickName notMatch RegexConst.REGEX_NICKNAME -> false.toast(dispatchGetString(R.string.activity_change_nick_name_error))
            else -> true
        }
    }
}