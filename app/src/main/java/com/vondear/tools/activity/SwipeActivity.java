package com.vondear.tools.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.vondear.rxtools.activity.ActivityBase;
import com.vondear.rxtools.view.RxTitle;
import com.vondear.rxtools.view.sidebar.PinnedHeaderDecoration;
import com.vondear.tools.R;
import com.vondear.tools.adapter.AdapterContactCity;
import com.vondear.tools.adapter.MyAdapter;
import com.vondear.tools.bean.ModelContactCity;
import com.vondear.tools.tools.ComparatorLetter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeActivity extends ActivityBase {

  int currentIndex = 0;

  Handler handler = new Handler();

  @BindView(R.id.rx_title) RxTitle rxTitle;
  @BindView(R.id.xrecyclerview) XRecyclerView xrecyclerview;
  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.iv_state) ImageView ivState;
  @BindView(R.id.tv_state_tips) TextView tvStateTips;
  @BindView(R.id.state_container) RelativeLayout stateContainer;
  @BindView(R.id.recycler_view_container) FrameLayout recyclerViewContainer;

  MyAdapter mAdapterContactCity;

  ArrayList<ModelContactCity> list;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_swipe);
    ButterKnife.bind(this);
    initView();

    Type listType = new TypeToken<ArrayList<ModelContactCity>>() {
    }.getType();
    Gson gson = new Gson();
    list = gson.fromJson(ModelContactCity.DATA, listType);

    Collections.sort(list, new ComparatorLetter());

    initData();
  }

  private void initData() {
    swipeRefreshLayout.setRefreshing(true);
    xrecyclerview.setLoadingMoreEnabled(false);

    handler.postDelayed(new Runnable() {
      @Override public void run() {
        mAdapterContactCity = new MyAdapter(list.subList(0, 10));
        xrecyclerview.setAdapter(mAdapterContactCity);
        currentIndex = 10;
        swipeRefreshLayout.setRefreshing(false);
        xrecyclerview.setLoadingMoreEnabled(true);
      }
    }, 2000);
  }

  private void initView() {
    rxTitle.setLeftFinish(mContext);

    xrecyclerview.setLayoutManager(new LinearLayoutManager(this));

    final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
    decoration.registerTypePinnedHeader(1, new PinnedHeaderDecoration.PinnedHeaderCreator() {
      @Override public boolean create(RecyclerView parent, int adapterPosition) {
        return true;
      }
    });
    xrecyclerview.addItemDecoration(decoration);

    xrecyclerview.setPullRefreshEnabled(false);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        initData();
      }
    });

    xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override public void onRefresh() {

      }

      @Override public void onLoadMore() {

        swipeRefreshLayout.setEnabled(false);

        handler.postDelayed(new Runnable() {
          @Override public void run() {
            mAdapterContactCity.notifyData(list.subList(currentIndex, currentIndex + 10));
            currentIndex += 10;
            swipeRefreshLayout.setEnabled(true);
            xrecyclerview.loadMoreComplete();
          }
        }, 2000);
      }
    });
  }
}
