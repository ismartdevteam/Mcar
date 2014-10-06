package mn.ismartdev.mcar.util;

import java.util.Arrays;
import java.util.List;

import mn.ismartdev.mcar.R;
import android.R.anim;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookLogin extends ActionBarActivity {
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(anim.fade_in, anim.fade_out);
	}

	private Bundle b;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private UiLifecycleHelper uiHelper;
	private String link = "https://play.google.com/store/apps/details?id=mn.ismartdev.mcar";
	@SuppressWarnings("unused")
	private boolean isResumed = false;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private Session session;
	private ActionBar bar;

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {

		if (state.equals(SessionState.CLOSED)) {

		}
		if (session != null && session.isOpened()) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {

				List<String> permissions = session.getPermissions();
				if (!permissions.containsAll(PERMISSIONS)) {
					requestPublishPermissions(session);
					return;
				}
			} else {
				session = Session.getActiveSession();
				showWebDialog();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebookshare);
		b = getIntent().getExtras();

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		session = Session.getActiveSession();
		if (session == null || !session.isOpened()) {
			Toast.makeText(getApplicationContext(),
					"Фейсбүүк-т нэвтэрнэ үү...", Toast.LENGTH_SHORT).show();
			return;
		} else {
			showWebDialog();
			// makeMeRequest(session);
		}
		bar = ((ActionBarActivity) this).getSupportActionBar();
		bar.setTitle("Фейсбүүк шейр");
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// getMenuInflater().inflate(R.menu.ad_menu, menu);

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

	private void requestPublishPermissions(Session session) {
		if (session != null) {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					this, PERMISSIONS).setDefaultAudience(
					SessionDefaultAudience.FRIENDS).setRequestCode(
					REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void hideKeyboard() {
		if (getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void showWebDialog() {

		Bundle param = new Bundle();
		param.putString("name", getString(R.string.app_name));

		param.putString("caption", getString(R.string.share_caption));
		param.putString("description", b.getString("desc"));
		param.putString("link", link);
		param.putString("picture", b.getString("image"));
		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,
				Session.getActiveSession(), param)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							Toast.makeText(getApplicationContext(),
									"Successfull shared on Facebook",
									Toast.LENGTH_SHORT).show();

						} else if (error instanceof FacebookOperationCanceledException) {
						} else {
							Toast.makeText(getApplicationContext(), "Error",
									Toast.LENGTH_SHORT).show();

						}
						finish();
					}

				}).build();
		feedDialog.show();

	}
}
