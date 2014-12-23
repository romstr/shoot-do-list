package lv.tsi.romstr.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Random;

import lv.tsi.romstr.todolist.dialog.ImageDialog;

/**
 * Created by Roman on 30.10.14..
 */
public class ToDoListAdapter extends ArrayAdapter {

    private int ITEM_DETAILS_MESSAGE = 0;

    private List objects;
    private Context context;
    private int resource;
    private Activity activity;
    private final Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
    private Random random;
    private ViewHolder vh;

    public ToDoListAdapter(Context context, int resource, List objects, Activity activity) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.resource = resource;
        this.activity = activity;
        random = new Random();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //final ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.to_do_list_item, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final ToDoItem item = (ToDoItem) getItem(position);

        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setDate(new Date());

                vh.checkBox.setEnabled(false);
                item.complete();
                vh.dateView.setText(item.getDate());
                notifyDataSetChanged();
            }
        });

        vh.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objects.remove(position);
                item.delete();
                notifyDataSetChanged();
            }
        });

        vh.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent.putExtra("item", item.getId());
                intent.putExtra("position", position);
                activity.startActivityForResult(intent, MainActivity.DETAILS_ACTIVITY_REQUEST);
            }
        });

        vh.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(item);
            }
        });

        if (position % 2 == 0) {
            ((GradientDrawable) vh.layout.getBackground())
                    .setColor(context.getResources().getColor(R.color.itemBackGround));
            //vh.layout.setBackgroundColor(context.getResources().getColor(R.color.itemBackGround));
        } else {
            ((GradientDrawable) vh.layout.getBackground())
                    .setColor(Color.TRANSPARENT);
            //vh.layout.setBackgroundColor(Color.TRANSPARENT);
        }
        vh.checkBox.setEnabled(!item.getCompletedStatus());
        vh.checkBox.setChecked(item.getCompletedStatus());
        vh.textView.setText(item.getText());
        vh.dateView.setText(item.getDate());

        if (item.getPhotoPath() != null) {
            vh.image.setImageBitmap(item.getPhoto());
        } else {
            vh.image.setImageBitmap(null);
        }

        if (!item.getCompletedStatus()) {
            vh.dateView.setText(item.getDaysTillDeadline());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
        TextView dateView;
        CheckBox checkBox;
        ImageButton deleteButton;
        LinearLayout body;
        LinearLayout layout;
        ImageView image;

        public ViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.valueView);
            dateView = (TextView) convertView.findViewById(R.id.dateView);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            body = (LinearLayout) convertView.findViewById(R.id.itemBody);
            deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
            layout = (LinearLayout) convertView.findViewById(R.id.list_item_layout);
            image = (ImageView) convertView.findViewById(R.id.item_photo);
        }
    }

    private void showImageDialog(ToDoItem item) {

        if (item.getPhotoPath() != null) {
            Intent dialogIntent = new Intent(context, ImageDialog.class);
            dialogIntent.putExtra("path", item.getPhotoPath());
            context.startActivity(dialogIntent);
        }
    }

}