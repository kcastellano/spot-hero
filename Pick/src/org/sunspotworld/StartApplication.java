/*
 * StartApplication.java
 *
 * Created on Jul 22, 2010 10:43:31 AM;
 */
package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.resources.Resources;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import java.io.*;
import javax.microedition.io.Connector;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class StartApplication extends MIDlet {

    private static final int CHANNEL = 13;
    private static final int BASE_STATION_PORT = 79;
    private static final short PAN_ID = IRadioPolicyManager.DEFAULT_PAN_ID;
    private static final int SECONDS = 30;
    private static final String BASE_STATION_ADDRESS = "0014.4F01.0000.6755";
    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    private IAccelerometer3D accelerometer;
    RadiogramConnection sendConnection = null;

    protected void startApp() throws MIDletStateChangeException {
        initialize();
        run();
    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     * 
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true when this method is called, the MIDlet must
     *    cleanup and release all resources. If false the MIDlet may throw
     *    MIDletStateChangeException  to indicate it does not want to be destroyed
     *    at this time.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        for (int i = 0; i < 8; i++) {
            leds[i].setOff();
        }
    }

    private void strum() {
        double threshold = -0.5;
        try {
            while (true) {
                double tilt = accelerometer.getTiltZ();
                if (tilt < threshold) {
                    System.out.println("Strumming @ " + tilt);
                    turnLedsOn();
                    sendStrummed(tilt);
                    turnLedsOff();
                }
                Thread.yield();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendStrummed(double tilt) {
        Radiogram sending;
        try {
            sending = (Radiogram) sendConnection.newDatagram(sendConnection.getMaximumLength());
            sending.reset();
            sending.writeDouble(tilt);
            sendConnection.send(sending);
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
    }

    private void turnLedsOn() {
        for (int i = 0; i < leds.length; i++) {
            leds[i].setRGB(0, 0, 100);
            leds[i].setOn();
        }
    }

    private void turnLedsOff() {
        for (int i = 0; i < leds.length; i++) {
            leds[i].setOff();
        }
    }

    private void blink() {
        turnLedsOn();
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
//            ex.printStackTrace();
        }
        turnLedsOff();
    }

    private void run() {
        strum();
    }

    private void initialize() {
        accelerometer = (IAccelerometer3D) Resources.lookup(IAccelerometer3D.class);
        IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(CHANNEL);
        rpm.setPanId(PAN_ID);
        try {
            sendConnection = (RadiogramConnection) Connector.open("radiogram://" + BASE_STATION_ADDRESS + ":" + BASE_STATION_PORT);
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
//        sendConnection.setTimeout(5 * SECONDS);
    }
}
