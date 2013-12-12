package com.rogue.bauble.device;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import static com.google.common.base.Preconditions.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.rogue.bauble.misc.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simplified interface for retrieving magneto/accelometer values.
 * 
 * @author R. Matt McCann
 */
@Singleton
public class SensorMeister implements SensorEventListener {
    /** Last read accelerometer values. */
    private float[] lastAccelerometerValues = null;
    
    /** Last read magnetometer values. */
    private float[] lastMagnetometerValues = null;
    
    /** Interface for logging events. */
    private final Logger logger = LoggerFactory.getLogger("SensorMeister");
    
    /** 
     * Rotation of the device around the x-axis. (X-axis points towards the
     * west, parallel to the ground.
     */
    private float pitch = 0.0f;
    
    /** 
     * Rotation of the device around the y-axis. (Y-axis points towards the
     * magnetic north.
     */
    private float roll = 0.0f;
    
    /** Interface for accessing internal Glass accelo/magnetometers. */
    private final SensorManager sensorManager;
    
    /** 
     * Rotation of the device around the z-axis. (Z-axis points towards the
     * center of the earth. 
     */
    private float yaw = 0.0f;
    
    /** Guice compatible constructor. */
    @Inject
    public SensorMeister(EventBus notifier,
                         SensorManager sensorManager) {
        this.sensorManager = checkNotNull(sensorManager);
        
        notifier.register(this);
    }
    
    /** Computes the orientation of the device. */
    private void computeOrientation() {
        logger.debug("Computing orientation...");
        float[] inclinationMatrix = null; // Not using this
        float[] rotationMatrix = new float[16];
        
        // If the rotation of the device could be calculated (can't in free fall)
        if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, 
                lastAccelerometerValues, lastMagnetometerValues)) {
            float[] adjRotationMatrix = new float[16];
            float[] orientationMatrix = new float[3];
            
            // Translate the rotation matrix into on-your-head space
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X, SensorManager.AXIS_Z,
                    adjRotationMatrix);
            
            // Calculate the orientation of the device
            SensorManager.getOrientation(adjRotationMatrix, orientationMatrix);
            yaw = orientationMatrix[0] * Constants.RADIANS_TO_DEGREES;
            pitch = -orientationMatrix[1] * Constants.RADIANS_TO_DEGREES;
            roll = orientationMatrix[2] * Constants.RADIANS_TO_DEGREES;
            
            logger.debug("Yaw:   " + yaw);
            logger.debug("Pitch: " + pitch);
            logger.debug("Roll:  " + roll);
        }
    }
    
    public float getPitch() { return pitch; }
    
    public float getRoll() { return roll; }
    
    public float getYaw() { return yaw; }
    
    /** {@inheritDocs} */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do nothing!
    }
    
    /** Disables listening to the hardware when the application goes to sleep. */
    @Subscribe
    public void onPause(OnPauseEvent event) {
        logger.info("Pausing...");
        sensorManager.unregisterListener(this);
    }
    
    /** Enables listening to the hardware when the application awakens or starts. */
    @Subscribe
    public void onResume(OnResumeEvent event) {
        logger.info("Resuming...");
        sensorManager.registerListener(this, 
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /** {@inheritDocs} */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // If the accelerometer has updated
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            logger.debug("Accelerometer has changed!");
            final int startPos = 0;
            
            // If this is the first value read
            if (lastAccelerometerValues == null) {
                // Initialize the array
                lastAccelerometerValues = new float[3];
            }
            
            // Save the new accelerometer values
            System.arraycopy(event.values, startPos, 
                    lastAccelerometerValues, startPos, event.values.length);
        }
        // If the magnetometer has updated
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            logger.debug("Magnetometer has changed!");
            final int startPos = 0;
            
            // If this is ther first value read
            if (lastMagnetometerValues == null) {
                // Initialize the array
                lastMagnetometerValues = new float[3];
            }
            
            // Save the new magnetometer values
            System.arraycopy(event.values, startPos,
                    lastMagnetometerValues, startPos, event.values.length);
        }
        
        // If we have enough sensor data to compute orientation
        if (lastAccelerometerValues != null && lastMagnetometerValues != null) {
            computeOrientation();
        }
    }
}
