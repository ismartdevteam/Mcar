package mn.ismartdev.mcar.detail;

import java.sql.SQLException;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Ad;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.Utils;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

public class AdDetail extends ActionBarActivity implements
		BaseSliderView.OnSliderClickListener {
	private TextView title;
	private TextView desc;
	private TextView date;
	private TextView phone;
	private SliderLayout imageSlider;
	private DatabaseHelper helper;
	private Ad data;
	private ActionBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_detail);
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
		helper = new DatabaseHelper(this);
		imageSlider = (SliderLayout) findViewById(R.id.ad_det_slider);
		title = (TextView) findViewById(R.id.ad_detail_title);
		desc = (TextView) findViewById(R.id.ad_detail_desc);
		phone = (TextView) findViewById(R.id.ad_detail_phone);
		date = (TextView) findViewById(R.id.ad_detail_date);
		try {
			data = helper.getAdDao().queryForId(
					getIntent().getExtras().getInt("ad_id", 1));
			String images[] = data.images.split(",");

			for (String url : images) {
				if (url.length() > 5) {
					TextSliderView textSliderView = new TextSliderView(this);
					textSliderView
							.description(
									Utils.numberToFormat(data.price) + " ₮")
							.image(url)
							.setScaleType(BaseSliderView.ScaleType.CenterCrop)
							.setOnSliderClickListener(this);

					textSliderView.getBundle().putString("extra", url);

					imageSlider.addSlider(textSliderView);
				} else {
					TextSliderView textSliderView = new TextSliderView(this);
					textSliderView
							.description(
									Utils.numberToFormat(data.price) + " ₮")
							.image(R.drawable.car_holder)
							.setScaleType(
									BaseSliderView.ScaleType.FitCenterCrop)
							.setOnSliderClickListener(this);

					textSliderView.getBundle().putString("extra",
							R.drawable.car_holder + "");

					imageSlider.addSlider(textSliderView);
					break;
				}
			}

			imageSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);
			imageSlider
					.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
			imageSlider.setCustomAnimation(new DescriptionAnimation());
			imageSlider.setDuration(10000);
			title.setText(data.title);
			desc.setText(data.description);
			phone.setText(data.phone);
			date.setText(data.date);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onSliderClick(BaseSliderView slider) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.com_det_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// if (id == R.id.action_search_ad) {
		// mPagerAdapter.filterCar();
		// }
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

}
