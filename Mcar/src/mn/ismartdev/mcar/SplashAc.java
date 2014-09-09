package mn.ismartdev.mcar;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import mn.ismartdev.mcar.model.Company;
import mn.ismartdev.mcar.model.CompanyType;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.CustomRequest;
import mn.ismartdev.mcar.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class SplashAc extends Activity {
	private RequestQueue mRequestQueue;
	private DatabaseHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		helper = new DatabaseHelper(this);
		mRequestQueue = Volley.newRequestQueue(this);
		if (Utils.isNetworkAvailable(this)) {
			getCompanies();
		} else{
			Toast.makeText(this, getString(R.string.noNet), Toast.LENGTH_SHORT)
					.show();
			run();
		}
	
	}
public void run(){
	 finish();
	 startActivity(new Intent(SplashAc.this, MainActivity.class));
}
	private void getCompanies() {
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "company.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub

						try {
							if (response != null
									&& response.getInt("error_number") == 1) {
								Log.i("company", response.toString());
								JSONArray data = response.getJSONArray("data");
								makeCompany(data);
								run();
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

	private void makeCompany(JSONArray data) throws JSONException {
		if (data.length() > 0) {
			helper.deleteCompany();
			for (int i = 0; i < data.length(); i++) {
				JSONObject obj = data.getJSONObject(i);
				Company com = new Company();
				com.name = obj.optString("name");
				com.description = obj.optString("description");

				com.contact = obj.optString("contact");
				com.address = obj.optString("address");

				com.video = obj.optString("video_url");
				com.location = obj.optString("location");
				com.phone = obj.optString("phone");

				com.logo = obj.optString("logo");
				com.images = obj.optString("image_url");
				com.order = obj.optInt("order");
				CompanyType companyType = new CompanyType();
				companyType.id = obj.optInt("type_id");
				companyType.name = obj.optString("type_name");
				com.type_id = companyType.id;
				try {
					helper.getComDao().create(com);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					helper.getComTypeDao().create(companyType);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}

			}

		}
	
	}
}
