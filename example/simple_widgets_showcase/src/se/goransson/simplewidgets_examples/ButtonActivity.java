package se.goransson.simplewidgets_examples;

import se.goransson.andreas.simplewidgets.ShadedImageButton;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ButtonActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ShadedImageButton[] buttons = new ShadedImageButton[10];
		buttons[0] = (ShadedImageButton) findViewById(R.id.ShadedImageButton00);
		buttons[1] = (ShadedImageButton) findViewById(R.id.ShadedImageButton01);
		buttons[2] = (ShadedImageButton) findViewById(R.id.ShadedImageButton02);
		buttons[3] = (ShadedImageButton) findViewById(R.id.ShadedImageButton03);
		buttons[4] = (ShadedImageButton) findViewById(R.id.ShadedImageButton04);
		buttons[5] = (ShadedImageButton) findViewById(R.id.ShadedImageButton05);
		buttons[6] = (ShadedImageButton) findViewById(R.id.ShadedImageButton06);
		buttons[7] = (ShadedImageButton) findViewById(R.id.ShadedImageButton07);
		buttons[8] = (ShadedImageButton) findViewById(R.id.ShadedImageButton08);
		buttons[9] = (ShadedImageButton) findViewById(R.id.ShadedImageButton09);

		for (int i = 0; i < buttons.length; i++)
			buttons[i].setOnClickListener(this);
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
			break;
		case R.id.PROGRESSBARS:
			Intent progressbars = new Intent(ButtonActivity.this,
					ProgressbarActivity.class);
			startActivity(progressbars);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(this, "Icons from:\nhttp://www.freeiconsdownload.com/",
				Toast.LENGTH_SHORT).show();
	}
}