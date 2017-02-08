package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.ComplaintDetailActivity;
import com.wutong.repair.activity.EditInfoActivity;
import com.wutong.repair.activity.ExtraModularActivity;
import com.wutong.repair.activity.HelpFoundDetailActivity;
import com.wutong.repair.activity.NoticeDetailActivity;
import com.wutong.repair.activity.PhotoImageShowActivity;
import com.wutong.repair.activity.PushMessageListActivity;
import com.wutong.repair.activity.RepairOrderActivity;
import com.wutong.repair.activity.RepairOrderDetailActivity;
import com.wutong.repair.data.bean.HomePageBean;
import com.wutong.repair.data.bean.HomeProgressBean;
import com.wutong.repair.data.bean.PhotoBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;

public class HomePageFragment extends BaseFragment {

	private final static int REQUEST_CODE_COMPLAINT_INFO = 9899;
	private final static int REQUEST_CODE_REPAIR_INFO = 9866;
	private final static int REQUEST_CODE_NOTICE_INFO = 9855;
	private final static int REQUEST_CODE_PUBLISH_REPAIR_ORDER = 9844;
	private final static int REQUEST_CODE_HELP_FOUND = 9833;
	private final static int REQUEST_CODE_PUSH_MESSAGE = 9834;

	private TextView titlebarTitleView;
	
	private ImageView titlebarPushMsgView;

	private ImageView mPicturePerDayIv;//每日一图
	private ListView mProgressLv;//进展，动态（维修中报修单、已完成报修单、建议被回复、最新公告）
	private ProgressBaseAdapter mProgressBaseAdapter;
	private List<HomeProgressBean> mHomeProgressBeanList;

	private ImageView mPublishRepairOrderIv;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		dataInit();
		argumentInit();
		titlebarInit();
		viewInit();
		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupData();
	}

	private void dataInit(){
		mHomeProgressBeanList = new ArrayList<HomeProgressBean>();
	}

	private void argumentInit(){

	}

	private void titlebarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(getString(R.string.title_fragment_home_page));
		
		titlebarPushMsgView = (ImageView) findViewById(R.id.titlebar_push_msg);
		//Fix分支，已删除代码
		titlebarPushMsgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ExtraModularActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.fragment_label_push_category_list));
				startActivity(intent);
				titlebarPushMsgView.setImageResource(R.drawable.icon_push_msg_normal_layer);
			}
		});
	}

	private void viewInit(){
		setFragmentPageName(mContext, R.string.title_fragment_home_page);
		mPublishRepairOrderIv = (ImageView) findViewById(R.id.home_page_publish_repair_order);
		mPicturePerDayIv = (ImageView) findViewById(R.id.home_page_picture_per_day);
		mProgressLv = (ListView) findViewById(R.id.home_page_progress_list);
		mProgressBaseAdapter = new ProgressBaseAdapter();
		mProgressLv.setAdapter(mProgressBaseAdapter);

		mPublishRepairOrderIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(phoneInvalid()){
					return;
				}
				else{
					Intent intent = new Intent();
					intent.putExtra("repairType", "1");
					intent.putExtra("isFromIndex", true);
					intent.setClass(mContext, RepairOrderActivity.class);
					HomePageFragment.this.getActivity().startActivityForResult(intent,REQUEST_CODE_PUBLISH_REPAIR_ORDER);
				}
			}
		});
		mProgressLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View convertView, int position,
					long arg3) {
				if(position < mHomeProgressBeanList.size()){
					HomeProgressBean homeProgressBean = mHomeProgressBeanList.get(position);
					Intent intent;
					switch (homeProgressBean.getType()) {
					case HomeProgressBean.REPAIR_INFO:
						String repairOrderId = homeProgressBean.getMessageId();
						String repairType = homeProgressBean.getFormType();
						if(repairOrderId == null || repairOrderId.trim().length() == 0
								||repairType == null || repairType.trim().length() == 0){
							ToastUtil.showToast(mContext, "获取的数据异常，无法进入");
							return;
						}
						intent = new Intent();
						Bundle bundle = new Bundle();
						intent.putExtra("repairOrderId", repairOrderId);
						intent.putExtra("repairType", repairType);
						intent.setClass(mContext, RepairOrderDetailActivity.class);
						intent.putExtra("isFromIndex", true);
						intent.putExtra("isLargeCharacter", false);
						intent.putExtras(bundle);
						HomePageFragment.this.getActivity().startActivityForResult(intent,REQUEST_CODE_REPAIR_INFO);
						sendIsReadAsyncHttp(homeProgressBean.getIndexMessageId());
						break;
					case HomeProgressBean.NOTICE_INFO:
						String noticeId = homeProgressBean.getMessageId();
						intent = new Intent(mContext,NoticeDetailActivity.class);
						intent.putExtra("noticeId", noticeId);
						intent.putExtra("isRead", true);//默认不发送已读信息修改
						HomePageFragment.this.getActivity().startActivityForResult(intent, REQUEST_CODE_NOTICE_INFO);
						sendIsReadAsyncHttp(homeProgressBean.getIndexMessageId());
						break;
					case HomeProgressBean.COMPLAINT_INFO:
						String complaintId = homeProgressBean.getMessageId();
						intent = new Intent(mContext, ComplaintDetailActivity.class);
						intent.putExtra("complaintId", complaintId);
						HomePageFragment.this.getActivity().startActivityForResult(intent,REQUEST_CODE_COMPLAINT_INFO);
						sendIsReadAsyncHttp(homeProgressBean.getIndexMessageId());
						break;
					case HomeProgressBean.HELP_FOUND:
						intent = new Intent(mContext, HelpFoundDetailActivity.class);
						intent.putExtra("contributeId", homeProgressBean.getMessageId());
						HomePageFragment.this.getActivity().startActivityForResult(intent,REQUEST_CODE_HELP_FOUND);
						sendIsReadAsyncHttp(homeProgressBean.getIndexMessageId());
						break;
						
					case HomeProgressBean.PUSH_MESSAGE:
						intent = new Intent(mContext, PushMessageListActivity.class);
						intent.putExtra("contributeId", homeProgressBean.getMessageId());
						intent.putExtra("title","微生活");
						intent.putExtra("modularValue","1");
						HomePageFragment.this.getActivity().startActivityForResult(intent,REQUEST_CODE_PUSH_MESSAGE);
						sendIsReadAsyncHttp(homeProgressBean.getIndexMessageId());
						break;
					default:
						ToastUtil.showToast(mContext, "点击发生意外的类型");
						break;
					}

				}
			}
		});


		if(application.hasPermission(getString(R.string.roles_permissions_applicant_repair))){
			mPublishRepairOrderIv.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			mPublishRepairOrderIv.setVisibility(ViewGroup.GONE);
		}
		
	}

	private void setupData(){
		loadHomeProgressListHttp();
	}

	private void loadHomeProgressListHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getHomeProgressLoadList());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		NameValuePair startPair = new BasicNameValuePair("start", "0");
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", "5");
		params.add(limitPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<HomePageBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<HomePageBean>>(){}.getType());
				HomePageBean result = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				else if(result == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					List<HomeProgressBean> resultList = result.getHomeProgressBeanList();
					List<PhotoBean> pictures;
					//Fix分支
						pictures = result.getContributeList()!=null&&!result.getContributeList().isEmpty()?result.getContributeList().get(0).getImageList():null;
					if(resultList == null){
						ToastUtil.showToast(mContext, R.string.error_data_null);
						return;
					}
					else{
						mHomeProgressBeanList.clear();
						mHomeProgressBeanList.addAll(resultList);
						mProgressBaseAdapter.notifyDataSetChanged();
					}
					if(application.hasPermission(getString(R.string.roles_permissions_has_repairman_like))){
						//维修工赞
						mPicturePerDayIv.setImageResource(R.drawable.home_page_top_repairman_like_enter_img);
						mPicturePerDayIv.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext, ExtraModularActivity.class);
								intent.putExtra("fragmentLabel", getString(R.string.modular_url_repairman_like));
								startActivity(intent);
							}
						});
					}
					else{
						//
						if(pictures != null && !pictures.isEmpty()) {
							final PhotoBean firstPicture = pictures.get(0);
							if(firstPicture != null  && firstPicture.getUrl() != null){
								application.getImageLoader().displayImage(firstPicture.getUrl(), mPicturePerDayIv,application.getSimpleDisplayImageOptions());
							}
							else{
								mPicturePerDayIv.setImageResource(R.drawable.image_default);
							}
							mPicturePerDayIv.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.setClass(mContext, PhotoImageShowActivity.class);
									Bundle bundle = new Bundle();
									ArrayList<String> imageList = new ArrayList<String>();
									imageList.add(firstPicture.getUrl());
									bundle.putStringArrayList("imageList",imageList);
									bundle.putInt("imageType", PhotoImageShowActivity.HTTP_URL_TYPE);
									intent.putExtras(bundle);
									startActivity(intent);
								}
							});
						}
						else{
							mPicturePerDayIv.setImageResource(R.drawable.image_default);
						}
						
						//
					}
					
					//是否新消息
					if(result.isPushHasNew()){
						titlebarPushMsgView.setImageResource(R.drawable.icon_push_msg_hasnew_layer);
					}
					else{
						titlebarPushMsgView.setImageResource(R.drawable.icon_push_msg_normal_layer);
					}
				}

			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	static class ProgressViewHolder{
		ImageView repairStatusView;
		TextView assetAndTroubleView;
		TextView contentView;
		TextView titleView;

	}

	private class ProgressBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mHomeProgressBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProgressViewHolder holder;
			final HomeProgressBean homeProgressBean = mHomeProgressBeanList.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_home_progress, null);
				holder = new ProgressViewHolder();
				holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.home_page_progress_repair_asset_and_trouble);
				holder.contentView = (TextView) convertView.findViewById(R.id.home_page_progress_content);
				holder.repairStatusView = (ImageView) convertView.findViewById(R.id.home_page_progress_repair_status_icon);
				holder.titleView = (TextView) convertView.findViewById(R.id.home_page_progress_title);
				holder.titleView.getPaint().setFakeBoldText(true);
				convertView.setTag(holder);
			}
			else{
				holder = (ProgressViewHolder) convertView.getTag();
			}
			//
			holder.assetAndTroubleView.setVisibility(ViewGroup.GONE);
			holder.repairStatusView.setVisibility(ViewGroup.INVISIBLE);
			holder.titleView.setVisibility(ViewGroup.GONE);

			holder.contentView.setText(homeProgressBean.getContent());

			switch (homeProgressBean.getType()) {
			case HomeProgressBean.REPAIR_INFO:
				holder.assetAndTroubleView.setVisibility(ViewGroup.VISIBLE);
				holder.repairStatusView.setVisibility(ViewGroup.VISIBLE);

				holder.assetAndTroubleView.setText(homeProgressBean.getTitle());
				if(homeProgressBean.getRepairStatus() != null){
					if(homeProgressBean.getRepairStatus().equals("2")){
						holder.repairStatusView.setImageResource(R.drawable.icon_home_page_status_repairing);
					}
					else if(homeProgressBean.getRepairStatus().equals("3")){
						holder.repairStatusView.setImageResource(R.drawable.icon_home_page_status_finish);
					}
				}
				else{
					//其他状态和null数据都如此处理
					holder.repairStatusView.setImageDrawable(null);
				}
				break;
			case HomeProgressBean.NOTICE_INFO:
				holder.titleView.setVisibility(ViewGroup.VISIBLE);
				holder.titleView.setText(homeProgressBean.getTitle());

				break;
			case HomeProgressBean.COMPLAINT_INFO:
				holder.titleView.setVisibility(ViewGroup.VISIBLE);
				holder.titleView.setText(homeProgressBean.getTitle());
				break;
			case HomeProgressBean.HELP_FOUND:
				holder.assetAndTroubleView.setVisibility(ViewGroup.VISIBLE);
				holder.repairStatusView.setVisibility(ViewGroup.VISIBLE);

				holder.assetAndTroubleView.setText(homeProgressBean.getTitle());
				holder.repairStatusView.setImageResource(R.drawable.icon_home_page_help_found);
				break;
				
			case HomeProgressBean.PUSH_MESSAGE:
				holder.titleView.setVisibility(ViewGroup.VISIBLE);
				holder.titleView.setText(homeProgressBean.getTitle());

				break;
			default:
				ToastUtil.showToast(mContext, "加载发生意外的类型");
				break;
			}
			return convertView;
		}

	}

	private void sendIsReadAsyncHttp(final String progressId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getNoticeIsReadSendForHomePageUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair noticeIdPair = new BasicNameValuePair("indexMessageId", progressId);
		params.add(noticeIdPair);
		final String userId = application.getLoginInfoBean().getUserId().toString();
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					Logger.i("HOME_PAGE已读发送成功,progressId:" +progressId +"userId:"+userId);
				}
				else{
					Logger.w("HOME_PAGE已读发送失败,progressId:" +progressId +"userId:"+userId);
				}
			}

			@Override
			public void failed(Exception exception) {

			}

			@Override
			public void beforeDealt() {

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * 检测电话是否合法
	 */
	private boolean phoneInvalid(){
		//手机号码检测（权限控制）
		if(application.hasPermission(getString(R.string.roles_permissions_no_phone_no_access))){

			String phone = application.getLoginInfoBean().getPhone();
			if(phone == null ||phone.toString().trim().length() <11){
				//返回无信息视为没有电话
				Intent intent = new Intent(mContext, EditInfoActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.fragment_label_change_contact));
				intent.putExtra("contact", phone);
				HomePageFragment.this.getActivity().startActivity(intent);
				return true;
			}
		}
		return false;
	}

	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_fragment_home_page;

		default:
			return R.layout.fragment_home_page;
		}
	}

}
