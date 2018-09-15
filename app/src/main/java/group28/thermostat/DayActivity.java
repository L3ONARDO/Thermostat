package group28.thermostat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.*;

import java.util.ArrayList;

public class DayActivity extends Activity {

    private Button addDay, addNight;
    private TextView[] dayView;
    private TextView[] nightView;
    private Button deleteall; //delete buttons
    private Button undo, copy;
    private OverViewBar dayBar;
    Button[] deleteButtons;                                   //delete button
    String clickedDay;
    WeekProgram wpg;
    History history;
    ArrayList<Integer> daySwitches, nightSwitches;
    int lastHour = 11, lastMinute = 0;

    View.OnLongClickListener longPressdg = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int index = -1;
            for (int j = 0; j < 5; j++) {
                if (dayView[j].getId() == v.getId()) {
                    index = j;
                    break;
                }
            }

            if (index != -1 && !dayView[index].getText().equals("")) {
                Intent i = new Intent(getApplicationContext(), addActivity.class);
                i.putExtra("day", clickedDay);
                i.putIntegerArrayListExtra("daySwitches", daySwitches);
                i.putIntegerArrayListExtra("nightSwitches", nightSwitches);
                i.putExtra("type", "editday");
                i.putExtra("pressedSwitch", "d" + (index + 1));
                startActivityForResult(i, 1);
            }
            return false;
        }
    };

    View.OnLongClickListener longPressng = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int index = -1;
            for (int j = 0; j < 5; j++) {
                if (nightView[j].getId() == v.getId()) {
                    index = j;
                    break;
                }
            }

            if (index != -1 && !nightView[index].getText().equals("")) {
                Intent i = new Intent(getApplicationContext(), addActivity.class);
                i.putExtra("day", clickedDay);
                i.putIntegerArrayListExtra("daySwitches", daySwitches);
                i.putIntegerArrayListExtra("nightSwitches", nightSwitches);
                i.putExtra("type", "editnight");
                i.putExtra("pressedSwitch", "n" + (index + 1));
                startActivityForResult(i, 1);
            }
            return false;
        }
    };

    View.OnTouchListener pressedListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(Color.WHITE);
                v.setAlpha(0.4f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setAlpha(1.0f);
            }

            return false;
        }
    };

    View.OnTouchListener overviewListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            OverViewBar ovb;
            int ovbWidth, ovbBarWidth;
            int start, point;
            double hourPixels, minutePixels;
            int pickedHour, pickedMinute;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                ovb = (OverViewBar) v;
                ovbWidth = ovb.getOvbWidth();
                ovbBarWidth = ovb.getOvbBarWidth();
                hourPixels = ovbBarWidth / 24.0;
                minutePixels = ovbBarWidth / 24.0 / 60.0;

                start = ovb.getLeft() + ((ovbBarWidth / ovbWidth) * ovbWidth);

                point = (int)event.getX() - start;

                pickedHour = (int)(point / hourPixels);
                pickedMinute = (int)((point % hourPixels) / minutePixels);
                pickedMinute = pickedMinute / 30 * 30;

                if (pickedHour > 23 || pickedHour < 0 || pickedMinute < 0 || pickedMinute > 59 || (ovb.getLeft() > event.getX())) {
                    return true;
                }

                if ((daySwitches.size() + nightSwitches.size()) < 10) {
                    Intent i = new Intent(getApplicationContext(), addActivity.class);
                    i.putExtra("day", clickedDay);
                    i.putIntegerArrayListExtra("daySwitches", daySwitches);
                    i.putIntegerArrayListExtra("nightSwitches", nightSwitches);
                    i.putExtra("lastHour", pickedHour);
                    i.putExtra("lastMinute", pickedMinute);
                    i.putExtra("type", "add");
                    startActivityForResult(i, 1);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Maximum numbers of switches reached", Toast.LENGTH_LONG).show();
                }

                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                return true;
            }

            return true;
        }
    };

    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addDay = (Button)findViewById(R.id.button26);
        addNight = (Button)findViewById(R.id.button27);
        undo = (Button)findViewById(R.id.button24);
        copy = (Button)findViewById(R.id.button25);
        dayBar = (OverViewBar)findViewById(R.id.dayBar);
        dayBar.setOnTouchListener(overviewListener);

        copy.setOnCreateContextMenuListener(this);

        Intent currentIntent = getIntent();
        clickedDay = currentIntent.getStringExtra("clickedDay");
        getActionBar().setTitle(clickedDay);

        history = ((ThermoApp) getApplication()).getHistoryInstance();

        dayView = new TextView[5];
        dayView[0] = (TextView)findViewById(R.id.textView15);
        dayView[1] = (TextView)findViewById(R.id.textView17);
        dayView[2] = (TextView)findViewById(R.id.textView19);
        dayView[3] = (TextView)findViewById(R.id.textView21);
        dayView[4] = (TextView)findViewById(R.id.textView23);

        nightView = new TextView[5];
        nightView[0] = (TextView)findViewById(R.id.textView16);
        nightView[1] = (TextView)findViewById(R.id.textView18);
        nightView[2] = (TextView)findViewById(R.id.textView20);
        nightView[3] = (TextView)findViewById(R.id.textView22);
        nightView[4] = (TextView)findViewById(R.id.textView24);

        daySwitches = new ArrayList<Integer>();
        nightSwitches = new ArrayList<Integer>();
        updateSwitches();

        deleteall = (Button)findViewById(R.id.button18);

        deleteall.setVisibility(Button.INVISIBLE);
        deleteall.setClickable(false);

        deleteButtons = new Button[10];

        deleteButtons[0] = (Button)findViewById(R.id.button19);
        deleteButtons[1] = (Button)findViewById(R.id.button20);
        deleteButtons[2] = (Button)findViewById(R.id.button21);
        deleteButtons[3] = (Button)findViewById(R.id.button22);
        deleteButtons[4] = (Button)findViewById(R.id.button23);
        deleteButtons[5] = (Button)findViewById(R.id.button6);
        deleteButtons[6] = (Button)findViewById(R.id.button7);
        deleteButtons[7] = (Button)findViewById(R.id.button8);
        deleteButtons[8] = (Button)findViewById(R.id.button10);
        deleteButtons[9] = (Button)findViewById(R.id.button9);

        for (Button btn : deleteButtons) {
            btn.setVisibility(Button.INVISIBLE);
            btn.setClickable(false);
        }

        for (TextView txt : dayView) {
            txt.setText("");
            txt.setOnLongClickListener(longPressdg);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            txt.setOnTouchListener(pressedListener);
        }

        for (TextView txt : nightView) {
            txt.setText("");
            txt.setOnLongClickListener(longPressng);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            txt.setOnTouchListener(pressedListener);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.setWeekProgram(wpg);
                    updateSwitches();
                } catch (Exception e) {
                    System.err.println("Error from putdata " + e);
                }
            }
        }).start();

        addDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (daySwitches.size() < 5) {
                    Intent i = new Intent(getApplicationContext(), addActivity.class);
                    i.putExtra("day", clickedDay);
                    i.putIntegerArrayListExtra("daySwitches", daySwitches);
                    i.putIntegerArrayListExtra("nightSwitches", nightSwitches);
                    i.putExtra("lastHour", lastHour);
                    i.putExtra("lastMinute", lastMinute);
                    i.putExtra("type", "day");
                    startActivityForResult(i, 1);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Maximum numbers of day switches reached", Toast.LENGTH_LONG).show();
                }
            }
        });

        addNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightSwitches.size() < 5) {
                    Intent i = new Intent(getApplicationContext(), addActivity.class);
                    i.putExtra("day", clickedDay);
                    i.putIntegerArrayListExtra("daySwitches", daySwitches);
                    i.putIntegerArrayListExtra("nightSwitches", nightSwitches);
                    i.putExtra("lastHour", lastHour);
                    i.putExtra("lastMinute", lastMinute);
                    i.putExtra("type", "night");
                    startActivityForResult(i, 1);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Maximum numbers of night switches reached", Toast.LENGTH_LONG).show();
                }
            }
        });


        deleteall=(Button)findViewById(R.id.button18);
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ArrayList<Integer>> switches;

                if (!history.isEmpty(clickedDay)) {
                    switches = history.getPrevious(clickedDay);

                    wpg.setAllSwitches(clickedDay, switches.get(0), switches.get(1), false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.setWeekProgram(wpg);
                                updateSwitches();
                            } catch (Exception e) {
                                System.err.println("Error from putdata " + e);
                            }
                        }
                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to undo", Toast.LENGTH_LONG).show();
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(copy);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String type;
        int index;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                type = data.getStringExtra("type");
                index = data.getIntExtra("index", 0);

                if (type.equals("day") || type.equals("night")) {
                    wpg.addSwitch(Integer.valueOf(data.getStringExtra("time")), clickedDay, type, true);
                } else if (type.equals("editday")) {
                    wpg.editSwitch(clickedDay, index, Integer.valueOf(data.getStringExtra("time")), "day", true);
                } else if (type.equals("editnight")){
                    wpg.editSwitch(clickedDay, index, Integer.valueOf(data.getStringExtra("time")), "night", true);
                } else if (type.equals("editday2")) {
                    wpg.removeSwitch(index, clickedDay, "day", false);
                    wpg.addSwitch(Integer.valueOf(data.getStringExtra("time")), clickedDay, "night", true);
                } else if (type.equals("editnight2")) {
                    wpg.removeSwitch(index, clickedDay, "night", false);
                    wpg.addSwitch(Integer.valueOf(data.getStringExtra("time")), clickedDay, "day", true);
                }

                lastHour = data.getIntExtra("lastHour", 11);
                lastMinute = data.getIntExtra("lastMinute", 0);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.setWeekProgram(wpg);
                            updateSwitches();
                            dayBar.postInvalidate();
                        } catch (Exception e) {
                            System.err.println("Error from putdata " + e);
                        }
                    }
                }).start();
            }
            else if (requestCode == RESULT_CANCELED)
            {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_about) {
            Intent i = new Intent(getApplicationContext(), aboutActivity.class);
            startActivity(i);
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Copy to...");
        getMenuInflater().inflate(R.menu.menu_daychoose, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String day = "";
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.day_Monday:
                day = "Monday";
                break;
            case R.id.day_Tuesday:
                day = "Tuesday";
                break;
            case R.id.day_Wednesday:
                day = "Wednesday";
                break;
            case R.id.day_Thursday:
                day = "Thursday";
                break;
            case R.id.day_Friday:
                day = "Friday";
                break;
            case R.id.day_Saturday:
                day = "Saturday";
                break;
            case R.id.day_Sunday:
                day = "Sunday";
                break;
        }

        if (!day.equals("")) {
            wpg.setAllSwitches(day, daySwitches, nightSwitches, true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HeatingSystem.setWeekProgram(wpg);
                        updateSwitches();
                    } catch (Exception e) {
                        System.err.println("Error from putdata " + e);
                    }
                }
            }).start();

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSwitches();
    }

    public void updateSwitches() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wpg = HeatingSystem.getWeekProgram();
                    wpg.setAppData((ThermoApp) getApplication());

                    daySwitches = wpg.getDay(clickedDay).get(0);
                    nightSwitches = wpg.getDay(clickedDay).get(1);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 5; i++) {
                                if (i < daySwitches.size()) {
                                    dayView[i].setText(wpg.int_time_to_string(daySwitches.get(i)));
                                } else {
                                    dayView[i].setText("");
                                }
                            }

                            for (int i = 0; i < 5; i++) {
                                if (i < nightSwitches.size()) {
                                    nightView[i].setText(wpg.int_time_to_string(nightSwitches.get(i)));
                                } else {
                                    nightView[i].setText("");
                                }
                            }

                            if (daySwitches.size() == 5) {
                                addDay.setAlpha(.5f);
                            } else {
                                addDay.setAlpha(1.0f);
                            }

                            if (nightSwitches.size() == 5) {
                                addNight.setAlpha(0.5f);
                            } else {
                                addNight.setAlpha(1.0f);
                            }

                            if (history.isEmpty(clickedDay)) {
                                undo.setAlpha(0.5f);
                            } else {
                                undo.setAlpha(1.0f);
                            }
                        }
                    });

                    updateDeleteButtons();

                    dayBar.set(daySwitches, nightSwitches);
                    dayBar.postInvalidate();
                } catch (Exception e) {
                    System.err.println("Error from getWeekProgram " + e);
                }
            }

        }).start();

    }

    public void updateDeleteButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if ((daySwitches.size() + nightSwitches.size()) != 0) {
                deleteall.setVisibility(View.VISIBLE);
                deleteall.setClickable(true);
                deleteall.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         wpg.removeAllSwitches(clickedDay, true);
                         dayBar.postInvalidate();

                         new Thread(new Runnable() {
                             @Override
                             public void run() {
                                 try {
                                     HeatingSystem.setWeekProgram(wpg);
                                     updateSwitches();
                                 } catch (Exception e) {
                                     System.err.println("Error from putdata " + e);
                                 }
                             }
                         }).start();
                     }
                 }
                );
            }

            for (int i = 0; i < 5; i++) {
                if (i < daySwitches.size()) {
                    deleteButtons[i].setVisibility(View.VISIBLE);
                    deleteButtons[i].setClickable(true);
                    deleteButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = -1;
                            for (int j = 0; j < 10; j++) {
                                if (deleteButtons[j].getId() == v.getId()) {
                                    index = j;
                                    break;
                                }
                            }

                            if (index != -1) {
                                wpg.removeSwitch(index, clickedDay, "Day", true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dayBar.postInvalidate();

                                        try {
                                            HeatingSystem.setWeekProgram(wpg);
                                            updateSwitches();
                                        } catch (Exception e) {
                                            System.err.println("Error from putdata " + e);
                                        }
                                    }
                                }).start();
                            }
                        }
                    });
                } else {
                    deleteButtons[i].setVisibility(View.INVISIBLE);
                    deleteButtons[i].setClickable(false);
                }
            }

            for (int i = 5; i < 10; i++) {
                if ((i - 5) < nightSwitches.size()) {
                    deleteButtons[i].setVisibility(View.VISIBLE);
                    deleteButtons[i].setClickable(true);
                    deleteButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = -1;
                            for (int j = 0; j < 5; j++) {
                                if (deleteButtons[5 + j].getId() == v.getId()) {
                                    index = j;
                                    break;
                                }
                            }

                            if (index != -1) {
                                wpg.removeSwitch(index, clickedDay, "Night", true);
                                dayBar.postInvalidate();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            HeatingSystem.setWeekProgram(wpg);
                                            updateSwitches();
                                        } catch (Exception e) {
                                            System.err.println("Error from putdata " + e);
                                        }
                                    }
                                }).start();
                            }
                        }
                    });
                } else {
                    deleteButtons[i].setVisibility(View.INVISIBLE);
                    deleteButtons[i].setClickable(false);
                }
            }
            }
        });
    }
}
