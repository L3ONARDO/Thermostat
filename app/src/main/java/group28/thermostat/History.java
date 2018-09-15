package group28.thermostat;

import android.util.Log;

import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class History {
    Map<String, ArrayList<ArrayList<Integer>>> day_history = new HashMap<String, ArrayList<ArrayList<Integer>>>();
    Map<String, ArrayList<ArrayList<Integer>>> night_history = new HashMap<String, ArrayList<ArrayList<Integer>>>();
    ArrayList<ArrayList<Integer>> dayEntry;
    ArrayList<ArrayList<Integer>> nightEntry;

    History() {
        String day;

        for (int i = 0; i < WeekProgram.valid_days.length; i++) {
            day = WeekProgram.valid_days[i];

            dayEntry = new ArrayList<ArrayList<Integer>>();
            nightEntry = new ArrayList<ArrayList<Integer>>();

            day_history.put(day, dayEntry);
            night_history.put(day, nightEntry);
        }
    }

    public boolean isEmpty(String day) {
        return (day_history.get(day).size() == 0 || day_history.get(day).size() == 1);
    }

    public void add(String day, ArrayList<Integer> daySwitches, ArrayList<Integer> nightSwitches) {
        ArrayList<ArrayList<Integer>> historyDay = day_history.get(day); // Nested ArrayList
        ArrayList<ArrayList<Integer>> historyNight = night_history.get(day); // Nested ArrayList

        historyDay.add(daySwitches);
        day_history.put(day, historyDay);

        historyNight.add(nightSwitches);
        night_history.put(day, historyNight);
    }

    public ArrayList<ArrayList<Integer>> getPrevious(String day) { // NOT NESTED ARRAYLIST, JUST TWO ARRAYLISTS
        ArrayList<ArrayList<Integer>> switches = new ArrayList<ArrayList<Integer>>(); // NOT NESTED ARRAYLIST, JUST TWO ARRAYLISTS
        ArrayList<ArrayList<Integer>> historyDay = day_history.get(day); // Nested ArrayList
        ArrayList<ArrayList<Integer>> historyNight = night_history.get(day); // Nested ArrayList

        if (isEmpty(day)) {
            System.out.println("ERROR: Nothing to undo");
            throw new ArrayIndexOutOfBoundsException();
        } else {
            historyDay.remove(historyDay.size() - 1);
            switches.add(historyDay.get(historyDay.size() - 1));
            day_history.put(day, historyDay);

            historyNight.remove(historyNight.size() - 1);
            switches.add(historyNight.get(historyNight.size() - 1));
            night_history.put(day, historyNight);

            return switches;
        }
    }
}