package group28.thermostat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.thermostatapp.util.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private TextView targetTempText, currentTempText;
    private ImageView stateIndicator;
    private TextView serverChange;
    private TextView today;
    private TextView switchState;
    String day;
    String time;
    private Button increment, decrement;
    private Button wk;
    private SeekBar seekBar;
    private Switch vac;                               // A switch to activate or close the v-mode
    boolean vacationMode;
    boolean barChanging = false;
    double targetTemperature, currentTemperature;
    double dayTemperature, nightTemperature;
    boolean incrementRun, decrementRun;
    boolean mainServer;
    public WeekProgram wpg;
    History history;

    final int OVERRIDE = 0;
    final int DAYSWITCH = 1;
    final int NIGHTSWITCH = 2;

    public int newHour, newMinute;
    public String pickedDay;

    Timer refreshTimer = new Timer();
    Timer buttonPressTimer;
    TimerTask refreshTask = new TimerTask() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        currentTemperature = Double.valueOf(HeatingSystem.get("currentTemperature"));

                        if (!barChanging) {
                            targetTemperature = Double.valueOf(HeatingSystem.get("targetTemperature"));
                            seekBar.setProgress((int) ((targetTemperature - 5) * 10));
                        }

                        dayTemperature = Double.valueOf(HeatingSystem.get("dayTemperature"));
                        nightTemperature = Double.valueOf(HeatingSystem.get("nightTemperature"));

                        vacationMode = HeatingSystem.getVacationMode();
                        day = HeatingSystem.get("day");
                        time = HeatingSystem.get("time");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentTempText.setText(new DecimalFormat("#0.0").format(currentTemperature));
                                targetTempText.setText(new DecimalFormat("#0.0").format(targetTemperature));

                                if (currentTemperature > targetTemperature) {
                                    stateIndicator.setImageResource(R.drawable.cold);
                                } else if (currentTemperature < targetTemperature) {
                                    stateIndicator.setImageResource(R.drawable.warm);
                                } else {
                                    stateIndicator.setImageResource(0);
                                }

                                if (day == null) {
                                    today.setText("No data received\nSwitch to backup");
                                } else {
                                    today.setText(time + " " + day);
                                }

                                vac.setChecked(vacationMode);

//                                switch (switchType(day, time)) {
//                                    case OVERRIDE:
//                                        switchState.setBackgroundColor(getResources().getColor(R.color.white));
//                                        switchState.setText("O");
//                                        break;
//
//                                    case DAYSWITCH:
//                                        switchState.setBackgroundColor(getResources().getColor(R.color.orange));
//                                        switchState.setText("D");
//                                        break;
//
//                                    case NIGHTSWITCH:
//                                        switchState.setBackgroundColor(getResources().getColor(R.color.blue));
//                                        switchState.setText("N");
//                                        break;
//
//                                    default:
//                                        break;
//                                }
                            }
                        });

                    } catch (Exception e) {
                        System.err.println("Error from getdata " + e);
                    }
                }
            }).start();
        }
    };

    TimerTask incrementTask = new TimerTask() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (incrementRun) {
                        if (targetTemperature < 30) {
                            targetTemperature += 0.1;
                            seekBar.setProgress((int) ((targetTemperature - 5) * 10));
                        }
                    } else if (decrementRun) {
                        if (targetTemperature > 5) {
                            targetTemperature -= 0.1;
                            seekBar.setProgress((int) ((targetTemperature - 5) * 10));
                        }
                    }
                }
            }).start();
        }
    };

    View.OnLongClickListener longPress = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (day != null) {
                openContextMenu(v);
            } else {
                Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        targetTempText = (TextView)findViewById(R.id.textView);
        currentTempText = (TextView)findViewById(R.id.textView25);
        stateIndicator = (ImageView)findViewById(R.id.imageView);
        switchState = (TextView)findViewById(R.id.textView28);
        today = (TextView)findViewById(R.id.textView36);
        buttonPressTimer = new Timer();
        increment=(Button)findViewById(R.id.button1);
        decrement=(Button)findViewById(R.id.button2);
        serverChange = (TextView)findViewById(R.id.serverChangeView);
        wk=(Button)findViewById(R.id.button);
        buttonPressTimer.schedule(incrementTask, 0, 10);
        vac = (Switch)findViewById(R.id.switch2);

        today.setOnCreateContextMenuListener(this);
        serverChange.setOnCreateContextMenuListener(this);
        // Initialize the textview with '5'

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        today.setOnLongClickListener(longPress);
        today.setOnTouchListener(pressedListener);

        HeatingSystem.BASE_ADDRESS = "http://pcwin889.win.tue.nl/2id40-ws/28";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";
        history = ((ThermoApp) getApplication()).getHistoryInstance();

        mainServer = !HeatingSystem.BASE_ADDRESS.equals("http://pcwin889.win.tue.nl/2id40-ws/28");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentTemperature = Double.valueOf(HeatingSystem.get("currentTemperature"));
                    targetTemperature = Double.valueOf(HeatingSystem.get("targetTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            targetTempText.setText(new DecimalFormat("#0.0").format(targetTemperature));
                            currentTempText.setText(new DecimalFormat("#0.0").format(currentTemperature));
                            seekBar.setProgress((int) ((targetTemperature - 5) * 10));
                        }
                    });

                    wpg = HeatingSystem.getWeekProgram();
                    wpg.setAppData(((ThermoApp) getApplication()).getHistoryInstance());

                    for (String day : WeekProgram.valid_days) {
                        history.add(day, wpg.getDay(day).get(0), wpg.getDay(day).get(1));
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        refreshTimer.schedule(refreshTask, 0, 1000);

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetTemperature < 30) {
                    targetTemperature += 0.1;
                    seekBar.setProgress((int) ((targetTemperature - 5) * 10));
                    barChanging = true;
                } else if (targetTemperature == 30) {
                    Toast.makeText(getApplicationContext(), "Maximum temperature reached", Toast.LENGTH_LONG).show();
                }
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (targetTemperature > 5) {
                    targetTemperature -= 0.1;
                    seekBar.setProgress((int) ((targetTemperature - 5) * 10));
                    barChanging = true;
                } else if (targetTemperature == 5) {
                    Toast.makeText(getApplicationContext(), "Minimum temperature reached", Toast.LENGTH_LONG).show();
                }
            }
        });

        increment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    putTargetTemperature(targetTemperature + 0.1);
                    incrementRun = false;
                    barChanging = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && targetTemperature == 30.0) {
                    incrementRun = false;
                }

                return false;
            }
        });

        decrement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    putTargetTemperature(targetTemperature - 0.1);
                    decrementRun = false;
                    barChanging = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && targetTemperature == 5.0) {
                    decrementRun = false;
                }

                return false;
            }
        });

        increment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                incrementRun = true;
                decrementRun = false;
                barChanging = true;
                return false;
            }
        });

        decrement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                decrementRun = true;
                incrementRun = false;
                barChanging = true;
                return false;
            }
        });

        wk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vacationMode) {
                    Toast.makeText(getApplicationContext(), "Vacation mode activated\nNo Week Program available", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getApplicationContext(), WeekActivity.class);
                    i.putExtra("vacationMode", vacationMode);
                    startActivity(i);
                }
            }
        });

        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        seekBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        double name = progressValue;
                        targetTemperature = (name / 10) + 5;
                        targetTempText.setText(new DecimalFormat("#0.0").format(targetTemperature));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                        barChanging = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        putTargetTemperature(targetTemperature);
                        barChanging = false;
                    }
                }
        );

        vac.setOnCheckedChangeListener(
                new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final boolean checked = isChecked;
                        vacationMode = isChecked;
                        wk.setAlpha((isChecked ? 0.5f : 1.0f));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    HeatingSystem.put("weekProgramState", (checked ? "off" : "on"));
                                } catch (Exception e) {
                                    System.err.println("Error from putdata " + e);
                                }
                            }
                        }).start();
                    }
                });

    }

    public void putTargetTemperature(final double temp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("targetTemperature", Double.toString(temp));
                } catch (Exception e) {
                    System.err.println("Error from putdata " + e);
                }
            }
        }).start();
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
        if (id == R.id.action_server) {
            openContextMenu(serverChange);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (day != null) {
            newHour = data.getIntExtra("newHour", Integer.valueOf(today.getText().toString().substring(0, 2)));
            newMinute = data.getIntExtra("newMinute", Integer.valueOf(today.getText().toString().substring(3,5)));
        } else {
            newHour = data.getIntExtra("newHour", 12);
            newMinute = data.getIntExtra("newMinute", 0);
        }

        pickedDay = data.getStringExtra("pickedDay");

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            HeatingSystem.put("time", new DecimalFormat("00").format(newHour) + ":" + new DecimalFormat("00").format(newMinute));
                            HeatingSystem.put("day", pickedDay);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (day == null) {
                                        today.setText("No data received\nSwitch to backup");
                                    } else {
                                        today.setText(time + " " + day);
                                    }
                                }
                            });


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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()) {
            case R.id.textView36:
                menu.setHeaderTitle("Change day to...");
                getMenuInflater().inflate(R.menu.menu_daychoose, menu);
                break;

            case R.id.serverChangeView:
                menu.setHeaderTitle("Change to server");
                getMenuInflater().inflate(R.menu.menu_serverchoose, menu);

                if (mainServer) {
                    menu.findItem(R.id.server_main).setChecked(true);
                } else {
                    menu.findItem(R.id.server_backup).setChecked(true);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String day = "";
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() != R.id.server_main && item.getItemId() != R.id.server_backup) {
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
                Intent i = new Intent(getApplicationContext(), addActivity.class);
                i.putExtra("day", day);
                i.putExtra("lastHour", Integer.valueOf(today.getText().toString().substring(0, 2)));
                i.putExtra("lastMinute", Integer.valueOf(today.getText().toString().substring(3,5)));
                i.putExtra("type", "edittime");
                startActivityForResult(i, 1);

                return true;
            } else {
                return false;
            }
        } else if (item.getItemId() == R.id.server_main || item.getItemId() == R.id.server_backup) {
            switch (item.getItemId()) {
                case R.id.server_main:
                    mainServer = true;
                    HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/28";
                    HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";
                    return true;

                case R.id.server_backup:
                    mainServer = false;
                    HeatingSystem.BASE_ADDRESS = "http://pcwin889.win.tue.nl/2id40-ws/28";
                    HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";
                    return true;

                default:
                    return false;
            }
        }

        return false;
    }

    public int switchType(String day, String time) {
        ArrayList<Integer> dayList = wpg.getDay(day).get(0);
        ArrayList<Integer> nightList = wpg.getDay(day).get(1);
        int dayValue = -1;
        int nightValue = -1;
        int timeValue = Integer.valueOf(time.substring(0,time.indexOf(":"))) * 100 + Integer.valueOf(time.substring(time.indexOf(":") + 1));

        for (int i = 0; i < dayList.size(); i++) {
            if (dayList.get(i) <= timeValue) {
                dayValue = dayList.get(i);
            } else {
                break;
            }
        }

        for (int i = 0; i < nightList.size(); i++) {
            if (nightList.get(i) <= timeValue) {
                nightValue = nightList.get(i);
            } else {
                break;
            }
        }

        if (Math.max(dayValue, nightValue) == nightValue && targetTemperature == nightTemperature) { // Night Switch is closest (including midnight)
            return NIGHTSWITCH;
        } else if (Math.max(dayValue, nightValue) == dayValue && dayValue != -1 && targetTemperature == dayTemperature) { // Day Switch is closest
            return DAYSWITCH;
        } else {
            return OVERRIDE;
        }
    }
}
