package se.goransson.bigfatui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
 * 
 * @author Andreas Göransson
 * 
 */
public class BigFatTurner extends View {

	private static final String TAG = "BigFatTurner";

	private Context mContext;

	private Drawable handle;

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

		handle = mContext.getResources().getDrawable(R.drawable.bigturner_handle);

		if (attrs != null) {
			// Load custom user-specified values
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.BigFatTurner);

			if (xml_attrs.getDrawable(R.styleable.BigFatTurner_handle) != null)
				handle = xml_attrs.getDrawable(R.styleable.BigFatTurner_handle);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			/* Filters are not supported in edit mode */
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));
		}

		handle.draw(canvas);

		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
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
			// Take the minimum of the preferred size and what we were told to be.
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}
}
