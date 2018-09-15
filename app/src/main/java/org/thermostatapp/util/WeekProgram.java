package org.thermostatapp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import group28.thermostat.History;
import group28.thermostat.ThermoApp;

/**
 * Created by Mark Leenen on 20-6-2015.
 */
public class WeekProgram {
    /* Switches are stored in a hashmap, mapping every day to its
    corresponding set of switches */
    Map<String, ArrayList<Integer>> data_day = new HashMap<String, ArrayList<Integer>>();
    Map<String, ArrayList<Integer>> data_night = new HashMap<String, ArrayList<Integer>>();
    ArrayList<Integer> daySwitches;
    ArrayList<Integer> nightSwitches;

    String currentDay, time;
    double currentTemperature, targetTemperature, dayTemperature, nightTemperature;
    boolean weekProgramState;

    ThermoApp thermoApp;
    History history;

    public static String[] valid_days = { "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday" };

    public WeekProgram() {
        String day;
        // Retrieve switches
        for (int i = 0; i < valid_days.length; i++) {
            day = valid_days[i];

            daySwitches = new ArrayList<Integer>();
            nightSwitches = new ArrayList<Integer>();

            data_day.put(day, daySwitches);
            data_night.put(day, nightSwitches);
        }
    }

    public WeekProgram(String current_day, String time, String current_temperature, String target_temperature,
                       String day_temperature, String night_temperature, String week_program_state) {
        this.currentDay = current_day;
        this.time = time;
        this.currentTemperature = Double.valueOf(current_temperature);
        this.targetTemperature = Double.valueOf(target_temperature);
        this.dayTemperature = Double.valueOf(day_temperature);
        this.nightTemperature = Double.valueOf(night_temperature);
        this.weekProgramState = (week_program_state.equals("on") ? true : false);

        System.out.println("BITCH" + getCurrentDay());
        System.out.println("BITCH" + getTime());
        System.out.println("BITCH" + getCurrentTemperature());
        System.out.println("BITCH" + getTargetTemperature());
        System.out.println("BITCH" + getDayTemperature());
        System.out.println("BITCH" + getNightTemperature());
        System.out.println("BITCH" + getWeekProgramState());

        String day;
        // Retrieve switches
        for (int i = 0; i < valid_days.length; i++) {
            day = valid_days[i];

            daySwitches = new ArrayList<Integer>();
            nightSwitches = new ArrayList<Integer>();

            data_day.put(day, daySwitches);
            data_night.put(day, nightSwitches);
        }
    }

    public void setAppData(ThermoApp tma) {
        thermoApp = tma;
        history = tma.getHistoryInstance();
    }

    public void setAppData(History history) {
        this.history = history;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public String getTime() {
        return time;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public double getTargetTemperature() {
        return targetTemperature;
    }

    public double getDayTemperature() {
        return dayTemperature;
    }

    public double getNightTemperature() {
        return nightTemperature;
    }

    public boolean getWeekProgramState() {
        return weekProgramState;
    }

    public boolean setAllSwitches(String day, ArrayList<Integer> dayList, ArrayList<Integer>  nightList, boolean storeHistory) {
        if (validDay(day)) {
            setData(day, dayList, nightList, storeHistory);
            return true;
        }

        return false;
    }

    public void setData(String day, ArrayList<Integer> dayData, ArrayList<Integer> nightData, boolean storeHistory) {
        daySwitches = dayData;
        nightSwitches = nightData;

        data_day.put(day, daySwitches);
        data_night.put(day, nightSwitches);

        if (storeHistory) {
            addToHistory(day);
        }
    }

    boolean validDay(String day) {
        for (int i = 0; i < valid_days.length; i++) {
            if (valid_days[i].equalsIgnoreCase(day)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<ArrayList<Integer>> getDay(String day) {
        ArrayList<ArrayList<Integer>> switches = new ArrayList<ArrayList<Integer>>();
        switches.add(data_day.get(day));
        switches.add(data_night.get(day));

        return switches;
    }

    public boolean addSwitch(int time, String day, String type, boolean storeHistory) {
        boolean matchTime = false;

        if (time >= 0 && time < 2400) {
            if (validDay(day)) {
                if (type.equalsIgnoreCase("day")) {
                    for (int i : data_day.get(day)) {
                        if (time == i) {
                            matchTime = true;
                            break;
                        }
                    }

                    if (!matchTime) {
                        if (data_day.get(day).size() < 5) {
                            data_day.get(day).add(time);
                            Collections.sort(data_day.get(day));

                            if (storeHistory) {
                                addToHistory(day);
                            }

                            return true;
                        }
                    }
                } else if (type.equalsIgnoreCase("night")) {
                    for (int i : data_night.get(day)) {
                        if (time == i) {
                            matchTime = true;
                            break;
                        }
                    }

                    if (!matchTime) {
                        if (data_night.get(day).size() < 5) {
                            data_night.get(day).add(time);
                            Collections.sort(data_night.get(day));

                            if (storeHistory) {
                                addToHistory(day);
                            }

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean editSwitch(String day, int index, int time, String type, boolean storeHistory) {
        daySwitches = data_day.get(day);
        nightSwitches = data_night.get(day);

        if (type.equalsIgnoreCase("day")) {
            daySwitches.set(index, time);
        } else if (type.equalsIgnoreCase("night")){
            nightSwitches.set(index, time);
        }

        setAllSwitches(day, daySwitches, nightSwitches, storeHistory);

        return true;
    }

    public boolean removeSwitch(int i, String day, String type, boolean storeHistory) {
        if (validDay(day)) {
            if (type.equalsIgnoreCase("day")) {
                if (i < data_day.get(day).size()) {
                    data_day.get(day).remove(i);

                    if (storeHistory) {
                        addToHistory(day);
                    }

                    return true;
                }
            } else if (type.equalsIgnoreCase("night")) {
                if (i < data_night.get(day).size()) {
                    data_night.get(day).remove(i);

                    if (storeHistory) {
                        addToHistory(day);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeAllSwitches(String day, boolean storeHistory) {
        if (validDay(day)) {
            daySwitches = new ArrayList<Integer>();
            nightSwitches = new ArrayList<Integer>();
            data_day.put(day, daySwitches);
            data_night.put(day, nightSwitches);

            if (storeHistory) {
                addToHistory(day);
            }

            return true;
        }

        return false;
    }

    public String int_time_to_string(int time) {
        String hours = Integer.toString(time / 100);
        String mins = Integer.toString(time % 100);
        if (time < 1000)
            hours = "0" + hours;
        if (time - time / 100 * 100 < 10)
            mins = "0" + mins;

        return hours + ":" + mins;
    }

    public String toXML() throws NullPointerException {
        ArrayList<Integer> switches;
        StringBuilder build = new StringBuilder();
        String prefix;
        String suffix = "</week_program>";
        if (!HeatingSystem.getVacationMode())
            prefix = "<week_program state=\"on\">";
        else
            prefix = "<week_program state=\"off\">";

        // Add prefix.
        build.append(prefix).append("\n");
        // Construct all the days.
        for (int i = 0; i < valid_days.length; i++) {
            // Add the day
            String day = valid_days[i];

            build.append("<day name=\"" + day + "\">").append("\n");

            // Add the switches
            switches = data_day.get(day);

            for (int j = 0; j < 5; j++) {
                if (j < switches.size()) {
                    build.append("<switch type=\"day\" state=\"on\">"
                            + int_time_to_string(switches.get(j)) + "</switch>").append("\n");
                } else {
                    build.append("<switch type=\"day\" state=\"off\">00:00</switch>").append("\n");
                }
            }

            switches = data_night.get(day);

            for (int j = 0; j < 5; j++) {
                if (j < switches.size()) {
                    build.append("<switch type=\"night\" state=\"on\">"
                            + int_time_to_string(switches.get(j)) + "</switch>").append("\n");
                } else {
                    build.append("<switch type=\"night\" state=\"off\">00:00</switch>").append("\n");
                }
            }

            // Closing day tag.
            build.append("</day>").append("\n");
        }

        // Add suffix.
        build.append(suffix);

        // System.out.println(build.toString());

        return build.toString();
    }

    void addToHistory(String day) {
        history.add(day, data_day.get(day), data_night.get(day));
    }
}
