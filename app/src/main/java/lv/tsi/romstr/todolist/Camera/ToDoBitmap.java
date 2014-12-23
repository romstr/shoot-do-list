package lv.tsi.romstr.todolist.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.widget.ImageView;

import lv.tsi.romstr.todolist.R;
import lv.tsi.romstr.todolist.ToDoItem;

/**
 * Created by Roman on 14.12.14..
 */
public class ToDoBitmap {

    private Context context;

    public ToDoBitmap(Context context) {
        this.context = context;
    }

    public void setThumbnail(ToDoItem item) {
        // Get the dimensions of the View

        int icon_size = context.getResources().getDimensionPixelSize(R.dimen.large_icon_size);

        int targetW = icon_size;
        int targetH = icon_size;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(item.getPhotoPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(item.getPhotoPath(), bmOptions);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, icon_size, icon_size);
        if (bitmap != null) {
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);

            Bitmap roundedBitmap = Bitmap.createBitmap(icon_size, icon_size, Bitmap.Config.ARGB_8888);

            RectF rect = new RectF(0, 0, icon_size, icon_size);

            Canvas canvas = new Canvas(roundedBitmap);

            canvas.drawRoundRect(rect, icon_size / 2, icon_size / 2, paint);

            item.setPhoto(roundedBitmap);
        } else {
            item.setPhotoPath(null);
        }
    }

    public void setPhoto(ToDoItem item, ImageView image) {
        // Get the dimensions of the View

        int icon_size = context.getResources().getDimensionPixelSize(R.dimen.large_icon_size);

        int targetWidth = context.getResources().getDisplayMetrics().widthPixels;

        int targetW = icon_size;
        int targetH = icon_size;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(item.getPhotoPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = photoW/targetWidth;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(item.getPhotoPath(), bmOptions);

        image.setImageBitmap(bitmap);
        image.setMaxWidth(bitmap.getWidth());
        image.setMaxHeight(bitmap.getHeight());
    }

    public Bitmap getPhoto(String imagePath) {
        // Get the dimensions of the View

        int icon_size = context.getResources().getDimensionPixelSize(R.dimen.large_icon_size);

        int targetWidth = context.getResources().getDisplayMetrics().widthPixels;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;

        // Determine how much to scale down the image
        int scaleFactor = photoW/targetWidth;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        return bitmap;
    }

}
