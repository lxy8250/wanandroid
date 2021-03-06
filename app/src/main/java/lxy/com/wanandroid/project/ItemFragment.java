package lxy.com.wanandroid.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lxy.basemodel.base.Constants;
import com.lxy.basemodel.network.model.ArticleModel;
import com.lxy.basemodel.network.model.ResponseModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lxy.com.wanandroid.R;
import lxy.com.wanandroid.base.ToastUtils;
import com.lxy.basemodel.detail.ArticleDetailActivity;
import com.lxy.basemodel.detail.DetailModel;
import lxy.com.wanandroid.home.ArticleAdapter;
import lxy.com.wanandroid.network.NetworkManager;

/**
 * Creator : lxy
 * date: 2019/3/26
 */

public class ItemFragment extends Fragment {

    private RecyclerView rv;
    private ArticleAdapter adapter;
    private List<ArticleModel> list = new ArrayList<>();
    private int ocId;
    private int page = 0;
    private int totalPages = 0;
    private SwipeRefreshLayout refreshLayout;

    public static ItemFragment newInstance(int ocId) {

        Bundle args = new Bundle();
        args.putInt("ocid",ocId);
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_office_account,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null){
            ocId = getArguments().getInt("ocid");
        }
        initView(view);
        initListener();
        getDataByServer();
    }

    private void initView(View view) {
        rv = view.findViewById(R.id.office_account_rv);
        adapter = new ArticleAdapter(R.layout.item_home_article_image,list);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        adapter.bindToRecyclerView(rv);
        adapter.setEmptyView(R.layout.item_empty);
        refreshLayout = view.findViewById(R.id.office_account_swipe);
        refreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary));

    }

    private void initListener(){
        refreshLayout.setOnRefreshListener(() -> {
            page = 0;
            getDataByServer();
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                DetailModel model = new DetailModel();
                model.setName(list.get(position).getTitle());
                model.setLink(list.get(position).getLink());
                model.setId(list.get(position).getId());
                model.setCollect(list.get(position).isCollect());
                intent.putExtra("article",new Gson().toJson(model));
                startActivity(intent);
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                int total = adapter.getItemCount();
                if (lastVisibleItemPosition + Constants.ITEM_NUM >= total && page <= totalPages){
                    getDataByServer();
                }
            }
        });
    }

    private void getDataByServer(){
        NetworkManager.getManager().getServer().getProjectList(page, ocId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseModel model) {
                        if (model.getErrorCode() != 0){
                            ToastUtils.show(model.getErrorMsg());
                            return;
                        }
                        if (page == 0){
                            list.clear();
                        }
                        list.addAll(model.getData().getDatas());
                        totalPages = model.getData().getPageCount();
                        adapter.notifyDataSetChanged();
                        page++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("officeAccount",e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

}
