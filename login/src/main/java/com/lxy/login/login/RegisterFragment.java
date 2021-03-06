package com.lxy.login.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.lxy.basemodel.network.model.LoginModel;
import com.lxy.basemodel.network.BaseObserver;
import com.lxy.basemodel.network.NetworkManager;
import com.lxy.basemodel.network.RxHelper;
import com.lxy.basemodel.utils.ToastUtils;
import com.lxy.login.R;

/**
 * Creator : lxy
 * date: 2019/2/27
 */

public class RegisterFragment extends Fragment {

    private static String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etREPassword;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        initListener();
        return view;
    }

    private void initView(View view) {
        etUsername = view.findViewById(R.id.register_et_username);
        etPassword = view.findViewById(R.id.register_et_password);
        etREPassword = view.findViewById(R.id.register_et_repassword);
        btnLogin = view.findViewById(R.id.register_btn);
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String name = etUsername.getText().toString().trim();
        String pswd = etPassword.getText().toString().trim();
        String repswd = etREPassword.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pswd) || TextUtils.isEmpty(repswd)) {
            ToastUtils.show(R.string.login_toast);
            return;
        }
        NetworkManager.getManager().getServer(LoginAPI.class).register(name, pswd, repswd)
                .compose(RxHelper.observableIO2Main())
                .subscribe(new BaseObserver<LoginModel>() {
                    @Override
                    public void onSuccess(LoginModel model) {
                        ToastUtils.show(R.string.login_success);
                        model.setPassword(pswd);
                        LoginUtil.getInstance().setLoginInfo(getContext(),new Gson().toJson(model));
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.show(message);
                    }
                });
    }
}
