package dan.ko.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import static dan.ko.sensors.MainActivity.accelerometerSensor;
import static dan.ko.sensors.MainActivity.gyroSensor;
import static dan.ko.sensors.MainActivity.sensorManager;

public class SensorService extends Service implements SensorEventListener {

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_UI);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                MainActivity.secAccData.add(event.values[0]);
                if (MainActivity.secAccData.size() > 50) {
                    MainActivity.secAccData.remove(0);
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                MainActivity.secGyroData.add(event.values[0]);
                if (MainActivity.secGyroData.size() > 50){
                    MainActivity.secGyroData.remove(0);
                }
                break;

            default: return;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
