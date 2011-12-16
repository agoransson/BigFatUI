package se.goransson.bigfatui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/*
 * A basic library of custom android views (widgets, components, whatever you want to call them)
 * Copyright (C) 2011  Andreas G�ransson
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
 * 
 * @author Andreas G�ransson
 * 
 */
public class BigFatTurner extends View {

	private static final String TAG = "BigFatTurner";

	private static final int VALUES = 12398;

	private Context mContext;

	private Drawable fill, handle;

	// Rotation values
	private double angle_saved = 0;
	private float minAngle = 0.0f;
	private float maxAngle = 360.0f;
	private float rotation;

	// Value, translated from rotation to value range
	private int value = 0;

	// Value range for rotation
	private int MIN_VALUE, MAX_VALUE;

	// The listener
	private Handler activityHandler;

	// The padding between the corner of the component and the visual
	// representation of the turner
	private int padding;

	public BigFatTurner(Context context) {
		this(context, null);
	}

	public BigFatTurner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BigFatTurner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;

		setup(attrs);

		if (isInEditMode()) {
			// Layout editor inits?
		}
	}

	/**
	 * Initialize the view.
	 * 
	 * @param attrs
	 */
	private void setup(AttributeSet attrs) {
		// Load default values (non-xml)
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bigbackground_square));

		fill = mContext.getResources().getDrawable(R.drawable.bigturner_fill);

		handle = mContext.getResources().getDrawable(
				R.drawable.bigturner_handle);

		if (attrs != null) {
			// Load custom user-specified values
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.BigFatTurner);

			if (xml_attrs.getDrawable(R.styleable.BigFatTurner_handle) != null)
				handle = xml_attrs.getDrawable(R.styleable.BigFatTurner_handle);

			if (xml_attrs.getDrawable(R.styleable.BigFatTurner_fill) != null)
				fill = xml_attrs.getDrawable(R.styleable.BigFatTurner_fill);

			// Get the padding value from xml, if it exists
			padding = xml_attrs.getInt(R.styleable.BigFatTurner_padding, 20);

			MIN_VALUE = xml_attrs.getInt(R.styleable.BigFatTurner_min_value, 0);
			MAX_VALUE = xml_attrs.getInt(R.styleable.BigFatTurner_max_value,
					100);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			/* Filters are not supported in edit mode */
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		}

		fill.draw(canvas);

		canvas.save();
		canvas.translate(getWidth() / 2 - handle.getIntrinsicWidth() / 2,
				getHeight() / 2);
		canvas.rotate(rotation);
		// Draw handle
		handle.draw(canvas);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);

		fill.setBounds(padding, padding, width - padding, height - padding);
		handle.setBounds(0, 0, handle.getIntrinsicWidth(),
				handle.getIntrinsicHeight());

		setMeasuredDimension(width, height);
	}

	/**
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int preferred = getBackground().getIntrinsicWidth();
		return getMeasurement(measureSpec, preferred);
	}

	/**
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
			// Take the minimum of the preferred size and what we were told to
			// be.
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}

	/**
	 * Sets the handler for this BigFatTurner, this is where current value will
	 * be returned to any listener.
	 * 
	 * Note: there can only be one listener for each BigFatTurner!
	 * 
	 * @param handler
	 */
	public void setHandler(Handler handler) {
		if (handler != null)
			activityHandler = handler;
	}

	/**
	 * Changes the relative rotation of the handle, this is not the absolute
	 * rotation!
	 * 
	 * @param d_rotation
	 */
	private void changeRotation(double rotation_radians) {
		this.rotation += Math.toDegrees(rotation_radians);

		if (this.rotation <= minAngle)
			this.rotation = minAngle;
		else if (this.rotation >= maxAngle)
			this.rotation = maxAngle;

		// Post invalidation when a new rotation is done
		invalidate();
	}

	/**
	 * Set the absolute rotation of the handle.
	 * 
	 * @param rotation_degrees
	 */
	private void setRotation(float rotation_degrees) {
		this.rotation = rotation_degrees;

		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Set inital angle
			angle_saved = Math.atan2((event.getY() - getHeight() / 2),
					(event.getX() - getWidth() / 2));
			break;

		case MotionEvent.ACTION_MOVE:
			// Increase current angle
			double angle_new = Math.atan2((event.getY() - getHeight() / 2),
					(event.getX() - getWidth() / 2));

			// Correct the angle! (-180 to 180 => 0-360)
			if (angle_new < -Math.PI / 2)
				angle_new = Math.PI + (Math.PI + angle_new);

			changeRotation(angle_new - angle_saved);
			angle_saved = angle_new;

			value = parseValueFromDegrees(rotation);

			break;

		case MotionEvent.ACTION_UP:
			// store the new value
			break;
		}

		if (activityHandler != null) {
			// Send the current values to the activity
			activityHandler.obtainMessage(VALUES, value, (int) rotation)
					.sendToTarget();
		}

		return true;
	}

	private int parseValueFromDegrees(float degrees) {
		return (int) map(degrees, minAngle, maxAngle, MIN_VALUE, MAX_VALUE);
	}

	private float parseDegreesFromValue(long value) {
		return map(value, MIN_VALUE, MAX_VALUE, minAngle, maxAngle);
	}

	/**
	 * Stolen straight from Processing, maps a value in one range to another
	 * range.
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
		return ostart + (ostop - ostart)
				* ((value - istart) / (istop - istart));
	}
}
