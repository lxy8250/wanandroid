package com.lxy.basemodel.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.lxy.basemodel.R;
import com.lxy.basemodel.base.BaseActivity;
import com.lxy.basemodel.base.Constants;
import com.lxy.basemodel.network.BaseObserver;
import com.lxy.basemodel.network.NetworkManager;
import com.lxy.basemodel.network.model.ResponseModel;
import com.lxy.basemodel.utils.ToastUtils;
import com.wang.avi.AVLoadingIndicatorView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * date: 2019/1/25
 *
 * @author lxy
 */

public class ArticleDetailActivity extends BaseActivity {

    private WebView webView;
    private String url;
    private AVLoadingIndicatorView loadingView;
    private ImageView ivError;
    private boolean isError = false;
    private DetailModel model;


    @Override
    protected void initOptions() {
        initView();
        initListener();
    }


    @Override
    public int setContextView() {
        return R.layout.detail_activity_acticle;
    }

    private void initView() {
        showToolbarBack(true);
        webView = findViewById(R.id.activity_detail_web);
        loadingView = findViewById(R.id.detail_activity_loading);
        url = getIntent().getStringExtra("article");
        model = new Gson().fromJson(url, DetailModel.class);
        getSupportActionBar().setTitle(model.getName());
        webView.loadUrl(model.getLink());
        ivError = findViewById(R.id.detail_activity_error);
        initWebView();

        like = model.isCollect();
        invalidateOptionsMenu();


    }

    private void initListener() {
        toolbar.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.detail_love){
                collect();
            }else if (itemId == R.id.detail_no_love){
                unCollect();
            }else if (itemId == R.id.detail_share){
                StringBuffer buffer = new StringBuffer();
                buffer.append("这里有一篇好看的文章，")
                        .append(model.getName())
                        .append(model.getLink())
                        .append("快来一起玩Android吧！！！");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Share");
                i.putExtra(Intent.EXTRA_TEXT, buffer.toString());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(i, getTitle()));
            }else if (itemId == R.id.detail_other){
                Uri uri = Uri.parse(model.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            return true;
        });
    }

    private void collect() {
//        NetworkManager.getManager().getServer().collectArticleInSite(model.getId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseModel>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @SuppressLint("ResourceType")
//                    @Override
//                    public void onNext(ResponseModel model) {
//                        try {
//                            if (model.getErrorCode() != 0) {
//                                ToastUtils.show(R.string.login_yet);
//                                ARouter.getInstance().build(Constants.URL_LOGIN_ACTIVITY).navigation();
//                            } else {
//                                ToastUtils.show(R.string.collect_success);
//                                like = true;
//                                invalidateOptionsMenu();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    private void unCollect() {
//        NetworkManager.getManager().getServer().unCollectArticle(model.getId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<ResponseModel>() {
//                    @Override
//                    public void onSuccess(ResponseModel responseModel) {
//                        try {
//                            if (responseModel.getErrorCode() != 0) {
//                                ToastUtils.show(R.string.login_yet);
//                                ARouter.getInstance().build(Constants.URL_LOGIN_ACTIVITY).navigation();
//                            } else {
//                                ToastUtils.show(R.string.uncollect_success);
//                                like = false;
//                                invalidateOptionsMenu();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String message) {
//
//                    }
//                });
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            //或者
//            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        settings.setJavaScriptEnabled(true);
        // 自动加载图片
        settings.setLoadsImagesAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                webView.setVisibility(View.VISIBLE);
                loadingView.show();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingView.show();
                ivError.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingView.hide();
                if (isError) {
                    ivError.setVisibility(View.VISIBLE);
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.detail_menu);
        return super.onCreateOptionsMenu(menu);
    }

    boolean like;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (like) {
            menu.findItem(R.id.detail_love).setVisible(false);
            menu.findItem(R.id.detail_no_love).setVisible(true);
        } else {
            menu.findItem(R.id.detail_love).setVisible(true);
            menu.findItem(R.id.detail_no_love).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flContent.removeView(webView);
        webView.destroy();
    }
}
