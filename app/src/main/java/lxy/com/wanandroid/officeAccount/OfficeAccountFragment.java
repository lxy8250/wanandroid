package lxy.com.wanandroid.officeAccount;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lxy.basemodel.base.Constants;

import java.util.ArrayList;
import java.util.List;

import lxy.com.wanandroid.R;
import lxy.com.wanandroid.base.FragmentInterface;
import lxy.com.wanandroid.databinding.FragmentOfficeAccountBinding;
import com.lxy.basemodel.detail.ArticleDetailActivity;
import com.lxy.basemodel.detail.DetailModel;
import lxy.com.wanandroid.home.ArticleAdapter;

/**
 * Creator : lxy
 * date: 2019/3/18
 */

public class OfficeAccountFragment extends Fragment implements FragmentInterface{

    private ArticleAdapter adapter;
    private List<ArticleModel> list = new ArrayList<>();
    private int ocId;
    private int page = 0;
    private int totalPages = 0;
    private FragmentOfficeAccountBinding binding;
    private ActivityOfficeAccountViewModel viewModel;

    public static OfficeAccountFragment newInstance(int ocId) {

        Bundle args = new Bundle();
        args.putInt("officeAccountId",ocId);
        OfficeAccountFragment fragment = new OfficeAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_office_account, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(ActivityOfficeAccountViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null){
            ocId = getArguments().getInt("officeAccountId");
        }
        initView();
        initListener();
        getOfficeAccountArticle();
    }

    private void initView() {
        adapter = new ArticleAdapter(R.layout.item_home_article_image,list);
        binding.officeAccountRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.officeAccountRv.setAdapter(adapter);
        binding.officeAccountSwipe.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary));
        
    }

    private void initListener(){
        binding.officeAccountSwipe.setOnRefreshListener(() -> {
            page = 0;
            getOfficeAccountArticle();
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            DetailModel model = new DetailModel();
            model.setName(list.get(position).getTitle());
            model.setLink(list.get(position).getLink());
            model.setId(list.get(position).getId());
            model.setCollect(list.get(position).isCollect());
            intent.putExtra("article",new Gson().toJson(model));
            startActivity(intent);
        });

        binding.officeAccountRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                int total = adapter.getItemCount();
                if (lastVisibleItemPosition + Constants.ITEM_NUM >= total && page <= totalPages){
                    getOfficeAccountArticle();
                }
            }
        });
    }

    private void getOfficeAccountArticle(){
        viewModel.getOfficeAccountArticle(ocId,page).observe(this, model -> {
            binding.officeAccountSwipe.setRefreshing(false);
            if (page == 0){
                list.clear();
            }
            list.addAll(model.getDatas());
            totalPages = model.getPageCount();
            adapter.notifyDataSetChanged();
            page++;
        });
    }

    @Override
    public void smoothToTop() {
        binding.officeAccountRv.scrollToPosition(0);
    }
}
