package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * A large button with a icon in the center and a transparent background with
 * rounded corners.
 * 
 * @author Andreas Göransson
 * 
 */
public class ShadedImageButton extends View {

	private static final String TAG = "ShadedImageButton";

	private Context mContext;

	/* Icon */
	private Bitmap icon;
	private Paint iconPaint;

	private int padding_icon_x = 10, padding_icon_y = 10;

	public ShadedImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		if (isInEditMode()) {
			setup(attrs);
		} else {
			setup(attrs);
		}
	}

	public ShadedImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		if (isInEditMode()) {
			setup(attrs);
		} else {
			setup(attrs);
		}
	}

	public ShadedImageButton(Context context) {
		super(context);
		mContext = context;

		if (isInEditMode()) {
			setup(null);
		} else {
			setup(null);
		}
	}

	/**
	 * Initialize the ShadedButton with, or without, attributs from XML layout.
	 * 
	 * @param attrs
	 */
	private void setup(AttributeSet attrs) {
		// Allways the same button background.
		this.setBackgroundResource(R.drawable.shadebuttondark);
		iconPaint = new Paint();

		// Set default icon (android icon)
		icon = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.icon);

		/* Init button */
		setFocusable(false);
		setFocusableInTouchMode(false);
		setClickable(true);

		/* Load layout values from XML */
		if (attrs != null) {
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.ShadedImageButton);

			// Load selected icon
			Drawable iconDrawable = xml_attrs
					.getDrawable(R.styleable.ShadedImageButton_icon);
			if (iconDrawable != null)
				icon = ((BitmapDrawable) iconDrawable).getBitmap();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Returns the actual width of the button, which is icon + 20px.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int preferred = (int) (icon.getWidth() + padding_icon_x * 2);
		return getMeasurement(measureSpec, preferred);
	}

	/**
	 * Returns the actual height of the button, which is icon + 20px.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int preferred = (int) (icon.getHeight() + padding_icon_y * 2);
		return getMeasurement(measureSpec, preferred);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;

		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			// This means the width of this view has been given.
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			// Take the minimum of the preferred size and what we were told to be.
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setBackgroundResource(R.drawable.shadebuttonlight);
			break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:
			setBackgroundResource(R.drawable.shadebuttondark);
			break;
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			/* Filters are not supported in edit mode */
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));
		}
		canvas.drawBitmap(icon, padding_icon_x, padding_icon_y, iconPaint);
		super.onDraw(canvas);
	}

	/**
	 * TODO This doesn't quite work yet... it's supposed to detect the average
	 * brightness of the background, and then based on that value the button
	 * should pick the correct background resource, either DARK or LIGHT resource!
	 * 
	 * @return
	 */
	private float getAverageBackgroundColor() {
		int[] pixels;

		View v = getRootView();
		v.setDrawingCacheEnabled(true);
		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		v.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
		v.buildDrawingCache(true);
		Bitmap bitmapFoo = v.getDrawingCache();

		int height = bitmapFoo.getHeight();
		int width = bitmapFoo.getWidth();

		int avg = 0;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				int pixel = bitmapFoo.getPixel(x, y);

				float A = ((float) Color.alpha(pixel) / 255);
				float R = ((float) Color.red(pixel) / 255);
				float G = ((float) Color.green(pixel) / 255);
				float B = ((float) Color.blue(pixel) / 255);

				Log.i("HEJ", "A:" + A + " R:" + R + " G:" + G + " B:" + B);

				avg += (0.2126 * R) + (0.7152 * G) + (0.0722 * B);
			}
		}

		// for (int i = 0; i < pixels.length; i++) {
		// int alpha = pixels[i] >> 24;
		// int R = (pixels[i] & 0x00FF0000) >> 16;
		// int G = (pixels[i] & 0x0000FF00) >> 8;
		// int B = (pixels[i] & 0x000000FF);
		//
		// // (0.2126*R) + (0.7152*G) + (0.0722*B)
		// avg += (0.2126 * (R / 255)) + (0.7152 * (G / 255)) + (0.0722 * (B /
		// 255));
		// }

		return (avg / (width * height));
	}
}
