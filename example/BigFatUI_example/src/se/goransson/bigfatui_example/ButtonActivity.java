package se.goransson.bigfatui_example;

import se.goransson.bigfatui.BigFatImageButton;
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
		setContentView(R.layout.buttons);

		BigFatImageButton[] buttons = new BigFatImageButton[10];
		buttons[0] = (BigFatImageButton) findViewById(R.id.BigFatImageButton00);
		buttons[1] = (BigFatImageButton) findViewById(R.id.BigFatImageButton01);
		buttons[2] = (BigFatImageButton) findViewById(R.id.BigFatImageButton02);
		buttons[3] = (BigFatImageButton) findViewById(R.id.BigFatImageButton03);
		buttons[4] = (BigFatImageButton) findViewById(R.id.BigFatImageButton04);
		buttons[5] = (BigFatImageButton) findViewById(R.id.BigFatImageButton05);
		buttons[6] = (BigFatImageButton) findViewById(R.id.BigFatImageButton06);
		buttons[7] = (BigFatImageButton) findViewById(R.id.BigFatImageButton07);
		buttons[8] = (BigFatImageButton) findViewById(R.id.BigFatImageButton08);
		buttons[9] = (BigFatImageButton) findViewById(R.id.BigFatImageButton09);

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
		Intent mIntent = null;
		switch (item.getItemId()) {
		case R.id.BUTTONS:
			// Nothing to do...
			break;
		case R.id.PROGRESSBARS:
			mIntent = new Intent(ButtonActivity.this, ProgressbarActivity.class);
			break;
		case R.id.SLIDERS:
			mIntent = new Intent(ButtonActivity.this, SliderActivity.class);
			break;
		}
		if (mIntent != null)
			startActivity(mIntent);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(this, "Icons from:\nhttp://www.freeiconsdownload.com/",
				Toast.LENGTH_SHORT).show();
	}
}