package group28.thermostat;

import android.app.Application;

import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThermoApp extends Application {
    public History history;

    public ThermoApp() {
        super();

        history = new History();
    }

    public History getHistoryInstance() {
        return history;
    }
}