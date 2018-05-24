package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;
import com.agmbat.picker.tag.CategoryTag;
import com.agmbat.picker.tag.CategoryTagPickerView;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.TagText;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑兴趣爱好
 */
public class EditHobbyActivity extends Activity {

    @BindView(R.id.picker)
    CategoryTagPickerView mPickerView;

    /**
     * 保存button
     */
    @BindView(R.id.btn_save)
    Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_hobby);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        update(XMPPManager.getInstance().getRosterManager().getLoginUser());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 收到vcard更新信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginUserUpdateEvent event) {
        LoginUser user = event.getLoginUser();
        update(user);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击保存
     */
    @OnClick(R.id.btn_save)
    void onClickSave() {
        List<String> tagList = mPickerView.getCheckedList();
        String text = TagText.toTagText(tagList);
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        if (text.equals(user.getHobby())) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            user.setHobby(text);
            XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            finish();
        }
    }

    /**
     * 更新用户信息显示
     *
     * @param user
     */
    private void update(LoginUser user) {
        String text = AppResources.readAssetFile("wheelpicker/hobby_category.json");
        Type jsonType = new TypeToken<List<CategoryTag>>() {
        }.getType();
        List<CategoryTag> list = GsonHelper.fromJson(text, jsonType);
        List<String> checkedList = TagText.parseTagList(user.getHobby());
        cleanCheckedList(list, checkedList);
        mPickerView.setCategoryTagList(list);
        mPickerView.setMaxSelectCount(5);
        mPickerView.setCheckedList(checkedList);
        mPickerView.update();
    }

    /**
     * 将选中的tag中不在tag面板了tag移除
     *
     * @param list
     * @param checkedList
     */
    private static void cleanCheckedList(List<CategoryTag> list, List<String> checkedList) {
        List<String> removeList = new ArrayList<>();
        for (String tag : checkedList) {
            if (!checkTag(list, tag)) {
                removeList.add(tag);
            }
        }
        checkedList.removeAll(removeList);
    }

    private static boolean checkTag(List<CategoryTag> list, String tag) {
        for (CategoryTag categoryTag : list) {
            if (categoryTag.contains(tag)) {
                return true;
            }
        }
        return false;
    }

}
