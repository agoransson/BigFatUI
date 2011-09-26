package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
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
public class BigSlider extends View {

	private static final String TAG = "BigSlider";

	private Context mContext;

	// Slider control
	private float min = 0, max = 100;
	private float value;
	private float slider_x;
	private float slider_w = 10;
	private RectF sliderControl;
	private Paint sliderControl_paint;

	// Slider fill
	private RectF sliderFill;
	private Paint sliderFill_paint;

	// Attributes
	private int border = 3;
	private boolean drawLine = false;
	private int backgroundColor = Color.GREEN;
	private int foregroundColor = Color.WHITE;
	private float radialsize = 150.0f;

	// Callback handler
	public static final int CALLBACK = 27452; // Just some semi-random numbers...
	private Handler callback_handler;

	public BigSlider(Context context) {
		this(context, null);
	}

	public BigSlider(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BigSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		if (isInEditMode()) {
			// Load default editor values
		}

		setup(attrs);

		if (isInEditMode())
			setValue(50);
		else
			setValue(0);
	}

	private void setup(AttributeSet attrs) {
		// Set background (using the same as BigProgressbar)
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bigprogressbarbackground));

		// Slider control
		slider_x = 0;
		slider_w = 5;
		sliderControl = new RectF();
		sliderControl_paint = new Paint();
		sliderControl_paint.setColor(Color.BLACK);

		// Slider fill
		sliderFill = new RectF();
		sliderFill_paint = new Paint();

		/* Load XML attributes */
		if (attrs != null) {
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.BigSlider);

			// Draw black line?
			drawLine = xml_attrs.getBoolean(R.styleable.BigSlider_drawline, false);

			// Colors
			backgroundColor = xml_attrs.getColor(R.styleable.BigSlider_background,
					Color.GREEN);
			foregroundColor = xml_attrs.getColor(R.styleable.BigSlider_foreground,
					Color.WHITE);

			// range
			max = xml_attrs.getInt(R.styleable.BigSlider_max, 100);
			min = xml_attrs.getInt(R.styleable.BigSlider_min, 0);

			// Radial
			radialsize = xml_attrs.getFloat(R.styleable.BigSlider_radialsize, 150.0f);
		}
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
		int preferred = (int) (200);
		return getMeasurement(measureSpec, preferred);
	}

	/**
	 * Returns the actual height of the slider.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int preferred = (int) (50);
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

		// Draw fill-gradient
		RadialGradient gradient = new RadialGradient(
				getPositionFromValue(getValue()), getHeight() / 2, radialsize,
				foregroundColor, backgroundColor, Shader.TileMode.CLAMP);
		sliderFill_paint.setShader(gradient);
		canvas.drawRoundRect(sliderFill, 5, 5, sliderFill_paint);

		// Draw slider control
		if (drawLine)
			canvas.drawRoundRect(sliderControl, 3, 3, sliderControl_paint);

		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setValue(getValueFromPosition(event.getX()));
		// This is end-of-the-line for this event!
		return true;
	}

	/**
	 * Set a new value range for this slider.
	 * 
	 * @param min
	 * @param max
	 */
	public void setRange(float min, float max) {
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
	public void setValue(float value) {
		this.value = (value < min ? min : (value > max ? max : value));

		if (callback_handler != null)
			callback_handler.obtainMessage(CALLBACK, (int) this.value, 0)
					.sendToTarget();

		float x = getPositionFromValue(this.value);
		float y = 0 + border;

		if (drawLine)
			sliderControl.set(x - slider_w / 2, y, x + slider_w / 2, y
					+ (isInEditMode() ? 90 : getHeight()) - 2 * border);

		sliderFill.set((int) (border), border, (int) (getWidth() - border),
				getHeight() - border);
		invalidate();
	}

	private float getValueFromPosition(float position) {
		return (position / ((isInEditMode() ? 400 : getWidth()) - 2 * border))
				* (max - min) - min;
	}

	private float getPositionFromValue(float value) {
		return border
				+ slider_w
				/ 2
				+ (value / (max - min) * ((isInEditMode() ? 400 : getWidth()) - 2
						* border - slider_w));
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
	 * Get the value in relation to the range! Should return 0.0 - 1.0
	 * 
	 * @return
	 */
	public float getValueQuota() {
		return 0;
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
