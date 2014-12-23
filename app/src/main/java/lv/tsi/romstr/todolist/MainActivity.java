package lv.tsi.romstr.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lv.tsi.romstr.todolist.Camera.ToDoBitmap;
import lv.tsi.romstr.todolist.Camera.ToDoCamera;
import lv.tsi.romstr.todolist.Camera.ToDoCameraSingleton;


public class MainActivity extends Activity {

    private final static ArrayList<ToDoItem> list = new ArrayList<ToDoItem>();

    public static final int PREFERENCES_UPDATE_REQUEST = 2;
    public static final int DETAILS_ACTIVITY_REQUEST = 0;

    private ToDoListAdapter adapter;

    private ToDoCamera toDoCamera;
    private ToDoBitmap toDoBitmap;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Active Android initialization
        ActiveAndroid.initialize(this);

        //Camera and Bitmap initialization
        toDoCamera = ToDoCameraSingleton.getSingleton(this, this);
        toDoBitmap = new ToDoBitmap(this);

        context = this;

        setContentView(R.layout.activity_main);


        //FacebookDialog fbDialog = new FacebookDialog.ShareDialogBuilder(this).setLink("http://Test").build();

        //fbDialog.present();

        updateSettings();

        readFromDb();

        final ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        ListView listView = (ListView) findViewById(R.id.listView);
        //Button deleteButton = (Button) findViewById(R.id.deleteButton);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        final EditText editText = (EditText) findViewById(R.id.textView);

        adapter = new ToDoListAdapter(this, android.R.layout.simple_list_item_1, list, this);

        final ImageButton textButton = (ImageButton) findViewById(R.id.add_text_button);
        final ImageButton photoButton = (ImageButton) findViewById(R.id.add_photo_button);
        final ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button);

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textButton.setVisibility(View.GONE);
                photoButton.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                editText.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                textButton.setVisibility(View.VISIBLE);
                photoButton.setVisibility(View.VISIBLE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoItem item = new ToDoItem(String.valueOf(editText.getText()));
                list.add(item);
                item.save();
                adapter.notifyDataSetChanged();
                saveToDb();

                editText.setText("");
                adapter.notifyDataSetChanged();

                editText.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                textButton.setVisibility(View.VISIBLE);
                photoButton.setVisibility(View.VISIBLE);
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoCamera.dispatchSavePictureIntent(-1);
            }
        });

        listView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAILS_ACTIVITY_REQUEST) {
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("pressed");
                System.out.println("_______Pressed: " + result);
                if (result.equals("delete")) {
                    ToDoItem item;
                    int position = data.getIntExtra("position", 0);
                    item = list.get(position);
                    list.remove(position);
                    item.delete();
                } else if (result.equals("cancel")) {
                    ToDoItem item;
                    int position = data.getIntExtra("position", 0);
                    item = list.get(position);
                    list.remove(position);
                    list.add(position, item);
                }
            }

        } else if (requestCode == PREFERENCES_UPDATE_REQUEST) {
            updateSettings();
        } else if (requestCode == ToDoCamera.REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");*/
                ActiveAndroid.initialize(context);

                ToDoItem item = new ToDoItem();
                item.setPhotoPath(toDoCamera.getCurrentPhotoPath());
                toDoBitmap.setThumbnail(item);
                list.add(item);
                item.save();
                adapter.notifyDataSetChanged();

            } else {
                AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(context);
                errorDialogBuilder.setTitle("Error");
                errorDialogBuilder.setMessage("Failed to get a photo!");
                errorDialogBuilder.setCancelable(true);
                AlertDialog errorDialog = errorDialogBuilder.create();
                errorDialog.show();
            }
        }
        readFromDb();
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AppPreferences.class);
            startActivityForResult(intent, PREFERENCES_UPDATE_REQUEST);

            return true;
        } else if (id == R.id.action_mail) {
            dispatchMailShareIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToDb();
    }

    private void saveToDb() {
        for (ToDoItem item : list) {
            System.out.println("------------------------Writing " + item.getText());
            item.save();
            System.out.println("------------------------Completed " + item.getText());

        }
    }

    private void readFromDb() {

        list.clear();
        list.addAll(getAll());

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPhotoPath() != null && list.get(i).getPhoto() == null) {
                toDoBitmap.setThumbnail(list.get(i));
            }
        }
    }

    public static List<ToDoItem> getAll() {
        return new Select()
                .from(ToDoItem.class)
                .execute();
    }

    private void updateSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //List item background
        /*int bgColor = Color.parseColor(prefs.getString("prefs_bgColor", "#ffffff"));
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setBackgroundColor(bgColor);*/

        //App background
        //int appBgColor = Color.parseColor(prefs.getString("prefs_appBgColor", "#ffffff"));
        int appBgColor = prefs.getInt("background_color", 0);
        System.out.println("Setting color " + appBgColor);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.appLayout);
        mainLayout.setBackgroundColor(appBgColor);
    }

    private void dispatchMailShareIntent(){
        ArrayList<Uri> imageUris = new ArrayList<Uri>();

        String shareString = getResources().getString(R.string.app_name) + ":\n\n";
        for (int i = 0; i < list.size(); i++) {
            shareString += (i + 1) + "). " + list.get(i).getStringToShare() + "\n";
            if (list.get(i).getPhotoPath() != null) {
                String path = list.get(i).getPhotoPath();
                Uri imageUri = Uri.fromFile(new File(path));
                imageUris.add(imageUri);
            }
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
    }

}
