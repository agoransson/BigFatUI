package se.goransson.simplewidgets_examples;

import se.goransson.andreas.simplewidgets.BigSlider;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class SliderActivity extends Activity {

	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sliders);

		textView = (TextView) findViewById(R.id.textView1);
		
		BigSlider slider = (BigSlider) findViewById(R.id.bigSlider1);
		slider.setCallbackHandler(mHandler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent mIntent = null;
		switch (item.getItemId()) {
		case R.id.BUTTONS:
			mIntent = new Intent(SliderActivity.this, ButtonActivity.class);
			break;
		case R.id.PROGRESSBARS:
			mIntent = new Intent(SliderActivity.this, ButtonActivity.class);
			break;
		case R.id.SLIDERS:
			// Nothing to do...
			break;
		}
		if (mIntent != null)
			startActivity(mIntent);
		return super.onOptionsItemSelected(item);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BigSlider.CALLBACK:
				// arg1 contains the value!
				textView.setText(Integer.toString(msg.arg1));
				break;
			}
		}

	};
}