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

    private int x;

    private int y;

    private int screenOffet = 0;

	@SuppressLint("NewApi")
	public BodyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    public int getScreenOffet() {
        return screenOffet;
    }

    public void setScreenOffet(int screenOffet) {
        this.screenOffet = screenOffet;
    }

    public int getScreenX() {
        return x;
    }

    public int getScreenY() {
        return y;
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

    public void detectScreenLocation() {
        int[] xy = new int[2];
        getLocationOnScreen(xy);
        x = xy[0];
        y = xy[1];
    }

    public int getRealY() {
        return y - screenOffet;
    }
}
