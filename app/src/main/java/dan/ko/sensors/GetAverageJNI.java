package dan.ko.sensors;

import java.util.ArrayList;

public class GetAverageJNI {
    static {
        System.loadLibrary("native-lib");
    }

    public static native float nativeGetAverage(float[] array, int size);

}
