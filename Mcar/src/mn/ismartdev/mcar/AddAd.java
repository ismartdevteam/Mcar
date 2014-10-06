package mn.ismartdev.mcar;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.ismartdev.mcar.model.AdCat;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.CustomRequest;
import mn.ismartdev.mcar.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.test.UiThreadTest;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class AddAd extends ActionBarActivity implements OnClickListener {
	private EditText title;
	private EditText price;
	private EditText desc;
	private EditText phone;
	private Spinner type;
	private ImageView image1;
	private ImageView image2;
	private ImageView image3;
	private Button add;
	private List<AdCat> typeList;
	private String[] typeStr;
	private Bitmap[] upload_images = new Bitmap[3];
	private DatabaseHelper helper;
	private static final int SELECT_PHOTO1 = 100;
	private static final int SELECT_PHOTO2 = 101;
	private static final int SELECT_PHOTO3 = 102;
	private RequestQueue mRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_add);
		title = (EditText) findViewById(R.id.ad_add_title);
		price = (EditText) findViewById(R.id.ad_add_price);
		desc = (EditText) findViewById(R.id.ad_add_desc);
		phone = (EditText) findViewById(R.id.ad_add_phone);
		type = (Spinner) findViewById(R.id.ad_add_cat);
		image1 = (ImageView) findViewById(R.id.ad_add_image1);
		image2 = (ImageView) findViewById(R.id.ad_add_image2);
		image3 = (ImageView) findViewById(R.id.ad_add_image3);
		add = (Button) findViewById(R.id.ad_add_new);
		image1.setOnClickListener(this);
		image2.setOnClickListener(this);
		image3.setOnClickListener(this);
		add.setOnClickListener(this);
		helper = new DatabaseHelper(this);
		try {
			typeList = helper.getadCatDao().queryForAll();
			typeStr = new String[typeList.size()];
			for (int i = 0; i < typeList.size(); i++) {
				typeStr[i] = typeList.get(i).name;
			}
			ArrayAdapter<String> cat_adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, typeStr);

			cat_adapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			type.setAdapter(cat_adapter);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ad_add_new:
			if (title.length() < 1) {
				Toast.makeText(
						AddAd.this,
						getString(R.string.title) + " "
								+ getString(R.string.fill_field),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (price.length() < 1) {
				Toast.makeText(
						AddAd.this,
						getString(R.string.price) + " "
								+ getString(R.string.fill_field),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (desc.length() < 1) {
				Toast.makeText(
						AddAd.this,
						getString(R.string.desc) + " "
								+ getString(R.string.fill_field),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (phone.length() < 1) {
				Toast.makeText(
						AddAd.this,
						getString(R.string.phone) + " "
								+ getString(R.string.fill_field),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (Utils.isNetworkAvailable(AddAd.this))
				addAd();
			else {
				Toast.makeText(AddAd.this, getString(R.string.noNet),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ad_add_image1:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, ""),
					SELECT_PHOTO1);
			break;
		case R.id.ad_add_image2:
			Intent intent1 = new Intent();
			intent1.setType("image/*");
			intent1.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent1, ""),
					SELECT_PHOTO2);
			break;
		case R.id.ad_add_image3:
			Intent intent2 = new Intent();
			intent2.setType("image/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent2, ""),
					SELECT_PHOTO3);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SELECT_PHOTO1:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String selectedImagePath = getAbsolutePath(data.getData());
					upload_images[0] = decodeFile(selectedImagePath);
					image1.setImageBitmap(upload_images[0]);
				}

			}
			break;
		case SELECT_PHOTO2:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String selectedImagePath = getAbsolutePath(data.getData());
					upload_images[1] = decodeFile(selectedImagePath);
					image2.setImageBitmap(upload_images[1]);
				}

			}
			break;
		case SELECT_PHOTO3:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String selectedImagePath = getAbsolutePath(data.getData());
					upload_images[2] = decodeFile(selectedImagePath);
					image3.setImageBitmap(upload_images[2]);
				}
			}
			break;
		}
	}

	public Bitmap decodeFile(String path) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 200;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeFile(path, o2);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getAbsolutePath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	public static String encodeTobase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

		return imageEncoded;
	}

	private void addAd() {
		final ProgressDialog progress = ProgressDialog.show(AddAd.this, "",
				getString(R.string.loading));
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		final String imei = telephonyManager.getDeviceId();
		mRequestQueue = Volley.newRequestQueue(this);
		CustomRequest adReq = new CustomRequest(Method.POST,
				this.getString(R.string.main_ip) + "add_ad.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						progress.dismiss();
						try {
							if (response != null) {
								Toast.makeText(
										AddAd.this,
										response.getString("error_description"),
										Toast.LENGTH_SHORT).show();

							} else {
								Toast.makeText(AddAd.this,
										"Алдаа гарлаа дараа дахин оролдоно уу",
										Toast.LENGTH_SHORT).show();
							}
							finish();
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
						progress.dismiss();
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("title", title.getText().toString().trim());
				params.put("price", price.getText().toString().trim());
				params.put("desc", desc.getText().toString().trim());
				params.put("phone", phone.getText().toString().trim());
				params.put("cat_id",
						typeList.get(type.getSelectedItemPosition()).id + "");
				params.put("imei", imei);
				for (int i = 0; i < upload_images.length; i++) {
					if (upload_images[i] != null) {
						params.put("image" + (i + 1),
								encodeTobase64(upload_images[i]));

					}
				}
				return params;
			}

		};
		mRequestQueue.add(adReq);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}
}
