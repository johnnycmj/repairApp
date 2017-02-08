package com.wutong.androidprojectlibary.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wutong.androidprojectlibary.R;

public class RefreshLoadMoreListView extends XListView {
	private View loadMoreView;
	private View loadMoreLoadingView;
	private View loadMoreForMoreView;
	private View loadMoreForNoMoreView;
	private final int LOAD_MORE = 1;
	private final int LOADING = 2;
	private final int NO_MORE = 3;
	private OnLoadMoreListener mOnLoadMoreListener;
	private OnClickListener mLoadMoreOnClickListener;

	public RefreshLoadMoreListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initContentView(context);
	}

	public RefreshLoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContentView(context);
	}

	public RefreshLoadMoreListView(Context context) {
		super(context);
		initContentView(context);
	}

	private void initContentView(Context context){
		loadMoreView = LayoutInflater.from(context).inflate(R.layout.footer_common_load_more, null);
		loadMoreLoadingView = loadMoreView.findViewById(R.id.loading);
		loadMoreForMoreView = loadMoreView.findViewById(R.id.loadmore);
		loadMoreForNoMoreView = loadMoreView.findViewById(R.id.nomore);
		this.addFooterView(loadMoreView);
		mLoadMoreOnClickListener =new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mOnLoadMoreListener !=null){
					showfooter(LOADING);
					mOnLoadMoreListener.onLoadMore();
				}

			}
		};
		loadMoreView.setOnClickListener(mLoadMoreOnClickListener);
		showfooter(LOAD_MORE);
	}

	/**
	 * 
	 * @param size data size from 
	 * @param limit data size per page
	 */
	public void finishLoadMore(int size,int limit){
		if(size >= limit){
			showfooter(LOAD_MORE);
		}
		else{
			showfooter(NO_MORE);
		}

	}

	public interface OnLoadMoreListener {
		public void onLoadMore();

	}

	/**
	 * footer的显示更改
	 * @param status
	 */
	private void showfooter(int status){
		switch (status) {
		case LOAD_MORE://1加载更多
			loadMoreForMoreView.setVisibility(ViewGroup.VISIBLE);
			loadMoreLoadingView.setVisibility(ViewGroup.GONE);
			loadMoreForNoMoreView.setVisibility(ViewGroup.GONE);
			loadMoreView.setOnClickListener(mLoadMoreOnClickListener);
			break;
		case LOADING://正在加载
			loadMoreForMoreView.setVisibility(ViewGroup.GONE);
			loadMoreLoadingView.setVisibility(ViewGroup.VISIBLE);
			loadMoreForNoMoreView.setVisibility(ViewGroup.GONE);
			break;
		case NO_MORE://加载完毕
			loadMoreForMoreView.setVisibility(ViewGroup.GONE);
			loadMoreLoadingView.setVisibility(ViewGroup.GONE);
			loadMoreForNoMoreView.setVisibility(ViewGroup.VISIBLE);
			loadMoreView.setOnClickListener(null);//去掉监听
			break;
		default:
			break;
		}
	}

	public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
		this.mOnLoadMoreListener = mOnLoadMoreListener;
	}
	/**
	 * 结合了finishLoadMore 和 stopRefresh 
	 * @author Jolly
	 *
	 */
	public void finishDataGet(boolean isRefresh,int size,int limit){
		if(isRefresh){
			this.stopRefresh();
		}
		this.finishLoadMore(size, limit);
	}

}
