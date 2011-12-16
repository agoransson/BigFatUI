package se.goransson.bigfatui_example;

import se.goransson.bigfatui.BigFatTurner;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class TurnerActivity extends Activity {

	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.turners);

		mTextView = (TextView) findViewById(R.id.textView1);
		BigFatTurner mBigFatTurner = (BigFatTurner) findViewById(R.id.bigFatTurner1);
		mBigFatTurner.setHandler(mHandler);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int val = msg.arg1;
			mTextView.setText(Integer.toString(val));
		}

	};
}
