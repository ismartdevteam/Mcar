package com.flavienlaurent.notboringactionbar;

import java.util.Random;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.DatabaseHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by f.laurent on 21/11/13.
 */
public class KenBurnsSupportView extends FrameLayout {

	private static final String TAG = "KenBurnsView";

	private final Handler mHandler;
	private String[] mResourceURL;
	private NetworkImageView[] mImageViews;
	private int mActiveImageIndex = -1;

	private final Random random = new Random();
	private int mSwapMs = 10000;
	private int mFadeInOutMs = 400;

	private float maxScaleFactor = 1.5F;
	private float minScaleFactor = 1.2F;
	private int mSize = 0;
	private Context mContext;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;
	private Runnable mSwapImageRunnable = new Runnable() {
		@Override
		public void run() {
			swapImage();
			mHandler.postDelayed(mSwapImageRunnable, mSwapMs - mFadeInOutMs * 2);
		}
	};


	public KenBurnsSupportView(Context context) {
		this(context, null);
	}

	public KenBurnsSupportView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public KenBurnsSupportView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mHandler = new Handler();

		this.mContext = context;
		mRequestQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mRequestQueue,
				new ImageLoader.ImageCache() {
					private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
							10);

					public void putBitmap(String url, Bitmap bitmap) {
						mCache.put(url, bitmap);
					}

					public Bitmap getBitmap(String url) {
						return mCache.get(url);
					}
				});
	}

	public void setResourceURL(String... resourceIds) {
		mResourceURL = resourceIds;
		mSize=resourceIds.length;
		fillImageViews();
	}

	private void swapImage() {
		Log.d(TAG, "swapImage active=" + mActiveImageIndex);
		if(mImageViews.length>0){
		if (mActiveImageIndex == -1) {
			mActiveImageIndex = 1;
			animate(mImageViews[mActiveImageIndex]);
			return;
		}

		int inactiveIndex = mActiveImageIndex;
		mActiveImageIndex = (1 + mActiveImageIndex) % mImageViews.length;
		Log.d(TAG, "new active=" + mActiveImageIndex);

		final NetworkImageView activeImageView = mImageViews[mActiveImageIndex];
		ViewHelper.setAlpha(activeImageView, 0.0f);
		ImageView inactiveImageView = mImageViews[inactiveIndex];

		animate(activeImageView);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(mFadeInOutMs);
		animatorSet.playTogether(
				ObjectAnimator.ofFloat(inactiveImageView, "alpha", 1.0f, 0.0f),
				ObjectAnimator.ofFloat(activeImageView, "alpha", 0.0f, 1.0f));
		animatorSet.start();
		}
	}

	private void start(View view, long duration, float fromScale,
			float toScale, float fromTranslationX, float fromTranslationY,
			float toTranslationX, float toTranslationY) {
		ViewHelper.setScaleX(view, fromScale);
		ViewHelper.setScaleY(view, fromScale);
		ViewHelper.setTranslationX(view, fromTranslationX);
		ViewHelper.setTranslationY(view, fromTranslationY);
		ViewPropertyAnimator propertyAnimator = ViewPropertyAnimator
				.animate(view).translationX(toTranslationX)
				.translationY(toTranslationY).scaleX(toScale).scaleY(toScale)
				.setDuration(duration);

		propertyAnimator.start();
		Log.d(TAG, "starting Ken Burns animation " + propertyAnimator);
	}

	private float pickScale() {
		return this.minScaleFactor + this.random.nextFloat()
				* (this.maxScaleFactor - this.minScaleFactor);
	}

	private float pickTranslation(int value, float ratio) {
		return value * (ratio - 1.0f) * (this.random.nextFloat() - 0.5f);
	}

	public void animate(View view) {
		float fromScale = pickScale();
		float toScale = pickScale();
		float fromTranslationX = pickTranslation(view.getWidth(), fromScale);
		float fromTranslationY = pickTranslation(view.getHeight(), fromScale);
		float toTranslationX = pickTranslation(view.getWidth(), toScale);
		float toTranslationY = pickTranslation(view.getHeight(), toScale);
		start(view, this.mSwapMs, fromScale, toScale, fromTranslationX,
				fromTranslationY, toTranslationX, toTranslationY);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startKenBurnsAnimation();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mHandler.removeCallbacks(mSwapImageRunnable);
	}

	private void startKenBurnsAnimation() {
		mHandler.post(mSwapImageRunnable);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ViewGroup view = (ViewGroup) inflate(getContext(),
				R.layout.view_kenburns, this);

		mImageViews = new NetworkImageView[mSize];
		for (int i = 0; i < mSize; i++) {
			mImageViews[i] = new NetworkImageView(mContext);
			mImageViews[i].setScaleType(ScaleType.CENTER_CROP);
			mImageViews[i].setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			view.addView(view);
		}
		// mImageViews[0] = (NetworkImageView) view.findViewById(R.id.image);
		// mImageViews[1] = (NetworkImageView) view.findViewById(R.id.image1);
	}

	private void fillImageViews() {
		for (int i = 0; i < mImageViews.length; i++) {
			mImageViews[i].setImageUrl(mResourceURL[i], mImageLoader);

		}
	}
}
