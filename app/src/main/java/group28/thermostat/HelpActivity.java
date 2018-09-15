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


public class HelpActivity extends Activity {

    private TextView helpview;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getActionBar().setTitle("Help");
        getActionBar().setDisplayUseLogoEnabled(true);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        helpview = (TextView)findViewById(R.id.textView32);
        helpview.setText(Html.fromHtml("<h2>Main Page:</h2><br><p>The upper temperature is the current temperature in your home. The bottom temperature is the target temperature. The target temperature is the temperature the heating systems strives for. Those are not always the same because is take time to heat up or cool down a home.\n" +
                " \n" +
                "If you want to raise the target temperature you can click the green plus next to it (maximum is 30). If you want to lower the target temperature you can click the red minus next to it (minimum is 5). You can also use the slider bar under the target temperature. to the left is lowering the target temperature to the right is raising the target temperature. If you change the target temperature in this way, it is changed to the standard day or night temperature at the next switch in the week program.\n" +
                " \n" +
                "For changing the standard temperature for day and night see the chapter week program. If you want to disable the week program for longer period of time see vacation mode in the chapter week program. If you want to change the temperature for a longer period of time you need to go to the week program menu.\n" +
                " \n" +
                "If you click the button week program you go to the week program menu.\n</p>" +
                "<h2>Week Program:</h2><br><p>You are on the main page of the week program menu. If you want to go back to the main page of the app press the back button of your phone.\n" +
                " \n" +
                "The upper temperature by the sun is the standard day temperature you can change this temperature using the slider bar under the temperature. If you move it to the left the temperature lowers(minimum of 5). if you move it to the right is will raise (maximum of 30).\n" +
                " \n" +
                "The lower temperature by the moon and stars is the standard night temperature you can change this temperature using the slider bar under the temperature. If you move it to the left the temperature lowers(minimum of 5). if you move it to the right is will raise (maximum of 30).\n" +
                " \n" +
                "If you click on one of the days on the upper side of the screen you can change to program (switches) for that day.\n" +
                " \n" +
                "If you click on the switch next to vacation mode you will activate the vacation mode.\n</p>" +
                "<h2>Vacation Mode</h2><br><p>You are currently in vacation mode, in vacation mode the week program is disabled. So the target temperature stays the vacation temperature. It you click on the switch next to vacation mode you exit vacation mode.\n" +
                " \n" +
                "You can change the vacation temperature by using the slider bar under need the vacation temperature. If you move it to the left the temperature lowers(minimum of 5). if you move it to the right is will raise (maximum of 30).\n</p>" +
                "<h2>Day Page:</h2><br><p>This page shows the switched for one day. The switches in the column of the sun are switches to day temperature. The switches in the column of the moon and stars are switches to night temperature.\n" +
                " \n" +
                "You can delete a switch by pressing the red x next to the switch. or delete all at ones by pressing delete all at the bottom of the screen.\n" +
                " \n" +
                "If you want to add a switch you need to press the add a switch button.\n" +
                " \n" +
                "You cannot change a switch you have to delete the one you want to change and make a new one.\n</p>" +
                "<h2>Adding a switch:</h2><br><p>On this page you can make a switch.\n" +
                " \n" +
                "At the top of the screen you can set a time. This will be the time of the switch. At the bottom you can choose between day or night. If you choose day it will be a switch to day temperature. If you choose night it will be a switch to night temperature.\n" +
                " \n" +
                "If you are contend with the setting of the switch press confirm to add the switch. If you changed your mind and do not want to add a switch press cancel or the back button on your phone.\n" +
                "\n" +
                "\n</p>"));
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
