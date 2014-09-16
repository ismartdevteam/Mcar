package mn.ismartdev.mcar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.ismartdev.mcar.adapter.AdAdapter;
import mn.ismartdev.mcar.fragment.ScrollTabHolderFragment;
import mn.ismartdev.mcar.model.Ad;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.CustomRequest;
import mn.ismartdev.mcar.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
	private List<Ad> mListItems;
	private int cat_id;
	private int mPosition;
	private View load_footer;
	private RequestQueue mRequestQueue;
	private AdAdapter adapter;
	private boolean isLoad = false;
	private DatabaseHelper helper;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, null);
		View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, mListView, false);
		mListView.addHeaderView(placeHolderView);
		mListView = (ListView) v.findViewById(R.id.listView);
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

		if (mListView.getLastVisiblePosition() == index - 1) {

			// Log.i("selecteditem", visibleItemCount + "");
			if (index % 10 == 0 || !isLoad)
				getAd(index);
			// new NextListItems().execute();

		}
	}

	private void getAd(final int sIndex) {
		Log.i("ad index", sIndex + "");
		isLoad = true;
		mListView.addFooterView(load_footer);

		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "ad.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							if (response != null
									&& response.getInt("error_number") == 1) {
								index = index + response.getInt("number_row");
								JSONArray data = response.getJSONArray("data");
								makeAd(data);

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						} catch (SQLException e) {
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

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("index", sIndex + "");
				params.put("cat_id", cat_id + "");
				return params;
			}

		};
		mRequestQueue.add(logRequest);
	}

	protected void makeAd(JSONArray data) throws JSONException, SQLException {
		// TODO Auto-generated method stub
		if (data.length() > 0) {
			for (int i = 0; i < data.length(); i++) {
				Ad ad = new Ad();
				JSONObject obj = data.getJSONObject(i);
				ad.category_id=obj.optInt("category_id");
				ad.title = obj.optString("title");
				ad.description = obj.optString("desc");
				ad.price = obj.optInt("price");
				ad.date = obj.optString("created_date");
				ad.images = obj.optString("images");
				ad.phone = obj.optString("phone");
				ad.order = obj.optInt("order_status");
				helper.getAdDao().create(ad);
				mListItems.add(ad);
		
			}
		}
		adapter.notifyDataSetChanged();

		isLoad = false;
		mListView.setOnScrollListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// nothing
	}

}