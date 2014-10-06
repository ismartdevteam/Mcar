package mn.ismartdev.mcar;

import mn.ismartdev.mcar.util.FacebookLogin;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ShareDialog extends DialogFragment implements OnClickListener {
	private Bundle b;
	private ImageView fb;
	private ImageView twit;
	View v;

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		b = getArguments();
		setStyle(DialogFragment.STYLE_NO_FRAME,
				android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.share_main, container, false);

		fb = (ImageView) v.findViewById(R.id.share_fb);
		twit = (ImageView) v.findViewById(R.id.share_tw);
		fb.setOnClickListener(this);
		twit.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == fb) {
			Intent fbIntent = new Intent(getActivity(), FacebookLogin.class);
			fbIntent.putExtras(b);
			startActivity(fbIntent);
		}
		dismiss();
	}

}
