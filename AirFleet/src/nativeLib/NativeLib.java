package nativeLib;

import model.Avion;

public class NativeLib {

    static {
        System.loadLibrary("AirFleetC"); // le .dll compilé depuis C
    }

    // Fonctions natives
    public native Avion[] trierParCrashs(Avion[] avions);
    public native Avion[] trierParAutonomie(Avion[] avions);
    public native double moyenneCrashs(Avion[] avions);
    public native double moyenneAutonomie(Avion[] avions);
}
