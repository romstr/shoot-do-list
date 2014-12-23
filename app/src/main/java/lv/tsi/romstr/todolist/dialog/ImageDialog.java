package lv.tsi.romstr.todolist.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import lv.tsi.romstr.todolist.R;
import lv.tsi.romstr.todolist.Camera.ToDoBitmap;

/**
 * Created by Roman on 14.12.14..
 */
public class ImageDialog extends Activity {


    private ImageView mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("path");

        ToDoBitmap toDoBitmap = new ToDoBitmap(this);

        FrameLayout frame = (FrameLayout) findViewById(R.id.image_dialog_root);

        frame.setBackgroundColor(Color.BLACK);
        AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
        alpha.setDuration(500); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        // And then on your layout
        frame.startAnimation(alpha);


        mDialog = (ImageView) findViewById(R.id.image_dialog_image_view);

        if (imagePath != null && !imagePath.equals("")) {
            mDialog.setImageBitmap(toDoBitmap.getPhoto(imagePath));
        }
        mDialog.setClickable(true);


        //finish the activity (dismiss the image dialog) if the user clicks
        //anywhere on the image
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
