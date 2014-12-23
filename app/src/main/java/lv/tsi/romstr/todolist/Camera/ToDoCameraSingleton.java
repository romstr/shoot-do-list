package lv.tsi.romstr.todolist.Camera;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Roman on 14.12.14..
 */
public class ToDoCameraSingleton {

    private static ToDoCamera camera;

    public static ToDoCamera getSingleton(Context context, Activity activity) {
        if (camera == null) {
            camera = new ToDoCamera(context, activity);
        } else {
            camera.updateSettings(context, activity);
        }
        return camera;
    }

}
