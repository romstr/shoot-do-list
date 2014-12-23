package lv.tsi.romstr.todolist;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Roman on 01.12.14..
 */
public class AppPreferences extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        addPreferencesFromResource(R.xml.preferences);
        return false;
    }
}
