package es.upm.miw.bantumi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

public class ConfigurationActivity extends AppCompatActivity {
    SharedPreferences preferences;
    private boolean preferencesChanged = false;
    SharedPreferences.OnSharedPreferenceChangeListener spChanged = (sharedPreferences, key) -> {
        Log.i(MainActivity.LOG_TAG, "Preference " + key + " changed");
        preferencesChanged = true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesChanged = false;
        setContentView(R.layout.activity_configuration);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(spChanged);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.configuration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent replyIntent = new Intent();
            if (preferencesChanged) {
                setResult(RESULT_OK, replyIntent);
            }
            this.finish();
            return true;
        } else {
            this.showSnackBarConfigurationMessagesByResourceID(R.string.txtSinImplementar);
        }
        return false;
    }

    private void showSnackBarConfigurationMessagesByResourceID(int id) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(id),
                Snackbar.LENGTH_LONG
        ).show();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ConfigurationActivity configuration = (ConfigurationActivity) requireActivity();
            findPreference(getString(R.string.prInitialSeedNumberKey)).setOnPreferenceChangeListener((preference, newValue) -> {
                Log.i(MainActivity.LOG_TAG, "Changing initial seed numbers");
                try {
                    Integer.parseInt(newValue.toString());
                } catch (NumberFormatException nfex) {
                    Log.i(MainActivity.LOG_TAG, "Trying to set a non-number field to initial seed number preference");
                    configuration.showSnackBarConfigurationMessagesByResourceID(R.string.prSBSeedShouldBeNumberText);
                    return false;
                }
                return true;
            });
        }
    }
}