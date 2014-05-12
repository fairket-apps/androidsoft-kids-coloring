package org.androidsoft.coloring.service;


import java.io.IOException;
import java.io.InputStream;

import com.silo.games.coloring.kids.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class FairKetService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams 	mRootLayoutParams;
	private RelativeLayout 	mRootLayout;
	private RelativeLayout mContentContainerLayout;
	private RelativeLayout 	mLogoLayout;
	private static final int TRAY_DIM_X_DP 	= 170;	// Width of the tray in dps
	private static final int TRAY_DIM_Y_DP 	= 160; 	// Height of the tray in dps
	private static final int ANIMATION_FRAME_RATE = 30;
	private static final int TRAY_CROP_FRACTION = 12;
	
	@Override
	public void onCreate() {
		// Get references to all the views and add them to root view as needed.
				mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

				mRootLayout = (RelativeLayout) LayoutInflater.from(this).
						inflate(R.layout.service_player, null);
				mContentContainerLayout = (RelativeLayout) mRootLayout.findViewById(R.id.content_container);
				mLogoLayout = (RelativeLayout) mRootLayout.findViewById(R.id.logo_layout);
				mRootLayoutParams = new WindowManager.LayoutParams(
						dpToPixels(TRAY_DIM_X_DP, getResources()),
						dpToPixels(TRAY_DIM_Y_DP, getResources()),
						WindowManager.LayoutParams.TYPE_PHONE, 
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 
						PixelFormat.TRANSLUCENT);

				mRootLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
				mWindowManager.addView(mRootLayout, mRootLayoutParams);
				
				
				// Post these actions at the end of looper message queue so that the layout is
				// fully inflated once these functions execute
				mRootLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						
						// Reusable variables
						RelativeLayout.LayoutParams params;
						InputStream is;
						Bitmap bmap;
						
						// Setup background spotify logo
						is = getResources().openRawResource(R.drawable.background_land);
						int containerNewWidth = (TRAY_CROP_FRACTION-1)*mLogoLayout.getHeight()/TRAY_CROP_FRACTION;
						bmap = loadMaskedBitmap(is, mLogoLayout.getHeight(), containerNewWidth);
						params = (RelativeLayout.LayoutParams) mLogoLayout.getLayoutParams();
						params.width = (bmap.getWidth() * mLogoLayout.getHeight()) / bmap.getHeight();
						params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
						mLogoLayout.setLayoutParams(params);
						mLogoLayout.requestLayout();
						mLogoLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bmap));
						
						
						
						
						
						
						// Setup the root layout
						mRootLayoutParams.x = -mLogoLayout.getLayoutParams().width;
						mRootLayoutParams.y = (getApplicationContext().getResources().getDisplayMetrics().heightPixels-mRootLayout.getHeight()) / 2;
						mWindowManager.updateViewLayout(mRootLayout, mRootLayoutParams);
						
						// Make everything visible
						mRootLayout.setVisibility(View.VISIBLE);
						
						
					}
				}, ANIMATION_FRAME_RATE);
				
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getBooleanExtra("stop_service", false)){
			// If it's a call from the notification, stop the service.
			stopSelf();
		}else{
			// Make the service run in foreground so that the system does not shut it down.
			Intent notificationIntent = new Intent(this, FairKetService.class);
			notificationIntent.putExtra("stop_service", true);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
			Notification notification = new Notification(
					R.drawable.icon, 
					"Fairket tray launched",
			        System.currentTimeMillis());
			notification.setLatestEventInfo(
					this, 
					"Fairket tray",
			        "Tap to close the widget.", 
			        pendingIntent);
			startForeground(86, notification);
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		if (mRootLayout != null)
			mWindowManager.removeView(mRootLayout);
	}
	
	
	public static int dpToPixels(int dp, Resources res){
		return (int)(res.getDisplayMetrics().density*dp + 0.5f);
	}
	
	public static Bitmap loadMaskedBitmap(InputStream iStream, int containerHeight, int containerWidth){
		
		// Load image data before loading the image itself.
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		
		BitmapFactory.decodeStream(iStream,null,bmOptions);
		
		int photoH = bmOptions.outHeight;
		int photoW = bmOptions.outWidth;
		
		// Find a suitable scalefactor to load the smaller bitmap, and then set the options.
		int scaleFactor = Math.min(photoH/containerHeight, photoW/containerHeight);
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		// Load the square region out of the bitmap.
		Bitmap bmap=null;
		BitmapRegionDecoder decoder;
		try {
			iStream.reset();
			decoder = BitmapRegionDecoder.newInstance(iStream, false);
			bmap = decoder.decodeRegion(new Rect(0, 0, Math.min(photoH, photoW), Math.min(photoH, photoW)),bmOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Calculate new width of the bitmap based on the width of the container
		int bitmapNewWidth = (bmap.getWidth()*containerWidth)/containerHeight;   
		
		// Produce clipping mask on the canvas and draw the bitmap 
		Bitmap targetBitmap = Bitmap.createBitmap(bitmapNewWidth, bmap.getHeight(), bmap.getConfig());
		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(bmap.getWidth() / 2, bmap.getHeight() / 2, bmap.getWidth() / 2, Path.Direction.CCW);
		canvas.clipPath(path);
		canvas.drawBitmap(bmap, 0, 0, null);

		// Retrieve the clipped bitmap and return
		return targetBitmap;
	}

}
