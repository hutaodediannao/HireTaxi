package com.hiretaxi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.fragment.HomeFragment;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.util.SPUtils;
import com.hiretaxi.util.UserPermissionCheck;
import com.hiretaxi.view.BaseToolbar;

import java.nio.charset.Charset;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.hiretaxi.fragment.MeFragment.NICK_KEY;

public class UserInfoActivity extends ToolbarBaseActivity {

    private TextView spinner;
    String[] data = new String[]{"经济型", "舒适型", "商务型", "豪华型", "出租车"};
    String[] carAgeArray = new String[]{"准新车", "一年以内", "三年以内", "五年以内", "十年以内", "十年以外"};
    private EditText etNname, etSex, etPhoneNumber, etSoSPhoneNumber,
            etIdCard, etCarAuthen, etCarNumber, etCarInsurance, etCarStatu;
    private String nickName, sex, callNumber, callSoSNumber, idCardNumber,
            ahthenCardNumber, carNumber, carInsurance, carStatu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setDefacultTitle("用户信息");

        initView();
        setListener();
        updateUI();
    }

    private void updateUI() {
        CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
        if (carUser == null) {
            return;
        }

        String nickName = Constant.DEFACULT_NICK_NAME;
        if (carUser.getNickName() != null) {
            nickName = carUser.getNickName();
        } else {
            nickName = (String) SPUtils.get(this, NICK_KEY, "");
            if (nickName.isEmpty()) {
                nickName = "这个人很懒，没有写昵称";
            } else {
                nickName = new String(nickName.getBytes(Charset.forName("ISO-8859-1")));
            }
        }
        setTextView(etNname, nickName);

        String sex = Constant.DEFACULT_SEX;
        if (carUser.getSex() != null) {
            sex = carUser.getSex();
        }
        setTextView(etSex, sex);

        setTextView(etPhoneNumber, carUser.getMobilePhoneNumber());
        setTextView(etSoSPhoneNumber, carUser.getSosPhoneNumber());
        setTextView(etCarAuthen, carUser.getDriverLicense());

        setTextView(etCarNumber, carUser.getIdCardNumber());
        setTextView(etIdCard, carUser.getCarIdCardNumber());

        setTextView(etCarInsurance, carUser.getCarInsurance());
        setTextView(etCarStatu, carUser.getCarStatu());
        setTextView(spinner, carUser.getCarType());

    }

    //提交修改用户信息
    private void uploadUserMsg() {
        nickName = etNname.getText().toString();
        sex = etSex.getText().toString();
        callNumber = etPhoneNumber.getText().toString();
        callSoSNumber = etSoSPhoneNumber.getText().toString();
        idCardNumber = etIdCard.getText().toString();
        ahthenCardNumber = etCarAuthen.getText().toString();
        carNumber = etCarNumber.getText().toString();
        carInsurance = etCarInsurance.getText().toString();
        carStatu = etCarStatu.getText().toString();

        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            return;
        }

        final CarUser newCarUser = new CarUser(nickName, sex, carInsurance, ahthenCardNumber,
                carNumber, callSoSNumber, carStatu, idCardNumber);
        newCarUser.setMobilePhoneNumber(callNumber);

        final CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
        if (carUser == null) {
            return;
        }

        boolean checkUserPermission = UserPermissionCheck.checkUserContent2(this, newCarUser);
        if (!checkUserPermission) {
            return;
        }

        loadDialog.setTitleText("正在提交用户资料...");
        loadDialog.show();
        newCarUser.update(carUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadDialog.dismiss();
                if (e == null) {
                    showToast("提交成功！");
                    HomeFragment.NEED_REFRESH = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (carUser.getValidateOk() == null || !carUser.getValidateOk().booleanValue()) {
                                start(VerifyActivity.class);
                            }
                            finish();
                        }
                    }, 1000);
                } else {
                    if (e.getErrorCode() == 301 && e.getMessage().equals("mobilePhoneNumber Must be valid mobile number")) {
                        showToast("手机号码格式不对");
                    } else {
                        showToast("提交失败" + e.getErrorCode());
                    }
                }
            }
        });
    }

    private static final String TAG = "UserInfoActivity";

    private Handler handler = new Handler();

    private void setListener() {
        ((BaseToolbar) toolbarView).setToolbarClickListener(new BaseToolbar.ToolbarClickListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
                CarUser carUser = CarUser.getCurrentUser(CarUser.class);
                if (carUser == null) {
                    showToast("请登录");
                    return;
                }
                if (carUser.getValidateOk()  != null && carUser.getValidateOk().booleanValue()) {
                    showToast("您已经通过验证，无需重新提交");
                    return;
                }
                uploadUserMsg();
            }
        });
    }

    private void initView() {
        etNname = findView(R.id.etName);
        etSex = findView(R.id.etSex);
        etIdCard = findView(R.id.etIdCard);
        etCarAuthen = findView(R.id.etCarAuthen);
        etCarNumber = findView(R.id.etCarNumber);
        etPhoneNumber = findView(R.id.etPhonenumber);
        etSoSPhoneNumber = findView(R.id.etSosPhoneNumber);
        etCarInsurance = findView(R.id.etCarInsurance);
        etCarStatu = findView(R.id.etCarStatu);

        spinner = (TextView) findViewById(R.id.spinnerCarType);
        ((BaseToolbar) toolbarView).setViewVisibity(true, true, true);
        ((BaseToolbar) toolbarView).setIvRight(R.drawable.ic_send_black_24dp);
    }

    @Override
    protected View getToolbarView() {
        return null;
    }
}
