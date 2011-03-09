/*
 * StartApplication.java
 *
 * Created on Jul 22, 2010 11:40:11 PM;
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

    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    private final static int POCKET_PORT = 77;
    private final static int AMPLIFIER_PORT = 78;
    private String AMPLIFIER_ADDRESS = "0014.4F01.0000.6755";
    private static final int INITIAL_CHANNEL_NUMBER = 13;
    private int channel = INITIAL_CHANNEL_NUMBER;
    private static final short PAN_ID = IRadioPolicyManager.DEFAULT_PAN_ID;
    private RadiogramConnection receiveConnection = null;
    private RadiogramConnection sendConnection = null;
    private static final int SECONDS = 30;
    int signalStrength = 0;

    protected void startApp() throws MIDletStateChangeException {
        initialize();
        receiveRSSI();
    }

    /**
     * Method that receive the RSSI between the Guitar Neck and the Pocket
     */
    private void receiveRSSI() {
        Radiogram receive;
        boolean running = true;
        while (running) {
            try {
                receive = (Radiogram) receiveConnection.newDatagram(receiveConnection.getMaximumLength());
                while (running) {
                    receive.reset();
                    receiveConnection.receive(receive);
                    signalStrength = receive.getRssi();
                    int range = getDistanceRange(signalStrength);
                    setFretColors(range);
                    System.out.println("Distance range:"+ range);
                    System.out.println("The signal strength:" + signalStrength);
                    sendRSSI(signalStrength);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method that sends the distance to the Amplifier
     * @param distance
     */
    private void sendRSSI(int distance) {
        Radiogram send;
        try {
            send = (Radiogram) sendConnection.newDatagram(sendConnection.getMaximumLength());
            send.reset();
            send.writeInt(distance);
            sendConnection.send(send);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


/**
 * Calculates the range in the fret in which the Neck is positioned
 * @param distance
 * @return the range
 */
    private int getDistanceRange(int distance) {
        int min = -5;
        int max = 0;
        for (int i = 0; i < 8; i++) {
            if ((distance >= min) && (distance < max)) {
                return 8 - i;
            } else {
                min += 5;
                max += 5;
            }
        }
        return 0;
    }

    /**
     * Determines the amount of the colors in which the leds will turn on
     * @param red
     * @param green
     * @param blue
     */
    private void turnLedsOn(int red, int green, int blue) {
        for (int i = 0; i < leds.length; i++) {
             leds[i].setRGB(red, green, blue);
             leds[i].setOn();
        }
    }

    /**
     * Determines the color based in the range
     * @param range
     */
    private void setFretColors(int range) {
        int red = 100;
        int green = 100;
        int blue = 100;

        switch (range) {
            case -1:
                break;
            case 0:
                red = 0;
                green = 255;
                blue = 255;
                break;
            case 1:
                red = 0;
                green = 0;
                blue = 255;
                break;
            case 2:
                red = 0;
                green = 255;
                blue = 150;
                break;
            case 3:
                red = 0;
                green = 255;
                blue = 0;
                break;
            case 4:
                red = 255;
                green = 255;
                blue = 0;
                break;
            case 5:
                red = 255;
                green = 150;
                blue = 0;
                break;
            default:
                red = 255;
                green = 0;
                blue = 0;
                break;
        }
        turnLedsOn(red, green, blue);
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

    /**
     * Method used to initialize the variables of the radio
     */
    private void initialize() {
        IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(channel);
        rpm.setPanId(PAN_ID);
        try {
            receiveConnection = (RadiogramConnection) Connector.open("radiogram://:" + POCKET_PORT);
            sendConnection = (RadiogramConnection) Connector.open("radiogram://" + AMPLIFIER_ADDRESS + ":" + AMPLIFIER_PORT);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
