package dan.ko.sensors;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button pauseButton;
    private Button resumeButton;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroSensor;

    private Runnable graphTimer;

    private final Handler handler = new Handler();

    private double graphLastValue = -1d;

    static GraphView graphAcc;
    static GraphView graphGyro;
    static LineGraphSeries<DataPoint> seriesAccX;
    static LineGraphSeries<DataPoint> seriesGyroX;

    static ArrayList<Float> secAccData = new ArrayList<>();
    static ArrayList<Float> secGyroData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pauseButton = (Button) this.findViewById(R.id.pauseButton);
        resumeButton = (Button) this.findViewById(R.id.resumeButton);
        graphAcc = (GraphView) this.findViewById(R.id.graphAcc);
        graphGyro = (GraphView) this.findViewById(R.id.graphGyro);

        View.OnClickListener oclPauseButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        };
        pauseButton.setOnClickListener(oclPauseButton);

        View.OnClickListener oclResumeButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        };
        resumeButton.setOnClickListener(oclResumeButton);

        startService(new Intent(this, SensorService.class));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        seriesAccX = new LineGraphSeries<>();
        seriesAccX.setTitle("Accelerometer");
        graphAcc.addSeries(seriesAccX);
        graphAcc.getLegendRenderer().setVisible(true);
        graphAcc.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphAcc.getViewport().setXAxisBoundsManual(true);
        graphAcc.getViewport().setMinX(-5);
        graphAcc.getViewport().setMaxX(5);
        graphAcc.getViewport().setScalable(true);
        graphAcc.getViewport().setScrollable(true);

        seriesGyroX = new LineGraphSeries<>();
        seriesGyroX.setTitle("Gyroscope");
        graphGyro.addSeries(seriesGyroX);
        graphGyro.getLegendRenderer().setVisible(true);
        graphGyro.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphGyro.getViewport().setXAxisBoundsManual(true);
        graphGyro.getViewport().setMinX(-5);
        graphGyro.getViewport().setMaxX(5);
        graphGyro.getViewport().setScalable(true);
        graphGyro.getViewport().setScrollable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(graphTimer);
        sensorManager.unregisterListener(this);
        stopService(new Intent(MainActivity.this, SensorService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_UI);
        startService(new Intent(MainActivity.this, SensorService.class));

        graphTimer = new Runnable() {
            @Override
            public void run() {
                float[] accTemp = new float[secAccData.size()];
                for (int i = 0; i < secAccData.size(); i++) {
                    accTemp[i] = secAccData.get(i).floatValue();
                }

                float[] gyroTemp = new float[secGyroData.size()];
                for (int i = 0; i < secGyroData.size(); i++) {
                    gyroTemp[i] = secGyroData.get(i).floatValue();
                }

                graphLastValue += 1d;
                seriesAccX.appendData(new DataPoint(graphLastValue, GetAverageJNI.nativeGetAverage(accTemp, secAccData.size())), true, 300);
                seriesGyroX.appendData(new DataPoint(graphLastValue, GetAverageJNI.nativeGetAverage(gyroTemp, secGyroData.size())), true, 300);

                secAccData.clear();
                secGyroData.clear();

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(graphTimer, 1000);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                secAccData.add(event.values[0]);
                break;

            case Sensor.TYPE_GYROSCOPE:
                secGyroData.add(event.values[0]);
                break;

            default: return;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
