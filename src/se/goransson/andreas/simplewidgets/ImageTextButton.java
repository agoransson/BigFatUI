package se.goransson.andreas.simplewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import se.goransson.andreas.simplewidgets.R;

/**
 * Custom button that contains an image, and a text.
 * 
 * Add the following namespace attribute to your layout. xmlns:ag_components=
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

	private final static String TAG = "MedeaButton";

	private final static int WIDTH_PADDING = 8;
	private final static int HEIGHT_PADDING = 10;

	private String text;
	private int imageResId;
	private Bitmap image;

	private Paint backgroundPaint, imagePaint;

	private Typeface typeface;
	private Paint textPaint = new Paint();

	private boolean enabled;

	private ColorMatrixColorFilter activeFilter;

	private ColorMatrixColorFilter unactiveFilter;

	public ImageTextButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ImageTextButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		/* Load the values from the xml layout */
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ImageTextButton);

		// Get the text from XML
		CharSequence s = a.getString(R.styleable.ImageTextButton_text);
		if (s != null) {
			setText(s.toString());
		}
		// Get the image from XML
		Drawable imageRes = a.getDrawable(R.styleable.ImageTextButton_image);
		if (imageRes != null)
			image = ((BitmapDrawable) imageRes).getBitmap();

		// Create the typeface
		String font = a.getString(R.styleable.ImageTextButton_text_font);
		int style = a.getInt(R.styleable.ImageTextButton_text_style,
				Typeface.NORMAL);
		typeface = Typeface.create((font != null ? font : "sans"), style);
		int fontsize = a.getInt(R.styleable.ImageTextButton_text_size, 16);

		textPaint.setColor(Color.BLACK);
		textPaint.setTypeface(typeface);
		textPaint.setTextSize(fontsize);

		a.recycle();

		setFocusable(false);
		setFocusableInTouchMode(false);
		setClickable(true);
		setBackgroundColor(Color.TRANSPARENT);

		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.parseColor("#00000000"));

		imagePaint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(1);
		activeFilter = new ColorMatrixColorFilter(cm);

		cm = new ColorMatrix();
		cm.setSaturation(0);
		unactiveFilter = new ColorMatrixColorFilter(cm);

	}

	public ImageTextButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the visible label for this controller.
	 * 
	 * @param s
	 */
	public void setText(String s) {
		this.text = s;
		this.invalidate();
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

	private int measureWidth(int measureSpec) {
		int preferred = (int) (image.getWidth() + textPaint.measureText(text));
		return getMeasurement(measureSpec, preferred);
	}

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
