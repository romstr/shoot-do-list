package lv.tsi.romstr.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

/**
 * Created by Roman on 06.12.14..
 */
public class ColorDialog extends DialogPreference {
    public ColorDialog(final Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.xml.color_dialog);



//to turn of showing the old color
        //picker.setShowOldCenterColor(false);

//adding onChangeListeners to bars



    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.saturation_bar);

        picker.addSaturationBar(saturationBar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        final SharedPreferences.Editor prefsEditor = prefs.edit();

        int currentColor = prefs.getInt("background_color", 0);

        picker.setOldCenterColor(currentColor);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                System.out.println("Writing color pref " + picker.getColor());
                prefsEditor.putInt("background_color", picker.getColor());
            }
        });

        Button okButton = (Button) view.findViewById(R.id.pinok_but);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefsEditor.commit();
                getDialog().dismiss();
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.pincancel_but);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle("Choose the background for App:");
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
        super.onPrepareDialogBuilder(builder);
    }
}
