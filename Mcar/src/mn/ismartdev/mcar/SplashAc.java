package mn.ismartdev.mcar;

import java.sql.SQLException;

import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarBody;
import mn.ismartdev.mcar.model.CarCategory;
import mn.ismartdev.mcar.model.CarMark;
import mn.ismartdev.mcar.model.CarModel;
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
import android.util.Log;
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
			getCar();
		} else {
			Toast.makeText(this, getString(R.string.noNet), Toast.LENGTH_SHORT)
					.show();
			run();
		}

	}

	public void run() {
		finish();
		startActivity(new Intent(SplashAc.this, MainActivity.class));
	}

	private void getCompanies() {
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "company.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							if (response != null
									&& response.getInt("error_number") == 1) {
								JSONArray data = response.getJSONArray("data");
								makeCompany(data);

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

	private void getCar() {
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "cars.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub

						try {
							if (response != null
									&& response.getInt("error_number") == 1) {
								JSONArray data = response.getJSONArray("data");
								makeCar(data);

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						run();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						run();
					}

				}) {

		};
		mRequestQueue.add(logRequest);
	}

	private void makeCar(JSONArray data) throws JSONException {
		if (data.length() > 0) {
			helper.deleteCars();
			for (int i = 0; i < data.length(); i++) {
				JSONObject obj = data.getJSONObject(i);
				Car car = new Car();
				car.car_id = obj.optInt("id");
				car.seller_id = obj.optInt("seller_id");
				car.seller_name = obj.optString("seller_name");
				car.year = obj.optString("year");
				car.roller_type = obj.optInt("roller_type");
				car.came_year = obj.optString("came_year");
				car.features = obj.optString("features");
				car.seller_notes = obj.optString("seller_notes");
				car.distance_type = obj.optInt("distance_type");
				car.mark_id = obj.optInt("mark_id");
				car.door = obj.getString("door");
				CarMark mark = new CarMark();
				mark.id = car.mark_id;
				mark.name = obj.optString("mark_name");
				car.mark_name = mark.name;
				mark.image = obj.optString("mark_image");
				CarModel mod = new CarModel();
				mod.id = obj.optInt("model_id");
				mod.name = obj.optString("model");
				car.model_name = mod.name;

				mod.mark_id = mark.id;
				car.model_id = mod.id;

				CarBody body = new CarBody();
				body.id = obj.optInt("body_id");
				body.name = obj.optString("body_name");
				car.body_id = body.id;

				CarCategory cat = new CarCategory();
				cat.id = obj.optInt("category_id");
				cat.name = obj.optString("category_name");
				car.category_id = cat.id;

				car.modification = obj.optString("modification");
				car.status = obj.optInt("status");
				car.transmission = obj.optInt("transmission");
				car.distance = obj.optInt("distance");
				car.price = obj.optInt("price");
				car.fuel = obj.optInt("fuel");
				car.view_count = obj.optInt("viewcount");
				car.order = obj.optInt("order");

				car.drivetrain = obj.optString("drivetrain");
				car.engine = obj.optDouble("engine");

				car.created_date = obj.optString("created_date");
				car.image_url = obj.optString("image_url");

				try {
					helper.getCarDao().create(car);
					helper.getMarkDao().create(mark);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				try {
					helper.getModelDao().create(mod);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
				}
				try {
					helper.getBodyDao().create(body);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
				}
				try {
					helper.getCarCatDao().create(cat);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
				}

			}
		}

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

				}

			}

		}

	}
}
