package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
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
 * A big fat progressbar!
 * 
 * @author Andreas Göransson
 * 
 */
public class BigProgressbar extends View {

	private static final String TAG = "BigProgressbar";

	private Context mContext;

	// Progressbar text
	private String progress_text;
	private int progress_font_size = 100;
	private int text_padding_x = 0;
	private int text_padding_y = 0;

	// Progressbar values
	private int max = 100;
	private int current = 0;

	// Graphics
	private Drawable progress;
	private Paint progress_text_paint;
	private boolean countdown = false;

	public BigProgressbar(Context context) {
		this(context, null);
	}

	public BigProgressbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BigProgressbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;

		setup(attrs);

		if (isInEditMode()) {
			setProgress(33);
		}
	}

	/**
	 * Initialize the view
	 * 
	 * @param attrs
	 */
	private void setup(AttributeSet attrs) {
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bigbackground));

		// Progressbar settings
		progress = mContext.getResources().getDrawable(R.drawable.bigprogressbar);

		// Text settings
		progress_text_paint = new Paint();
		progress_text_paint.setColor(Color.WHITE);
		progress_text_paint.setTextSize(progress_font_size);
		progress_text_paint.setTextAlign(Align.CENTER);
		progress_text_paint.setTypeface(Typeface.DEFAULT_BOLD);
		progress_text = new String(current + "%");

		// ...more text settings (font-size)
		FontMetrics fm = progress_text_paint.getFontMetrics();
		text_padding_y = (int) fm.descent;
		text_padding_x = 0;

		/* Try loading xml attrs (if any set they should overwrite the default) */
		if (attrs != null) {
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.BigProgressbar);

			// Load progress drawable
			progress = mContext.getResources().getDrawable(
					xml_attrs.getInt(R.styleable.BigProgressbar_drawable,
							R.drawable.bigprogressbar));
			// ...and color (if specified)
			int color = xml_attrs.getInt(R.styleable.BigProgressbar_color,
					Color.GREEN);

			// Load text size
			progress_text_paint.setTextSize(progress_font_size = xml_attrs.getInt(
					R.styleable.BigProgressbar_textsize, progress_font_size));
			// ...and update text padding
			fm = progress_text_paint.getFontMetrics();
			text_padding_y = (int) fm.descent;
			text_padding_x = 0;

			// Reversed?
			countdown = xml_attrs.getBoolean(R.styleable.BigProgressbar_countdown,
					false);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Returns the actual width of the progressbar.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int preferred = (int) (progress_text_paint.measureText(progress_text) + (2 * text_padding_x));
		return getMeasurement(measureSpec, preferred);
	}

	/**
	 * Returns the actual height of the progressbar.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int preferred = (int) (progress_font_size + text_padding_y);
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

		// Draw progress
		progress.draw(canvas);

		// Draw text
		canvas.drawText(progress_text, getWidth() / 2 + text_padding_x,
				progress_font_size, progress_text_paint);
		super.onDraw(canvas);
	}

	/**
	 * Set maximum value.
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Set the new current value.
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		float quota = (float) progress / max;
		current = (int) (100 * quota);

		// Used for EditMode (No need to do the calculation if not in editmode)
		int w = 0, h = 0;
		if (isInEditMode()) {
			w = (int) (progress_text_paint.measureText(progress_text) + (2 * text_padding_x));
			h = (int) (progress_font_size + text_padding_y);
		}

		if (!countdown) {
			this.progress.setBounds(3, 3,
					(int) (((isInEditMode() ? w : getWidth()) * quota) - 3),
					(isInEditMode() ? h : getHeight()) - 3);
			progress_text = Integer.toString(current) + "%";
		} else {
			this.progress.setBounds(3, 3,
					(int) (((isInEditMode() ? w : getWidth()) - ((isInEditMode() ? w
							: getWidth()) * quota)) - 3),
					(isInEditMode() ? h : getHeight()) - 3);
			progress_text = Integer.toString(100 - current) + "%";
		}

		invalidate();
	}

	int i = 0;
}
