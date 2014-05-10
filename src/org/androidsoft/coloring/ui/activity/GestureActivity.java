package org.androidsoft.coloring.ui.activity;

import java.util.ArrayList;

import com.silo.games.coloring.kids.R;

import android.app.Activity;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GestureActivity {
	
	
	public static View addGestureView(int resId, Activity activity){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View screenView = inflater.inflate(resId, null);

		return addGestureView(screenView, activity);
	}
	
	public static View addGestureView(View screenView, final Activity activity){
		LinearLayout screenRootView = new LinearLayout(activity);
        screenRootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        screenRootView.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GestureOverlayView gOverlay = (GestureOverlayView) inflater.inflate(R.layout.activity_custom_gestures, null);

        gOverlay.addOnGesturePerformedListener(new OnGesturePerformedListener(){

			@Override
			public void onGesturePerformed(GestureOverlayView overlay,
					Gesture gesture) {
				ArrayList<Prediction> predictions = getGLibrary(activity).recognize(gesture);
		        
		        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
		            
		        	String action = predictions.get(0).name;
		        	
		            Toast.makeText(activity, action+" Detected", Toast.LENGTH_SHORT).show();
		        }
				
			}
        	
        });

        screenRootView.addView(gOverlay);
        screenRootView.addView(screenView);
        
        return screenRootView;
	}
	
	private static GestureLibrary getGLibrary(Activity activity){
		GestureLibrary gLibrary = GestureLibraries.fromRawResource(activity, R.raw.gestures);
	    if (!gLibrary.load()) {
	    	activity.finish();
	    } 
	    return gLibrary;
	}

}
