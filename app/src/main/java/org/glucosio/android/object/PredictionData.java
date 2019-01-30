package org.glucosio.android.object;

public class PredictionData extends GlucoseData {

    public double trend = -1;
    public double confidence = -1;
    public Result errorCode;
    public int attempt;

    public PredictionData() {
    }

    public enum Result {
        OK,
        ERROR_NO_NFC,
        ERROR_NFC_READ
    }

}
