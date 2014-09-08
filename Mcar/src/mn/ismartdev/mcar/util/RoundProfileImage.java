package mn.ismartdev.mcar.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

public class RoundProfileImage extends NetworkImageView {
	public RoundProfileImage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundProfileImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RoundProfileImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setWillNotDraw(false);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Create a circular path.
		final float halfWidth = canvas.getWidth() / 2;
		final float halfHeight = canvas.getHeight() / 2;
		final float radius = Math.max(halfWidth, halfHeight);
		final Path path = new Path();
		path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW);

		final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawPath(path, paint);
	}
	
}
