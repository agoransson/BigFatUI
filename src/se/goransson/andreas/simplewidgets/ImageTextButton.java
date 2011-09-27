package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/*
 * A basic library of custom android views (widgets, components, whatever you want to call them)
 * Copyright (C) 2011  Andreas Göransson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Custom button that contains an image, and a text.
 * 
 * Add the following namespace attribute to your layout. xmlns:simplewidgets=
 * "http://schemas.android.com/apk/res/<INSERT_YOUR_APPS_PACKAGE_NAME_HERE>"
 * 
 * XML Layout attributes for this component:
 * 
 * To define a drawable resource for your button:
 * simplewidgets:image="@drawable/image_resource_identifier"
 * 
 * To define a text for your button: simplewidgets:text="some text"
 * 
 * To define font for the text: simplewidgets:text_font="serif"
 * 
 * To define a font style: simplewidgets:text_style="Typeface.BOLD"
 * 
 * @author Andreas Göransson
 * 
 */
public class ImageTextButton extends View {

	private final static String TAG = "ImageTextButton";

	private Context mContext;

	private final static int WIDTH_PADDING = 8;
	private final static int HEIGHT_PADDING = 10;

	private String text;
	private int imageResId;
	private Bitmap image;

	private Paint backgroundPaint, imagePaint, textPaint;

	private ColorMatrixColorFilter activeFilter;
	private ColorMatrixColorFilter unactiveFilter;

	public ImageTextButton(Context context) {
		this(context, null);
	}

	public ImageTextButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageTextButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		setup(attrs);

		if (isInEditMode()) {
			// Default editor values
		}
	}

	/**
	 * Initialize the button.
	 * 
	 * @param attrs
	 */
	private void setup(AttributeSet attrs) {

		// Set background color
		setBackgroundColor(Color.TRANSPARENT);
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.parseColor("#00000000"));

		// Image
		imagePaint = new Paint();
		if (!isInEditMode()) {
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(1);
			activeFilter = new ColorMatrixColorFilter(cm);
			cm = new ColorMatrix();
			cm.setSaturation(0);
			unactiveFilter = new ColorMatrixColorFilter(cm);
		}

		/* Load the values from the xml layout */
		if (attrs != null) {
			TypedArray attributes = mContext.obtainStyledAttributes(attrs,
					R.styleable.ImageTextButton);

			// Image
			image = ((BitmapDrawable) attributes
					.getDrawable(R.styleable.ImageTextButton_image)).getBitmap();

			// Text
			setText(attributes.getString(R.styleable.ImageTextButton_text) != null ? attributes
					.getString(R.styleable.ImageTextButton_text) : "a button");
			textPaint = new Paint();
			textPaint.setColor(Color.BLACK);
			textPaint.setTypeface(Typeface.SANS_SERIF);
			textPaint.setTextSize(attributes.getInt(
					R.styleable.ImageTextButton_text_size, 16));

			attributes.recycle();
		}

		setEnabled(true);
	}

	/**
	 * Sets the visible label for this controller.
	 * 
	 * @param s
	 */
	public void setText(String s) {
		this.text = s;
		invalidate();
	}

	@Override
	public void setEnabled(boolean enabled) {
		setFocusable(false);
		setFocusableInTouchMode(false);
		setClickable(enabled);

		if (!enabled) {
			textPaint.setColor(Color.GRAY);
			imagePaint.setColorFilter(unactiveFilter);
		} else {
			textPaint.setColor(Color.BLACK);
			backgroundPaint.setColor(Color.parseColor("#00000000"));
			imagePaint.setColorFilter(activeFilter);
		}

		invalidate();
	}

	/**
	 * Method called on to render the view.
	 */
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			/* Filters are not supported in edit mode */
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));
		}

		canvas.drawRoundRect(new RectF(0, 0, this.getWidth(), this.getHeight()), 5,
				5, backgroundPaint);

		canvas.drawBitmap(image, WIDTH_PADDING / 2, HEIGHT_PADDING / 2, imagePaint);

		canvas.drawText(text, image.getWidth(),
				(HEIGHT_PADDING / 2) + image.getHeight() / 2, textPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isEnabled() && isClickable()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				backgroundPaint.setColor(Color.parseColor("#99000000"));
				break;
			case MotionEvent.ACTION_MOVE:
				backgroundPaint.setColor(Color.parseColor("#44000000"));
				break;
			case MotionEvent.ACTION_UP:
				backgroundPaint.setColor(Color.parseColor("#00000000"));
				break;
			}
			invalidate();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Actual width of button content.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int preferred = (int) (image.getWidth() + textPaint.measureText(text));
		return getMeasurement(measureSpec, preferred);
	}

	/**
	 * Actual height of button content.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int preferred = image.getHeight();
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

	/**
	 * Returns the label of the button.
	 */
	public String getLabel() {
		return text;
	}

	/**
	 * Returns the resource id of the image.
	 */
	public int getImageResId() {
		return imageResId;
	}
}
