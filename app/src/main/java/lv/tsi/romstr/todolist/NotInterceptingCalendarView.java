package lv.tsi.romstr.todolist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.CalendarView;

/**
 * Created by Roman on 03.12.14..
 */
public class NotInterceptingCalendarView extends CalendarView {
    public NotInterceptingCalendarView(Context context) {
        super(context);
    }

    public NotInterceptingCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotInterceptingCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}
