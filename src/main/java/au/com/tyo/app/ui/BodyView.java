package au.com.tyo.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class BodyView extends FrameLayout {
	
	public BodyView(Context context) {
		super(context);
	}

	public BodyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public BodyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean dispatchTouchEvent (MotionEvent ev) {
	   for(int i = 0; i < this.getChildCount(); i++){
		   View v = this.getChildAt(i);
		   if (v.getVisibility() == View.VISIBLE)
			   v.dispatchTouchEvent(ev);
	   } 
		
	   return super.dispatchTouchEvent(ev);
	}
}
