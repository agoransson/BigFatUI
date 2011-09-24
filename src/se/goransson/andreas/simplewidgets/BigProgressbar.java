package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BigProgressbar extends View {

	private static final String TAG = "BigProgressbar";

	private Context mContext;

	// Progressbar text
	private String progress_text;
	private int progress_font_size = 100;
	private int padding_x = 0;
	private int padding_y = 0;

	// Progressbar values
	private int max = 100;
	private int current = 0;

	// Graphics
	private RectF progress_bar;
	private Paint progress_bar_paint;
	private Paint progress_text_paint;
	private boolean countdown = false;

	public BigProgressbar(Context context) {
		super(context);

		mContext = context;

		if (isInEditMode()) {
			setup(null);
			updateValue(33);
		} else {
			setup(null);
		}
	}

	public BigProgressbar(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		if (isInEditMode()) {
			setup(attrs);
			updateValue(33);
		} else {
			setup(attrs);
		}
	}

	public BigProgressbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;

		if (isInEditMode()) {
			setup(attrs);
			updateValue(33);
		} else {
			setup(attrs);
		}
	}

	private void setup(AttributeSet attrs) {
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bigprogressbarbackground));

		progress_bar = new RectF(0, 0, 0, 0);
		progress_bar_paint = new Paint();
		progress_bar_paint.setColor(Color.GREEN);

		progress_text_paint = new Paint();
		progress_text_paint.setColor(Color.WHITE);
		progress_text_paint.setTextSize(progress_font_size);
		progress_text_paint.setTextAlign(Align.CENTER);
		progress_text_paint.setTypeface(Typeface.DEFAULT_BOLD);
		progress_text = new String(current + "%");

		FontMetrics fm = progress_text_paint.getFontMetrics();
		padding_y = (int) fm.descent;
		padding_x = 0;

		/* Try loading xml attrs (if any set they should overwrite the default) */
		if (attrs != null) {
			TypedArray xml_attrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.BigProgressbar);

			// Load bar color
			progress_bar_paint.setColor(xml_attrs.getColor(
					R.styleable.BigProgressbar_barcolor, Color.GREEN));

			// Load text size
			progress_text_paint.setTextSize(progress_font_size = xml_attrs.getInt(
					R.styleable.BigProgressbar_textsize, progress_font_size));
			// ...and update padding
			fm = progress_text_paint.getFontMetrics();
			padding_y = (int) fm.descent;
			padding_x = 0;

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
		int preferred = (int) (progress_text_paint.measureText(progress_text) + (2 * padding_x));
		return getMeasurement(measureSpec, preferred);
	}

	/**
	 * Returns the actual height of the progressbar.
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int preferred = (int) (progress_font_size + padding_y);
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
		canvas.drawRoundRect(progress_bar, 5, 5, progress_bar_paint);

		canvas.drawText(progress_text, getWidth() / 2 + padding_x,
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
	 * @param newValue
	 */
	public void updateValue(int newValue) {
		float quota = (float) newValue / max;

		if (!countdown) {
			progress_bar.set(0, 0, getWidth() * quota, getHeight());
			progress_text = Integer.toString((int) (100 * quota)) + "%";
		} else {
			progress_bar.set(0, 0, getWidth() - (getWidth() * quota), getHeight());
			progress_text = Integer.toString(100 - (int) (100 * quota)) + "%";
		}

		invalidate();
	}

	int i = 0;
}
