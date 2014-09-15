package mn.ismartdev.mcar;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import mn.ismartdev.mcar.fragment.ScrollTabHolderFragment;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.CustomRequest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SampleListFragment extends ScrollTabHolderFragment implements OnScrollListener {

	private static final String ARG_POSITION = "position";
	private int index =0;
	private ListView mListView;
	private ArrayList<String> mListItems;
	private int cat_id;
	private int mPosition;
	private View load_footer;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;
	public static Fragment newInstance(int position, int catId) {
	
		SampleListFragment f = new SampleListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putInt("cat", catId);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(ARG_POSITION);
		cat_id= getArguments().getInt("cat");
		mListItems = new ArrayList<String>();

		for (int i = 1; i <= 100; i++) {
			mListItems.add(i + ". item - currnet page: " + (mPosition + 1));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, null);

		mListView = (ListView) v.findViewById(R.id.listView);
		load_footer=inflater.inflate(R.layout.list_load_footer, null);
//		View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, mListView, false);
//		mListView.addHeaderView(placeHolderView);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListView.setOnScrollListener(this);
		mRequestQueue = Volley.newRequestQueue(getActivity());
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
//		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item, android.R.id.text1, mListItems));
	

	@Override
	public void adjustScroll(int scrollHeight) {
		if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
			return;
		}

		mListView.setSelectionFromTop(1, scrollHeight);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mScrollTabHolder != null)
			mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
	
		if (mListView.getLastVisiblePosition() == index - 1) {

//			Log.i("selecteditem", visibleItemCount + "");
//			new NextListItems().execute();

		}
	}
	private void getAd(int index){
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "ad.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							if (response != null
									&& response.getInt("error_number") == 1) {
								JSONArray data = response.getJSONArray("data");
								makeAd(data);

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
					
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
					
					}

				}) {

		};
		mRequestQueue.add(logRequest);
	}
	protected void makeAd(JSONArray data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// nothing
	}

}