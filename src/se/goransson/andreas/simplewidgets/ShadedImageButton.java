package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

	public ShadedImageButton(Context context) {
		this(context, null);
	}

	public ShadedImageButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShadedImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		setup(attrs);

		if (isInEditMode()) {
			// Default values
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
			// Nothing
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
}
