/* Copyright (c) 2010-2011 Pierre LEVY androidsoft.org
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
package org.androidsoft.coloring.ui.activity;

import org.androidsoft.utils.ui.WhatsNewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.fairket.app.coloring.kids.R;
import com.fairket.sdk.android.FairketApiClient;
import com.fairket.sdk.android.FairketAppTimeHelper;
import com.tapjoy.TapjoyConnect;

/**
 * Splash activity
 * 
 * @author Pierre Levy
 */
public class SplashActivity extends WhatsNewActivity implements OnClickListener {

	public static final String FAIRKET_LOG_TAG = "FairketKidsMemory";

	// Integ
	 public static final String FAIRKET_APP_PUBLIC_KEY =
	 "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy287zl5teujoHKly7GbcTqIzC6gsKA3ZeYL3Kxxk7hDiKml6F7uzKwHJlLe21Ritj/NQiw0FUvRWAt4wyMshV+W4/Eesg2HjB840TxCWPAyZ84+EtsmJfL9uX7sdbFbXVQTGknAqol4X9trwsTKQzTauxMP1gCsBuihLV6QreKaL5VY8dmsVP/kYJ591K28adoULdpHqH35LGuDyzQDrXaqT3f0T8HEO0Xbqnjxcnv/90gdYMDkZHpj2F1aFbsk5S0V3m0yCfGWB/NpIq2FIpD0rjSgL/PZSIaTIuNj7p2YEf4CI71cVWmjxp1SoGUR2h/BPO8rAeANwXiBBaWOrFQIDAQAB";
	// Prod
	//public static final String FAIRKET_APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhoQ3MDHtRJhXpZp3lKiYRU+/Y4jLeWXmp0LBuEFzt8buiKBAxm7A0+BmAf/r9DrD6q9Djs5erL2Og5NU58RXKmARiNJtspdKmz5LQH7x6Z3Ay2m6UeCJhJOP1IkYOqH9M6kFGt2tWkk120GS12q1LEbM+agKlJ4mkOQfeBtjHLJBFXtd5QFTK0ZbhcSKCLwaBXMzCRRWzzdZ2FJwtFHSsSfs8+RSAo5xY/gmSYovkm8wfgFKaqB1xz75AIE4SNDRHTaQwESgOM1PDrGEOadhElgKldID3OAbACRRaB6LDy8Gb9T1dncR3tgGgdbUlHyRsKmKwDWSP5Sgmb0dVe+eIwIDAQAB";

	private Button mButtonPlay;
	private FairketApiClient mFaiirketApiClient;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.splash);

		mButtonPlay = (Button) findViewById(R.id.button_go);
		mButtonPlay.setOnClickListener(this);

		ImageView image = (ImageView) findViewById(R.id.image_splash);
		image.setImageResource(R.drawable.splash);

		mFaiirketApiClient = FairketAppTimeHelper.onCreate(this,
				SplashActivity.FAIRKET_APP_PUBLIC_KEY,
				SplashActivity.FAIRKET_LOG_TAG);

		// Tapjoy connect
		String tapjoyAppID = "85f873dc-78b6-4a86-b252-43d341741a56";
		String tapjoySecretKey = "xPtHrHetEUG5iRwSDuXS";
		TapjoyConnect.requestTapjoyConnect(this, tapjoyAppID, tapjoySecretKey);

	}

	/**
	 * {@inheritDoc }
	 */
	public void onClick(View v) {
		if (v == mButtonPlay) {
			Intent intent = new Intent(this, PaintActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public int getFirstRunDialogTitleRes() {
		return R.string.first_run_dialog_title;
	}

	@Override
	public int getFirstRunDialogMsgRes() {
		return R.string.first_run_dialog_message;
	}

	@Override
	public int getWhatsNewDialogTitleRes() {
		return R.string.whats_new_dialog_title;
	}

	@Override
	public int getWhatsNewDialogMsgRes() {
		return R.string.whats_new_dialog_message;
	}

	@Override
	protected void onPause() {
		super.onPause();

		FairketAppTimeHelper.onPause(mFaiirketApiClient);
	}

	@Override
	protected void onResume() {
		super.onResume();

		FairketAppTimeHelper.onResume(mFaiirketApiClient);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		FairketAppTimeHelper.onDestroy(mFaiirketApiClient);
	}
}
