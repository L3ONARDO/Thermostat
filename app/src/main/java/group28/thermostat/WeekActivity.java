package group28.thermostat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WeekActivity extends Activity {

    private Button mon, tue, wed, thu, fri, sat, sun; // Buttons for weekdays
    private Button[] topButtons;
    private TextView[] sideLabels;
    private SeekBar seekBard;                         // For customizing the day temp.
    private SeekBar seekBarn;                         // For customizing the night temp.
                                                      // under the vacation mode.
    private TextView textViewd;                       // Contains day temp.
    private TextView textViewn;                       // Contains night temp.
    public WeekProgram wpg;
    double dayTemperature = 5.0, nightTemperature = 5.0;
    double currentDaytemp, currentNighttemp;
    private ImageButton dayincrement, daydecrement, nightincrement, nightdecrement;
    boolean barChanging = false;
    boolean dayincrementRun, daydecrementRun, nightincrementRun, nightdecrementRun;
    private OverViewBar[] bars;
    private OverViewBar barDummy;
    boolean refresh = true;
    Timer refreshTimer = new Timer();
    Timer buttonPressTimer;
    TimerTask refreshTask = new TimerTask() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        currentDaytemp = Double.valueOf(HeatingSystem.get("dayTemperature"));
                        currentNighttemp = Double.valueOf(HeatingSystem.get("nightTemperature"));// Slow down the fetching

                        if (!barChanging) {
                            dayTemperature = Double.valueOf(HeatingSystem.get("dayTemperature"));
                            seekBard.setProgress((int) ((dayTemperature - 5) * 10));
                            nightTemperature = Double.valueOf(HeatingSystem.get("nightTemperature"));
                            seekBarn.setProgress((int) ((nightTemperature - 5) * 10));
                        }

                    } catch (Exception e) {
                        System.err.println("Error from getdata " + e);
                    }
                }
            }).start();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewd.setText(new DecimalFormat("#0.0").format(dayTemperature));
                    textViewn.setText(new DecimalFormat("#0.0").format(nightTemperature));
                }
            });

        }
    };

    TimerTask incrementTask = new TimerTask() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (dayincrementRun) {
                        if (dayTemperature < 30) {
                            dayTemperature += 0.1;
                            seekBard.setProgress((int) ((dayTemperature - 5) * 10));
                        }
                    }
                        else if (nightincrementRun) {
                            if (nightTemperature < 30) {
                                nightTemperature += 0.1;
                                seekBarn.setProgress((int) ((nightTemperature - 5) * 10));
                            }
                        }
                    else if (daydecrementRun) {
                        if (dayTemperature > 5) {
                            dayTemperature -= 0.1;
                            seekBard.setProgress((int) ((dayTemperature - 5) * 10));
                        }
                    } else if (nightdecrementRun) {
                        if (nightTemperature > 5) {
                            nightTemperature -= 0.1;
                            seekBarn.setProgress((int) ((nightTemperature - 5) * 10));
                        }
                    }
                }
            }).start();
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
            ArrayList<Integer> daySwitches, nightSwitches;
            String chosenDay;

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

                switch (v.getId()) {
                    case R.id.view:
                        chosenDay = "Monday";
                        break;

                    case R.id.view2:
                        chosenDay = "Tuesday";
                        break;

                    case R.id.view3:
                        chosenDay = "Wednesday";
                        break;

                    case R.id.view4:
                        chosenDay = "Thursday";
                        break;

                    case R.id.view5:
                        chosenDay = "Friday";
                        break;

                    case R.id.view6:
                        chosenDay = "Saturday";
                        break;

                    case R.id.view7:
                        chosenDay = "Sunday";
                        break;

                    default:
                        chosenDay = "";
                        break;
                }

                if (chosenDay.equals("")) {
                    return false;
                }

                daySwitches = wpg.getDay(chosenDay).get(0);
                nightSwitches = wpg.getDay(chosenDay).get(1);

                if ((daySwitches.size() + nightSwitches.size()) < 10) {
                    Intent i = new Intent(getApplicationContext(), addActivity.class);
                    i.putExtra("day", chosenDay);
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

    View.OnClickListener dayListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = -1;

            for (int i = 0; i < 7; i++) {
                if (topButtons[i].getId() == v.getId() || sideLabels[i].getId() == v.getId()) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                Intent i = new Intent(getApplicationContext(), DayActivity.class);
                i.putExtra("clickedDay", WeekProgram.valid_days[index]);
                startActivity(i);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        getActionBar().setTitle("Week Program");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        seekBard = (SeekBar) findViewById(R.id.seekBar);
        seekBarn = (SeekBar) findViewById(R.id.seekBar2);
        textViewd = (TextView) findViewById(R.id.textView5);
        textViewn = (TextView) findViewById(R.id.textView6);
        buttonPressTimer = new Timer();
        buttonPressTimer.schedule(incrementTask, 0, 10);
        // Initialize the textview with '5'
        textViewd.setText(new DecimalFormat("#0.0").format(dayTemperature));
        textViewn.setText(new DecimalFormat("#0.0").format(dayTemperature));
        topButtons = new Button[7];
        topButtons[0] = (Button)findViewById(R.id.button12);
        topButtons[1] = (Button)findViewById(R.id.button13);
        topButtons[2] = (Button)findViewById(R.id.button14);
        topButtons[3] = (Button)findViewById(R.id.button15);
        topButtons[4] = (Button)findViewById(R.id.button16);
        topButtons[5] = (Button)findViewById(R.id.button17);
        topButtons[6] = (Button)findViewById(R.id.button11);
        sideLabels = new TextView[7];
        sideLabels[0] = (TextView)findViewById(R.id.textView2);
        sideLabels[1] = (TextView)findViewById(R.id.textView38);
        sideLabels[2] = (TextView)findViewById(R.id.textView39);
        sideLabels[3] = (TextView)findViewById(R.id.textView41);
        sideLabels[4] = (TextView)findViewById(R.id.textView42);
        sideLabels[5] = (TextView)findViewById(R.id.textView43);
        sideLabels[6] = (TextView)findViewById(R.id.textView44);
        dayincrement = (ImageButton)findViewById(R.id.imageButton);
        daydecrement = (ImageButton)findViewById(R.id.imageButton2);
        nightincrement = (ImageButton)findViewById(R.id.imageButton3);
        nightdecrement = (ImageButton)findViewById(R.id.imageButton4);
        bars = new OverViewBar[7];
        bars[0] = (OverViewBar)findViewById(R.id.view);
        bars[1] = (OverViewBar)findViewById(R.id.view2);
        bars[2] = (OverViewBar)findViewById(R.id.view3);
        bars[3] = (OverViewBar)findViewById(R.id.view4);
        bars[4] = (OverViewBar)findViewById(R.id.view5);
        bars[5] = (OverViewBar)findViewById(R.id.view6);
        bars[6] = (OverViewBar)findViewById(R.id.view7);

        for (OverViewBar ovb : bars) {
            ovb.setOnTouchListener(overviewListener);
        }

        barDummy = (OverViewBar)findViewById(R.id.view8);
        barDummy.set(new ArrayList<Integer>(), new ArrayList<Integer>());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemperature = Double.valueOf(HeatingSystem.get("dayTemperature"));
                    nightTemperature = Double.valueOf(HeatingSystem.get("nightTemperature"));
                    wpg = HeatingSystem.getWeekProgram();
                    wpg.setAppData((ThermoApp) getApplication());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewd.setText(new DecimalFormat("#0.0").format(dayTemperature));
                            textViewn.setText(new DecimalFormat("#0.0").format(nightTemperature));
                            seekBard.setProgress((int) ((dayTemperature - 5) * 10));
                            seekBarn.setProgress((int) ((nightTemperature - 5) * 10));

                            bars[0].set(wpg.getDay("Monday").get(0), wpg.getDay("Monday").get(1));
                            bars[1].set(wpg.getDay("Tuesday").get(0), wpg.getDay("Tuesday").get(1));
                            bars[2].set(wpg.getDay("Wednesday").get(0), wpg.getDay("Wednesday").get(1));
                            bars[3].set(wpg.getDay("Thursday").get(0), wpg.getDay("Thursday").get(1));
                            bars[4].set(wpg.getDay("Friday").get(0), wpg.getDay("Friday").get(1));
                            bars[5].set(wpg.getDay("Saturday").get(0), wpg.getDay("Saturday").get(1));
                            bars[6].set(wpg.getDay("Sunday").get(0), wpg.getDay("Sunday").get(1));
                        }
                    });

                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        refreshTimer.schedule(refreshTask, 0, 1000);

        seekBard.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        seekBard.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBard.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        double name = progresValue;
                        dayTemperature = (name / 10) + 5;
                        textViewd.setText(new DecimalFormat("#0.0").format(dayTemperature));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        putDayTemperature(dayTemperature);
                    }
                });

        seekBarn.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        seekBarn.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBarn.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        double name = progresValue;
                        nightTemperature = (name / 10) + 5;
                        textViewn.setText(new DecimalFormat("#0.0").format(nightTemperature));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        putNightTemperature(nightTemperature);
                    }
                });

        for (int i = 0; i < 7; i++) {
            topButtons[i].setOnClickListener(dayListener);
            sideLabels[i].setOnClickListener(dayListener);
        }

        dayincrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayTemperature < 30) {
                    dayTemperature += 0.1;
                    seekBard.setProgress((int) ((dayTemperature - 5) * 10));
                    barChanging = true;
                } else if (dayTemperature == 30) {
                    Toast.makeText(getApplicationContext(), "Maximum temperature reached", Toast.LENGTH_LONG).show();
                }
            }
        });

        dayincrement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    putDayTemperature(dayTemperature+0.1);
                    dayincrementRun = false;
                    barChanging = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && dayTemperature == 30.0) {
                    dayincrementRun = false;
                }

                return false;
            }
        });

        dayincrement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dayincrementRun = true;
                daydecrementRun = false;
                barChanging = true;
                return false;
            }
        });

        daydecrement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dayTemperature > 5) {
                    dayTemperature -= 0.1;
                    seekBard.setProgress((int) ((dayTemperature - 5) * 10));
                    barChanging = true;
                } else if (dayTemperature == 5) {
                    Toast.makeText(getApplicationContext(), "Minimum temperature reached", Toast.LENGTH_LONG).show();
                }

                putDayTemperature(dayTemperature);
            }
        });

        daydecrement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    putDayTemperature(dayTemperature-0.1);
                    daydecrementRun = false;
                    barChanging = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && dayTemperature == 5.0) {
                    daydecrementRun = false;
                }

                return false;
            }
        });

        daydecrement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                daydecrementRun = true;
                dayincrementRun = false;
                barChanging = true;
                return false;
            }
        });


        nightincrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightTemperature < 30) {
                    nightTemperature += 0.1;
                    seekBarn.setProgress((int) ((nightTemperature - 5) * 10));
                    barChanging = true;
                } else if (nightTemperature == 30) {
                    Toast.makeText(getApplicationContext(), "Maximum temperature reached", Toast.LENGTH_LONG).show();
                }
            }
        });

        nightincrement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    putNightTemperature(nightTemperature+0.1);
                    nightincrementRun = false;
                    barChanging = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && nightTemperature == 30.0) {
                    nightincrementRun = false;
                }

                return false;
            }
        });

        nightincrement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nightincrementRun = true;
                nightdecrementRun = false;
                barChanging = true;
                return false;
            }
        });

        nightdecrement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (nightTemperature > 5) {
                    nightTemperature -= 0.1;
                    seekBarn.setProgress((int) ((nightTemperature - 5) * 10));
                    barChanging = true;
                } else if (nightTemperature == 5) {
                    Toast.makeText(getApplicationContext(), "Minimum temperature reached", Toast.LENGTH_LONG).show();
                }

                putNightTemperature(nightTemperature);
            }
        });

        nightdecrement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    putNightTemperature(nightTemperature-0.1);
                    nightdecrementRun = false;
                    barChanging = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && nightTemperature == 5.0) {
                    nightdecrementRun = false;
                }

                return false;
            }
        });

        nightdecrement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nightdecrementRun = true;
                nightincrementRun = false;
                barChanging = true;
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (refresh) {
                        wpg = HeatingSystem.getWeekProgram();
                        wpg.setAppData((ThermoApp) getApplication());
                    } else {
                        refresh = true;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bars[0].set(wpg.getDay("Monday").get(0), wpg.getDay("Monday").get(1));
                            bars[1].set(wpg.getDay("Tuesday").get(0), wpg.getDay("Tuesday").get(1));
                            bars[2].set(wpg.getDay("Wednesday").get(0), wpg.getDay("Wednesday").get(1));
                            bars[3].set(wpg.getDay("Thursday").get(0), wpg.getDay("Thursday").get(1));
                            bars[4].set(wpg.getDay("Friday").get(0), wpg.getDay("Friday").get(1));
                            bars[5].set(wpg.getDay("Saturday").get(0), wpg.getDay("Saturday").get(1));
                            bars[6].set(wpg.getDay("Sunday").get(0), wpg.getDay("Sunday").get(1));
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_week, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String type;
        String currentDay;
        int index = -1;


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                refresh = false;
                type = data.getStringExtra("type");
                currentDay = data.getStringExtra("day");

                if (type.equals("day") || type.equals("night")) {
                    wpg.addSwitch(Integer.valueOf(data.getStringExtra("time")), currentDay, type, true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.setWeekProgram(wpg);
                            } catch (Exception e) {
                                System.err.println("Error from putdata " + e);
                            }
                        }
                    }).start();

                    for (int i = 0; i < WeekProgram.valid_days.length; i++) {
                        if (WeekProgram.valid_days[i].equals(currentDay)) {
                            index = i;
                            break;
                        }
                    }
                }

                if (index != -1) {
                    bars[index].set(wpg.getDay(WeekProgram.valid_days[index]).get(0), wpg.getDay(WeekProgram.valid_days[index]).get(1));
                    bars[index].postInvalidate();
                }
            }
            else if (requestCode == RESULT_CANCELED)
            {
            }
        }
    }

    public void putDayTemperature(final double temp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
							/* test*/
                    HeatingSystem.put("dayTemperature", Double.toString(temp));
                } catch (Exception e) {
                    System.err.println("Error from putdata " + e);
                }
            }
        }).start();
    }

    public void putNightTemperature(final double temp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
							/* test*/
                    HeatingSystem.put("nightTemperature", Double.toString(temp));
                } catch (Exception e) {
                    System.err.println("Error from putdata " + e);
                }
            }
        }).start();
    }
}
