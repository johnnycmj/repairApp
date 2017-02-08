package com.wutong.repair.activity;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

import com.wutong.androidprojectlibary.http.bean.UrlBitmapFormBean;
import com.wutong.androidprojectlibary.http.util.UrlLoadBitmapAsyncTask;
import com.wutong.androidprojectlibary.http.util.UrlLoadBitmapAsyncTask.OnUrlLoadBitmapDealtListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.BitmapUtil;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhotoImageShowActivity extends BaseActivity {
	public static final int URI_TYPE = 1;
	public static final int HTTP_URL_TYPE = 2;
	
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	
	private ArrayList<Uri> imageUriList;
	private ArrayList<String> imageUrlList;
	private int imageType = 0;
	private ViewPager mImageGalleryView;
	private PagerAdapter galleryAdapter;
	private LinearLayout mIndicatorView;
	private View currentIndicatorView;
	private int imageSize;
	private Bitmap[] imageCacheArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_image_show);
		setStatPageName(mContext, R.string.title_activity_photo_image_show);
		intentInit();
		titleBarInit();
		viewInit();
		setupData();
	}

	private void intentInit(){
		imageType = getIntent().getIntExtra("imageType", 0);
		switch (imageType) {
		case URI_TYPE:
			imageUriList = getIntent().getParcelableArrayListExtra("imageList");
			imageSize = imageUriList.size();
			break;
		case HTTP_URL_TYPE:
			imageUrlList = getIntent().getStringArrayListExtra("imageList");
			imageSize = imageUrlList.size();
			break;
		default:
			break;
		}
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_photo_image_show);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PhotoImageShowActivity.this.onBackPressed();

			}
		});
		

	}
	
	protected void viewInit(){
		mImageGalleryView  = (ViewPager) findViewById(R.id.viewpager_photo_gallery);
		mIndicatorView = (LinearLayout) findViewById(R.id.viewpager_indicator);
		imageCacheArray = new Bitmap[imageSize];
		galleryAdapter = new GalleryPagerAdapter();
		mImageGalleryView.setAdapter(galleryAdapter);
		refreshIndicator();
		mImageGalleryView.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				View indcatorView = mIndicatorView.getChildAt(position);
				indcatorView.setSelected(true);
				if(currentIndicatorView != null){
					currentIndicatorView.setSelected(false);
				}
				currentIndicatorView = indcatorView;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private class GalleryPagerAdapter extends PagerAdapter{

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return imageSize;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			((ViewPager)container).removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View viewPagerView = LayoutInflater.from(mContext).inflate(R.layout.gallery_image,null);
			PhotoView imageView = (PhotoView) viewPagerView.findViewById(R.id.gallery_image_content);
			Bitmap bitmapCache = imageCacheArray[position];
			if(bitmapCache != null){
				int width = bitmapCache.getWidth();
				int height = bitmapCache.getHeight();
				if(width > height){
					Matrix m = new Matrix();
					m.postRotate(90);
					bitmapCache = Bitmap.createBitmap(bitmapCache, 0, 0, width, height, m , true);
				}
				imageView.setImageBitmap(bitmapCache);
			}
			else{
				switch (imageType) {
				case URI_TYPE:
					Uri uri = imageUriList.get(position);
					Bitmap bitmap =BitmapUtil.convertUri2Bitmap(mContext, uri);
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					if(width > height){
						Matrix m = new Matrix();
						m.postRotate(90);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m , true);
					}
					imageView.setImageBitmap(bitmap);
					imageCacheArray[position] = bitmap;
					break;
				case HTTP_URL_TYPE:
					loadHttpUrlImage(imageView,position);
					break;
				}
			}
			((ViewPager)container).addView(viewPagerView );
			return viewPagerView;
		}

	}
	/**
	 * 更新指示器
	 */
	private void refreshIndicator(){
		switch (imageType) {
		case URI_TYPE:
			if(imageUriList != null && !imageUriList.isEmpty()){
				int size = imageUriList.size();
				for(int i =0;i< size;i++ ){
					View indicatorView = LayoutInflater.from(mContext).inflate(R.layout.linearlayout_viewpager_indicator, null);
					mIndicatorView.addView(indicatorView);
				}
			}
			break;

		case HTTP_URL_TYPE:
			if(imageUrlList != null && !imageUrlList.isEmpty()){
				int size = imageUrlList.size();
				for(int i =0;i< size;i++ ){
					View indicatorView = LayoutInflater.from(mContext).inflate(R.layout.linearlayout_viewpager_indicator, null);
					mIndicatorView.addView(indicatorView);
				}
			}
			break;
		default:
			break;
		}
	}
	
	@SuppressLint("NewApi")
	private void loadHttpUrlImage(final ImageView imageView ,final int position ){
		UrlBitmapFormBean urlBitmapFormBean = new UrlBitmapFormBean();
		String url = imageUrlList.get(position);
		urlBitmapFormBean.setUrl(url);
		urlBitmapFormBean.setScale(false);

//		int maxSize = MobileInfoUtil.getDisplayWidth(PhotoImageShowActivity.this);
//		Logger.i("maxSize:"+maxSize);
		urlBitmapFormBean.setMaxSize(2048);
		UrlLoadBitmapAsyncTask urlLoadBitmapAsyncTask = new UrlLoadBitmapAsyncTask(mContext);
		urlLoadBitmapAsyncTask.setOnUrlLoadBitmapDealtListener(new OnUrlLoadBitmapDealtListener() {

			@Override
			public void success(Bitmap resultResponse) {
				int width = resultResponse.getWidth();
				int height = resultResponse.getHeight();
				if(width > height){
					Matrix m = new Matrix();
					m.postRotate(90);
					resultResponse = Bitmap.createBitmap(resultResponse, 0, 0, width, height, m , true);
				}
				imageView.setImageBitmap(resultResponse);
				imageCacheArray[position] = resultResponse;
			}

			@Override
			public void failed(Exception exception) {
				imageView.setImageResource(R.drawable.image_failed);
				imageCacheArray[position] = BitmapFactory.decodeResource(getResources(), R.drawable.image_failed);
			}

			@Override
			public void beforeDealt() {
				// TODO Auto-generated method stub

			}
		});
		if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
			urlLoadBitmapAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlBitmapFormBean);
		}
		else{
			urlLoadBitmapAsyncTask.execute(urlBitmapFormBean);
		}
	}

	protected void setupData() {
		View indcatorView = mIndicatorView.getChildAt(0);
		indcatorView.setSelected(true);
		currentIndicatorView = indcatorView;
	}
}
