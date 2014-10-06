package mn.ismartdev.mcar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mn.ismartdev.mcar.adapter.AdAdapter;
import mn.ismartdev.mcar.detail.AdDetail;
import mn.ismartdev.mcar.fragment.ScrollTabHolderFragment;
import mn.ismartdev.mcar.model.Ad;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.CustomRequest;
import mn.ismartdev.mcar.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class SampleListFragment extends ScrollTabHolderFragment implements
		OnScrollListener {

	private static final String ARG_POSITION = "position";
	private int index = 0;
	private ListView mListView;
	private ArrayList<Ad> mListItems;
	private int cat_id;
	private int mPosition;
	private View load_footer;
	private RequestQueue mRequestQueue;
	private AdAdapter adapter;
	private boolean isFinish = false;
	private DatabaseHelper helper;
	private boolean flag_loading = false;
	private View v;

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
		cat_id = getArguments().getInt("cat");
		mListItems = new ArrayList<Ad>();
	}

	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	 v = inflater.inflate(R.layout.fragment_list, container, false);
		mListView = (ListView) v.findViewById(R.id.listView);
		View placeHolderView = inflater.inflate(
				R.layout.view_header_placeholder, mListView, false);
		mListView.addHeaderView(placeHolderView);

		load_footer = inflater.inflate(R.layout.list_load_footer, null);
	
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		helper = new DatabaseHelper(getActivity());

		mRequestQueue = Volley.newRequestQueue(getActivity());
		adapter = new AdAdapter(getActivity(), mListItems);
		mListView.setAdapter(adapter);

		if (Utils.isNetworkAvailable(getActivity())) {
			getAd(index);
		} else
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.noNet), Toast.LENGTH_SHORT)
					.show();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putInt("ad_id", mListItems.get(position-1).id);

				Intent adIntent = new Intent(getActivity(), AdDetail.class);
				adIntent.putExtras(b);
				startActivity(adIntent);
			}
		});
	}

	// mListView.setAdapter(new ArrayAdapter<String>(getActivity(),
	// R.layout.list_item, android.R.id.text1, mListItems));

	@Override
	public void adjustScroll(int scrollHeight) {
		if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
			return;
		}

		mListView.setSelectionFromTop(1, scrollHeight);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mScrollTabHolder != null)
			mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount, mPosition);
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount != 0) {
			if (flag_loading == false && isFinish == false) {
				flag_loading = true;
				getAd(index);
			}
		}
	}

	private void getAd(final int sIndex) {	
		mListView.addFooterView(load_footer);
		CustomRequest adReq = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "ad.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							if (response != null
									&& response.getInt("error_number") == 1) {
								int num_rows = response.getInt("number_row");

								if (num_rows < 10) {
									isFinish = true;
								}
								index = index + 10;
								JSONArray data = response.getJSONArray("data");
								makeAd(data);

							}
							flag_loading = false;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mListView.removeFooterView(load_footer);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						mListView.removeFooterView(load_footer);
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("index", sIndex + "");
				Log.e("index", sIndex + "");
				params.put("cat_id", cat_id + "");
				return params;
			}

		};
		mRequestQueue.add(adReq);
	}

	protected void makeAd(JSONArray data) throws JSONException, SQLException {
		// TODO Auto-generated method stub
		if (data.length() > 0) {
			for (int i = 0; i < data.length(); i++) {
				Ad ad = new Ad();

				JSONObject obj = data.getJSONObject(i);
				ad.category_id = obj.optInt("category_id");
				ad.title = obj.optString("title");
				ad.description = obj.optString("description");
				ad.price = obj.optInt("price");
				ad.date = obj.optString("created_date");
				ad.images = obj.optString("images");
				ad.phone = obj.optString("phone");
				ad.order = obj.optInt("order_status");
				helper.getAdDao().create(ad);
				mListItems.add(ad);

			}
		} else {
			isFinish = true;
		}

		adapter.notifyDataSetChanged();
		mListView.setOnScrollListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// nothing
	}

}