package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.HelpFoundBean;
import com.wutong.repair.data.bean.ResponseBean;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LikeableFoundActivity extends BaseActivity {
	private String outFindDate;
	private String outItemType;
	private String outPlaceId;
	
	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	
	private List<HelpFoundBean> mHelpFoundBeanList;
	private ListView mLikeableFoundListView;
	private LikeableFoundAdapter mLikeableFoundAdapter;
	
	private int currentStart = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_likeable_found);
		commonInit();
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}
	private void setupData(){
		mLikeableFoundAdapter.notifyDataSetChanged();
	}
	
	
	private void commonInit(){
		mHelpFoundBeanList = new ArrayList<HelpFoundBean>();
	}
	
	private void intentInit(){
		outPlaceId = getIntent().getStringExtra("placeId");
		outItemType = getIntent().getStringExtra("itemType");
		outFindDate = getIntent().getStringExtra("findDate");
		
		List<HelpFoundBean> tempList = (List<HelpFoundBean>) getIntent().getSerializableExtra("foundList");
		if(tempList != null){
			mHelpFoundBeanList.addAll(tempList);
		}
		else{
			ToastUtil.showToast(mContext, "哎呀，没有数据了");
		}
	}
	
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_likeable_found);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void viewInit(){
		mLikeableFoundListView = (ListView) findViewById(R.id.likeable_found_list);
		View emptyView = findViewById(R.id.likeable_found_empty);
		mLikeableFoundListView.setEmptyView(emptyView);
		mLikeableFoundAdapter = new LikeableFoundAdapter();
		mLikeableFoundListView.setAdapter(mLikeableFoundAdapter);
		mLikeableFoundListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View convertView, int position,
					long arg3) {
				if(position < mHelpFoundBeanList.size()){
					Intent intent = new Intent(mContext,HelpFoundDetailActivity.class);
					HelpFoundBean contributeBean = mHelpFoundBeanList.get(position);
					intent.putExtra("contributeId", contributeBean.getId());
					LikeableFoundActivity.this.startActivityForResult(intent, 0);
				}
				
			}
		});;
	}
	
	
	static class LikeableFoundViewHolder{
		TextView timeView;
		TextView remarkView;
		TextView placeView;
		ImageView coverView;
	}
	
	private class LikeableFoundAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mHelpFoundBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LikeableFoundViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_likeable_found, null);
				
				holder = new LikeableFoundViewHolder();
				holder.timeView = (TextView) convertView.findViewById(R.id.likeable_found_item_time);
				holder.placeView = (TextView) convertView.findViewById(R.id.likeable_found_item_place);
				holder.remarkView = (TextView) convertView.findViewById(R.id.likeable_found_item_remark);
				holder.coverView = (ImageView) convertView.findViewById(R.id.likeable_found_item_cover);
				
				
				convertView.setTag(holder);
			}
			else{
				holder = (LikeableFoundViewHolder) convertView.getTag();
			}
			HelpFoundBean helpFoundBean = mHelpFoundBeanList.get(position);
			holder.timeView.setText(helpFoundBean.getCreateTime());
			holder.placeView.setText(helpFoundBean.getPlaceName());
			holder.remarkView.setText(helpFoundBean.getContent());
			
			if(helpFoundBean.getCoverImage() !=null){
				application.getImageLoader().displayImage(helpFoundBean.getCoverImage().getUrl(), holder.coverView,application.getSimpleDisplayImageOptions());
				holder.coverView.setVisibility(ViewGroup.VISIBLE);
			}
			else{
				holder.coverView.setVisibility(ViewGroup.GONE);
			}
			
			return convertView;
		}
		
	}

}
