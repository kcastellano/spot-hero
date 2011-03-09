/*
 * StartApplication.java
 *
 * Created on Jul 22, 2010 10:15:04 AM;
 */
package org.sunspotworld;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.*;

import java.io.*;
import javax.microedition.io.*;
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
    private static final int PORT = 77;
    private static final short PAN_ID = IRadioPolicyManager.DEFAULT_PAN_ID;
    private static final int SECONDS = 30;
    private static final String NECK_ADDRESS = "0014.4F01.0000.1514";
    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    RadiogramConnection txConn = null;

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

    public void sendDatagram() {
        System.out.println("Sending to: " + NECK_ADDRESS);
        Radiogram xrg;
        boolean running = true;
        while (running) {
            try {
                xrg = (Radiogram) txConn.newDatagram(txConn.getMaximumLength());
                while (running) {
                    xrg.reset();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    txConn.send(xrg);
                    turnLedsOn();
                }
            } catch (IOException iOException) {
//                iOException.printStackTrace();
            }
        }
    }

    private void initialize() {
        IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(CHANNEL);
        rpm.setPanId(PAN_ID);
        try {
            txConn = (RadiogramConnection) Connector.open("radiogram://" + NECK_ADDRESS + ":" + PORT);
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
        txConn.setTimeout(5 * SECONDS);
    }

    private void turnLedsOn() {
        for (int i = 0; i < leds.length; i++) {
            leds[i].setRGB(0, 100, 0);
            leds[i].setOn();
        }
    }

    private void run() {
        sendDatagram();
    }
}
