package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.OfficeMaterialDivisionStatisticActivity;
import com.wutong.repair.data.bean.OfficeMaterialBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

/**
 * 申领管理模块下-用品的模块
 * @author Jolly
 * 创建时间：2014年2月24日下午4:34:10
 *
 */
public class OfficeMaterialListStatisticFragment extends BaseFragment {

	private final static int VIEW_CODE_LIST = 100;
	private final static int VIEW_CODE_FOOTER = 101;
	
	private EditText searchContentView;
	private ImageView searchCleanView;
	private ImageView searchSubmitView;

	private View usedNumberLayoutView;
	private View storeNumberLayoutView;

	private TextView usedNumberView;
	private TextView storeNumberView;

	private CheckBox usedNumberToggleView;
	private CheckBox storeNumberToggleView;

	private PullToRefreshView materialPullToRefreshView;
	private ListView materialListView;
	private MaterialListBaseAdapter materialListBaseAdapter;
	private List<OfficeMaterialBean> materialBeanList;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;

	private Integer materialCurrentStart = 0;
	private String materialOrderType;
	private String materialSort;
	private String materialSearch;

	private final static String MATERIAL_ORDER_TYPE_MONTH_USED = "2";//使用量
	private final static String MATERIAL_ORDER_TYPE_STORE = "1";//库存量

	private final static String MATERIAL_SORT_DESC = "1";
	private final static String MATERIAL_SORT_ASC = "2";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		dataInit();
		viewInit();
		setupData();
		return fragmentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isSubFragment = true;
	}
	private void dataInit(){
		materialOrderType = MATERIAL_ORDER_TYPE_MONTH_USED;
		materialSort = MATERIAL_SORT_DESC;
		materialSearch = "";
	}

	private void viewInit(){
		searchContentView = (EditText) findViewById(R.id.material_page_search_input);
		searchCleanView = (ImageView) findViewById(R.id.material_page_search_clean);;
		searchSubmitView = (ImageView) findViewById(R.id.material_page_search_submit);
		usedNumberLayoutView = findViewById(R.id.material_page_used_number_layout);
		storeNumberLayoutView = findViewById(R.id.material_page_store_number_layout);
		usedNumberView = (TextView) findViewById(R.id.material_page_used_number_tv);
		storeNumberView = (TextView) findViewById(R.id.material_page_store_number_tv);

		usedNumberToggleView = (CheckBox) findViewById(R.id.material_page_used_number_toggle);
		storeNumberToggleView = (CheckBox) findViewById(R.id.material_page_store_number_toggle);
		materialListView =  (ListView) findViewById(R.id.material_page_material_list);
		materialPullToRefreshView = (PullToRefreshView) findViewById(R.id.workbench_material_pull_refresh_view);
		footerView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_FOOTER), null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		materialListView.addFooterView(footerView);
		View emptyView = findViewById(R.id.workbench_material_empty);
		materialListView.setEmptyView(emptyView);
		materialListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position < materialBeanList.size()){
					Intent intent;
					//Fix分支
						intent = new Intent(mContext, OfficeMaterialDivisionStatisticActivity.class);
					intent.putExtra("officeMaterialId", materialBeanList.get(position).getId());
					startActivity(intent);
				}
			}
		});

		materialBeanList = new ArrayList<OfficeMaterialBean>();
		materialListBaseAdapter = new MaterialListBaseAdapter();
		materialListView.setAdapter(materialListBaseAdapter);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);

			}
		});
		searchSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);

			}
		});
		materialPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);

			}
		});
		materialPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMaterialAsyncHttp(false, materialOrderType, materialSort, materialSearch);

			}
		});

		usedNumberLayoutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				usedNumberToggleView.performClick();
				usedNumberLayoutView.setSelected(true);
				usedNumberToggleView.setSelected(true);
				storeNumberLayoutView.setSelected(false);
				storeNumberToggleView.setSelected(false);
				usedNumberView.setSelected(true);
				storeNumberView.setSelected(false);
			}
		});
		storeNumberLayoutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				storeNumberToggleView.performClick();
				storeNumberLayoutView.setSelected(true);
				storeNumberToggleView.setSelected(true);
				usedNumberLayoutView.setSelected(false);
				usedNumberToggleView.setSelected(false);
				storeNumberView.setSelected(true);
				usedNumberView.setSelected(false);
			}
		});

		usedNumberToggleView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				materialOrderType = MATERIAL_ORDER_TYPE_MONTH_USED;
				materialSort = isChecked ?MATERIAL_SORT_ASC:MATERIAL_SORT_DESC;
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
			}
		});
		storeNumberToggleView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				materialOrderType = MATERIAL_ORDER_TYPE_STORE;
				materialSort = isChecked ?MATERIAL_SORT_ASC:MATERIAL_SORT_DESC;
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
			}
		});

		searchContentView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch(actionId){  
				case EditorInfo.IME_NULL:  
					Logger.i("null for default_content: " + v.getText() );  
					break; 
				case EditorInfo.IME_ACTION_NEXT:  
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
					break;
				case EditorInfo.IME_ACTION_SEND:  
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
					break;  
				case EditorInfo.IME_ACTION_DONE:  
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch); 
					break;  
				case EditorInfo.IME_ACTION_SEARCH:
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
					break;
				}  
				return true;
			}
		});
		searchContentView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				materialSearch = s.toString();

			}
		});

		searchCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchContentView.setText("");
				//执行 刷新
			}
		});
	}

	private void setupData(){
		loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
		//Fix分支，代码已删除
	}


	static class WorkbenchMaterialViewHolder{
		TextView nameView;
		TextView monthUsedNumberView;
		TextView brandView;
		TextView specificationView;
		TextView storeNumberView;
	}

	private class MaterialListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return materialBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return materialBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WorkbenchMaterialViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_LIST), null);
				holder = new WorkbenchMaterialViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.material_page_material_name);
				holder.brandView = (TextView) convertView.findViewById(R.id.material_page_brand);
				holder.monthUsedNumberView = (TextView) convertView.findViewById(R.id.material_page_month_used_number);
				holder.specificationView = (TextView) convertView.findViewById(R.id.material_page_specification);
				holder.storeNumberView = (TextView) convertView.findViewById(R.id.material_page_store_number);
				convertView.setTag(holder);
			}
			OfficeMaterialBean officeMaterialBean = materialBeanList.get(position);
			holder = (WorkbenchMaterialViewHolder) convertView.getTag();
			holder.nameView.setText(officeMaterialBean.getName());
			holder.brandView.setText(officeMaterialBean.getBrand());
			holder.monthUsedNumberView.setText(officeMaterialBean.getMonthUsedNumber().toString()+officeMaterialBean.getUnit());
			holder.storeNumberView.setText(officeMaterialBean.getIdleCount()+officeMaterialBean.getUnit());
			
			//Fix分支
				((TextView) convertView.findViewById(R.id.material_page_specification_label)).setText(R.string.workbench_material_page_stat_spec_to_code_index_label);
				holder.specificationView.setText(officeMaterialBean.getCodeIndex());
			
			
			return convertView;
		}

	}

	private void loadMaterialAsyncHttp( final boolean isRefresh,String orderType,String sort,String search){
		if(isRefresh){
			materialCurrentStart = 0;
		}
		else{
			materialCurrentStart += application.getPagingSize();
		}
		String url = application.getCommonHttpUrlActionManager().getOfficeOfficeMaterialListStatisticList();
		AjaxParams params = new AjaxParams();
		params.put("start", materialCurrentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		params.put("order", orderType);
		params.put("dir", sort);
		params.put("condition", search);
		Logger.i("[params:" + params.getParamString() + "]");
		Logger.i("[URL:" + url +"]");

		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				HttpResponseUtil.justToast(new Exception(t), mContext);
			}

			@Override
			public void onStart() {
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				hideProgressDialog();
				Logger.i("[result:" + t.toString() +"]");
				ResponseBean<List<OfficeMaterialBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<OfficeMaterialBean>>>(){}.getType());
				List<OfficeMaterialBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					if(isRefresh){
						materialBeanList.clear();
						materialPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						materialPullToRefreshView.onFooterRefreshComplete();
					}
					materialBeanList.addAll(resultList);



					if(resultList.size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							materialListView.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							materialListView.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
					materialListBaseAdapter.notifyDataSetChanged();
				}
			}

		});
	}
	
	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_LIST:
				return R.layout.spring_horse_list_item_office_material;
			case VIEW_CODE_FOOTER:
				return R.layout.spring_horse_footer_common_no_more;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_LIST:
				return R.layout.list_item_office_material;
			case VIEW_CODE_FOOTER:
				return R.layout.footer_common_no_more;
			default:
				break;
			}
		}
		return 0;
	}
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			searchSubmitView.setImageResource(R.drawable.spring_horse_search_selector);
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View emptyView = findViewById(R.id.workbench_material_empty);
			TextView tipView = (TextView) emptyView.findViewById(R.id.empty_tip);
			TextView clickTipView = (TextView) emptyView.findViewById(R.id.empty_click_tip);
			tipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			clickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			tipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);

			break;

		default:
			break;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_fragment_office_material_list_statistic;

		default:
			return R.layout.fragment_office_material_list_statistic;
		}
	}
}
