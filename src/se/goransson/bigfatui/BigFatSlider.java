package se.goransson.bigfatui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
 * A big fat slider!
 * 
 * @author Andreas Göransson
 * 
 */
public class BigFatSlider extends View {

	@SuppressWarnings("unused")
	private static final String TAG = "BigSlider";

	private Context mContext;

	// Slider control
	private int min = 0, max = 100;
	private int value;

	// Slider
	private Drawable slider;

	// Slider fill
	private Drawable fill;

	// Attributes
	private int border = 3;

	// Callback handler
	public static final int CALLBACK = 27452; // Just some semi-random numbers...
	private Handler callback_handler;

	public BigFatSlider(Context context) {
		this(context, null);
	}

	public BigFatSlider(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BigFatSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		if (isInEditMode()) {
			// Load default editor values
		}

		setup(attrs);

		setValue(value);
	}

	private void setup(AttributeSet attrs) {
		// Set background (using the same as BigProgressbar)
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bigbackground_rect));

		// Slider & fill
		slider = mContext.getResources().getDrawable(R.drawable.bigslider);
		fill = mContext.getResources().getDrawable(R.drawable.bigslider_fill);

		/* Load XML attributes */
		if (attrs != null) {
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.BigFatSlider);

			// Fill
			if (xml_attrs.getDrawable(R.styleable.BigFatSlider_filldrawable) != null)
				fill = xml_attrs.getDrawable(R.styleable.BigFatSlider_filldrawable);

			// Slider
			if (xml_attrs.getDrawable(R.styleable.BigFatSlider_sliderdrawable) != null)
				slider = xml_attrs.getDrawable(R.styleable.BigFatSlider_sliderdrawable);

			// range
			max = xml_attrs.getInt(R.styleable.BigFatSlider_max, 100);
			min = xml_attrs.getInt(R.styleable.BigFatSlider_min, 0);

			// Value
			value = xml_attrs.getInt(R.styleable.BigFatSlider_value, 0);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		fill.setBounds(border, border, w - border, h - border);

		int x = (int) map(this.value, min, max, border + slider.getIntrinsicWidth()
				/ 2, getWidth() - border - slider.getIntrinsicWidth() / 2);
		slider.setBounds(x - slider.getBounds().width() / 2, border, x
				+ slider.getBounds().width() / 2, h - border);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Returns the actual width of the slider.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int preferred = getBackground().getIntrinsicWidth();
		return getMeasurement(measureSpec, preferred);
	}

	/**
	 * Returns the actual height of the slider.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int preferred = getBackground().getIntrinsicHeight();
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
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			/* Filters are not supported in edit mode */
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));
		}

		// Draw fill
		fill.draw(canvas);

		// Draw slider
		slider.draw(canvas);

		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setValue((int) map(event.getX(), 0, getWidth(), min, max));
		// This is end-of-the-line for this event!
		return true;
	}

	/**
	 * Set a new value range for this slider.
	 * 
	 * @param min
	 * @param max
	 */
	public void setRange(int min, int max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Set the position of the slider. This is in relation to the value range
	 * defined, default is 0-100. If the value exceeds the range in either
	 * direction, the value will be clamped to the range min/max.
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = (value < min ? min : (value > max ? max : value));

		// Send new value to listeners
		if (callback_handler != null)
			callback_handler.obtainMessage(CALLBACK, (int) this.value, 0)
					.sendToTarget();

		// Update visual representation
		int x = (int) map(this.value, min, max, border + slider.getIntrinsicWidth()
				/ 2, getWidth() - border - slider.getIntrinsicWidth() / 2);

		slider.setBounds(x - slider.getIntrinsicWidth() / 2, border,
				x + slider.getIntrinsicWidth() / 2, getHeight() - border);

		invalidate();
	}

	/**
	 * HA! Stole this one from Ben&co. over at Processing... in yo face, I'm an
	 * outlaw!!
	 * 
	 * @param value
	 * @param istart
	 * @param istop
	 * @param ostart
	 * @param ostop
	 * @return
	 */
	static public final float map(float value, float istart, float istop,
			float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

	/**
	 * Get the current value.
	 * 
	 * @return
	 */
	public float getValue() {
		return value;
	}

	/**
	 * Register the handler which will recieve the callbacks from this component.
	 * Callbacks are retrieved using the constant "BigSlider.CALLBACK".
	 * 
	 * @param handler
	 */
	public void setCallbackHandler(Handler handler) {
		this.callback_handler = handler;
	}
}
