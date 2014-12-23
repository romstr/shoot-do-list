package lv.tsi.romstr.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import lv.tsi.romstr.todolist.Camera.ToDoBitmap;
import lv.tsi.romstr.todolist.Camera.ToDoCamera;
import lv.tsi.romstr.todolist.Camera.ToDoCameraSingleton;
import lv.tsi.romstr.todolist.dialog.ImageDialog;
import lv.tsi.romstr.todolist.sharing.TwitterActivity;

/**
 * Created by Roman on 05.11.14..
 */
public class ItemDetailsActivity extends Activity {

    public static final int DATE_DIALOG = 10;
    public static final int TWITTER_DIALOG = 11;

    private Intent intent = null;
    private ToDoItem item = null;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ToDoCamera toDoCamera;
    private ToDoBitmap toDoBitmap;

    //Layout Components
    //Item name
    private ImageButton nameImageButton;
    private TextView nameTextView;
    private EditText nameEditText;

    //Item completion status
    private ImageButton completionImageButton;
    private TextView completionTextView;

    //Item deadline
    private ImageButton deadlineImageButton;
    private TextView deadlineTextView;

    //Item description
    private ImageButton descriptionImageButton;
    private TextView descriptionTextView;
    private EditText descriptionEditText;

    //Item photo
    private ImageButton photoImageButton;
    private ImageView photoImageView;

    //Action buttons
    private ImageButton actionBackButton;
    private ImageButton actionDeleteButton;
    private ImageButton actionShareButton;



    private void initializeActivity() {

        toDoCamera = ToDoCameraSingleton.getSingleton(this, this);
        toDoBitmap = new ToDoBitmap(this);

        intent = getIntent();
        //if (ToDoItem.load(ToDoItem.class, (int) intent.getLongExtra("item", 1)) != null)
        //{
            item = ToDoItem.load(ToDoItem.class, (int) intent.getLongExtra("item", 1));
        //} else {
        //    item = ToDoItem.load(ToDoItem.class, (int) intent.getLongExtra("item", ToDoCamera.REQUEST_TAKE_PHOTO));
        //}

        setContentView(R.layout.item_details);

        //Background from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ScrollView scrollView = (ScrollView) findViewById(R.id.item_details_scroll);
        int bgColor = prefs.getInt("background_color", 0);
        scrollView.setBackgroundColor(bgColor);

        //Layout components
        //Item name
        nameImageButton = (ImageButton) findViewById(R.id.item_details_name_image_button);
        nameTextView = (TextView) findViewById(R.id.item_details_name_text_view);
        nameEditText = (EditText) findViewById(R.id.item_details_name_edit_text);

        nameTextView.setText(item.getText());
        //Swap between TextView and EditText
        nameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getVisibility() == View.GONE) {
                    nameTextView.setVisibility(View.GONE);
                    nameEditText.setVisibility(View.VISIBLE);
                    nameEditText.setText(item.getText());
                    nameImageButton.setImageResource(R.drawable.save);
                    nameEditText.requestFocus();
                } else {
                    nameTextView.setVisibility(View.VISIBLE);
                    item.setText(nameEditText.getText().toString());
                    nameEditText.setVisibility(View.GONE);
                    nameTextView.setText(item.getText());
                    nameImageButton.setImageResource(R.drawable.edit);
                    nameTextView.requestFocus();
                }
                item.save();
            }
        });

        //Item completion status
        completionImageButton = (ImageButton) findViewById(R.id.item_details_completion_image_button);
        completionTextView = (TextView) findViewById(R.id.item_details_completion_text_view);

        if (item.getCompletedStatus()) {
            completionTextView.setText(item.getDate());
            completionImageButton.setImageResource(R.drawable.uncheck);
        }
        completionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getCompletedStatus()) {
                    item.unComplete();
                    completionTextView.setText(R.string.completion_none);
                    completionImageButton.setImageResource(R.drawable.check);
                } else {
                    item.setDate(new Date());
                    item.complete();
                    completionTextView.setText(item.getDate());
                    completionImageButton.setImageResource(R.drawable.uncheck);
                }
                item.save();
            }
        });

        //Item deadline
        deadlineImageButton = (ImageButton) findViewById(R.id.item_details_deadline_image_button);
        deadlineTextView = (TextView) findViewById(R.id.item_details_deadline_text_view);

        deadlineTextView.setText(item.deadlineToString() + " : " + item.getDaysTillDeadline());

        final Calendar calendar = Calendar.getInstance();
        final int thisYear = calendar.get(Calendar.YEAR);
        final int thisMonth = calendar.get(Calendar.MONTH);
        final int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        deadlineImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), dateSetListener, thisYear, thisMonth, thisDay);
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                item.setDeadline(calendar.getTime());
                item.save();
                deadlineTextView.setText(item.deadlineToString() + " : " + item.getDaysTillDeadline());
            }
        };

        //Item description
        descriptionImageButton = (ImageButton) findViewById(R.id.item_details_description_image_button);
        descriptionTextView = (TextView) findViewById(R.id.item_details_description_text_view);
        descriptionEditText = (EditText) findViewById(R.id.item_details_description_edit_text);

        descriptionTextView.setText(item.getDescription());
        descriptionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (descriptionEditText.getVisibility() == View.GONE) {
                    descriptionTextView.setVisibility(View.GONE);
                    descriptionEditText.setVisibility(View.VISIBLE);
                    descriptionEditText.setText(item.getDescription());
                    descriptionImageButton.setImageResource(R.drawable.save);
                    descriptionEditText.requestFocus();
                } else {
                    descriptionTextView.setVisibility(View.VISIBLE);
                    item.setDescription(descriptionEditText.getText().toString());
                    descriptionEditText.setVisibility(View.GONE);
                    descriptionTextView.setText(item.getDescription());
                    descriptionImageButton.setImageResource(R.drawable.edit);
                    descriptionTextView.requestFocus();
                }
                item.save();
            }
        });

        //Item photo
        photoImageButton = (ImageButton) findViewById(R.id.item_details_photo_image_button);
        photoImageView = (ImageView) findViewById(R.id.item_details_photo_image_view);

        if (item.getPhotoPath() != null) {
            toDoBitmap.setPhoto(item, photoImageView);
        }
        photoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoCamera.dispatchSavePictureIntent(item.getId());
            }
        });
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchImageDialog();
            }
        });

        //Action buttons
        actionBackButton = (ImageButton) findViewById(R.id.item_details_action_back);
        actionDeleteButton = (ImageButton) findViewById(R.id.item_details_action_delete);
        actionShareButton = (ImageButton) findViewById(R.id.item_details_action_share);

        actionBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchActionBackIntent();
            }
        });
        actionDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchActionDeleteIntent();
            }
        });
        actionShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchActionShareIntent();
            }
        });

    }

    //Action button onClick handling
    private void dispatchActionBackIntent() {
        item.save();
        Intent response = new Intent();
        response.putExtra("position", intent.getIntExtra("position", 0));
        response.putExtra("pressed", "cancel");
        setResult(RESULT_OK, response);
        //setResult(RESULT_CANCELED, null);
        finish();
    }

    private void dispatchActionDeleteIntent() {
        Intent response = new Intent();
        //response.putExtra("item", item);
        response.putExtra("position", intent.getIntExtra("position", 0));
        response.putExtra("pressed", "delete");
        setResult(RESULT_OK, response);
        finish();
    }

    private void dispatchActionShareIntent() {
        setResult(RESULT_CANCELED, null);
        showShareDialog();
    }

    private void dispatchSimpleShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, item.getStringToShare());
        startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
    }

    private void dispatchImageDialog() {
        if (item.getPhotoPath() != null) {
            Intent dialogIntent = new Intent(this, ImageDialog.class);
            dialogIntent.putExtra("path", item.getPhotoPath());
            startActivity(dialogIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TWITTER_DIALOG) {
            String result;
            if(resultCode == RESULT_OK){
                result = "Your status has been updated! :)";
            } else {
                result = "Error updating status! :(";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Twitter")
                    .setMessage(result)
                    .setIcon(R.drawable.twitter)
                    .setCancelable(false)
                    .setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (requestCode == ToDoCamera.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            item.setPhotoPath(toDoCamera.getCurrentPhotoPath());
            System.out.println("---------------------" + item.getPhotoPath());
            toDoBitmap.setPhoto(item, photoImageView);
            toDoBitmap.setThumbnail(item);
            item.save();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //item.save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializeActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_back) {
            dispatchActionBackIntent();
            return true;
        } else if (id == R.id.action_delete) { // && this.item != null && intent != null) {
            dispatchActionDeleteIntent();
            return true;
        } else if (id == R.id.action_share) {
            showShareDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showShareDialog() {
        final AlertDialog.Builder shareDialog = new AlertDialog.Builder(this);

        shareDialog.setIcon(R.drawable.share);
        shareDialog.setTitle(R.string.share_dialog_name);

        View shareDialogLayout = getLayoutInflater().inflate(R.layout.share_dialog, null);
        shareDialog.setView(shareDialogLayout);

        ImageButton twitterButton = (ImageButton) shareDialogLayout.findViewById(R.id.twitter_share_button);

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent twitterIntent = new Intent(view.getContext(), TwitterActivity.class);
                twitterIntent.putExtra("status", item.getTwitterShare());
                twitterIntent.putExtra("path", item.getPhotoPath());
                startActivityForResult(twitterIntent, TWITTER_DIALOG);
            }
        });

        ImageButton simpleShareButton = (ImageButton) shareDialogLayout.findViewById(R.id.simple_share_button);
        simpleShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchSimpleShareIntent();
            }
        });

        shareDialog.create();
        shareDialog.show();
    }





}
