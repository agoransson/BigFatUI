package se.goransson.bigfatui_example;

import se.goransson.bigfatui.BigFatProgressbar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProgressbarActivity extends Activity {

	BigFatProgressbar small, big;

	BigFatProgressbar reversed1, reversed2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressbars);

		small = (BigFatProgressbar) findViewById(R.id.bigFatProgressbar1);
		big = (BigFatProgressbar) findViewById(R.id.bigFatProgressbar2);

		reversed1 = (BigFatProgressbar) findViewById(R.id.bigFatProgressbar3);
		reversed2 = (BigFatProgressbar) findViewById(R.id.bigFatProgressbar4);
	}

	@Override
	protected void onResume() {
		new MyTask().execute();
		super.onResume();
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
			mIntent = new Intent(ProgressbarActivity.this, ButtonActivity.class);
			break;
		case R.id.PROGRESSBARS:
			// Nothing to do...
			break;
		case R.id.SLIDERS:
			mIntent = new Intent(ProgressbarActivity.this, SliderActivity.class);
			break;
		}
		if (mIntent != null)
			startActivity(mIntent);
		return super.onOptionsItemSelected(item);
	}

	private class MyTask extends AsyncTask<Void, Integer, Void> {
		int i = 0;

		@Override
		protected Void doInBackground(Void... params) {
			while (i < 100) {
				this.publishProgress(++i);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			small.setProgress(values[0]);
			big.setProgress(values[0]);

			reversed1.setProgress(values[0]);
			reversed2.setProgress(values[0]);

			super.onProgressUpdate(values);
		}
	};
}
