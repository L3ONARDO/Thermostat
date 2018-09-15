package group28.thermostat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import static group28.thermostat.R.id.button1;
import static group28.thermostat.R.id.textView;



public class aboutActivity extends Activity {

    private TextView aboutview;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setTitle("About us");
        getActionBar().setDisplayUseLogoEnabled(true);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        aboutview = (TextView)findViewById(R.id.textView33);
        aboutview.setText(Html.fromHtml("<h2>About</h2><br><p>Thermostat App by Group 28 \n</p>" +
                " \n " +
                "Version 1.0 \n</p>" +
                " \n" +
                "<h2>Credits:</h2><br><p>Berdine Kamps, 0885071 \n</p>" +
                " \n" +
                "Mark Leenen, 0901613 \n</p><br><br>" +
                " \n" +
                "Yuntao Li, 0910663 \n</p>" +
                " \n" +
                " \n</p>"));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
