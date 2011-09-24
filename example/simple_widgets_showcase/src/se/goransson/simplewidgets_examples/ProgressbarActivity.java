package se.goransson.simplewidgets_examples;

import se.goransson.andreas.simplewidgets.BigProgressbar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProgressbarActivity extends Activity {

	BigProgressbar small, big;

	BigProgressbar reversed1, reversed2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_bar);

		small = (BigProgressbar) findViewById(R.id.bigProgressbar1);
		big = (BigProgressbar) findViewById(R.id.bigProgressbar2);

		reversed1 = (BigProgressbar) findViewById(R.id.bigProgressbar3);
		reversed2 = (BigProgressbar) findViewById(R.id.bigProgressbar4);
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
		switch (item.getItemId()) {
		case R.id.BUTTONS:
			Intent progressbars = new Intent(ProgressbarActivity.this,
					ButtonActivity.class);
			startActivity(progressbars);
			break;
		case R.id.PROGRESSBARS:
			break;
		}
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
			small.updateValue(values[0]);
			big.updateValue(values[0]);

			reversed1.updateValue(values[0]);
			reversed2.updateValue(values[0]);
			super.onProgressUpdate(values);
		}
	};
}
