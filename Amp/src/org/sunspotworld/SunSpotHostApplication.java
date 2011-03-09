/*
 * SunSpotHostApplication.java
 *
 * Created on Jul 23, 2010 12:46:01 AM;
 */
package org.sunspotworld;

import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.Spot;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import org.jfugue.Player;

/**
 * Sample Sun SPOT host application
 */
public class SunSpotHostApplication {

    private final static int NECK_PORT = 78;
    private final static int PICK_PORT = 79;
    private final static int CHANNEL = 13;
    private final static short PAN_ID = IRadioPolicyManager.DEFAULT_PAN_ID;
    private RadiogramConnection neckConnection = null;
    private RadiogramConnection pickConnection = null;
    private final static int SECONDS = 30;
    private String chord = "";
    private boolean running = true;
    private int lastDistance = 0;
    Player player;

    /**
     * Print out our radio address.
     */
    public void run() {
        initialize();
        neckThread.start();
        getStrummed();
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) {
        SunSpotHostApplication app = new SunSpotHostApplication();
        app.run();
    }

    private void getFret() {
        Radiogram receive;
        boolean running = true;
        try {
            receive = (Radiogram) neckConnection.newDatagram(neckConnection.getMaximumLength());
            receive.reset();
            neckConnection.receive(receive);
            lastDistance = receive.readInt();
//            System.out.println("The distance of the fret is: " + lastDistance);
        } catch (IOException ex) {
//                ex.printStackTrace();
        }
    }

    private void getStrummed() {
        Radiogram receive;
        double tilt = 0;
        while (running) {
            try {
                receive = (Radiogram) pickConnection.newDatagram(pickConnection.getMaximumLength());
                while (running) {
                    System.out.println("Waiting to be strummed");
                    receive.reset();
                    pickConnection.receive(receive);
                    tilt = receive.readDouble();
//                    System.out.println("The pick is strumming at tilt: " + tilt);
                    strum(lastDistance);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private Thread neckThread = new Thread() {

        public void run() {
            while (running) {
                getFret();
            }
        }
    };

    private void initialize() {
        IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(CHANNEL);
        rpm.setPanId(PAN_ID);
        try {
            pickConnection = (RadiogramConnection) Connector.open("radiogram://:" + PICK_PORT);
            neckConnection = (RadiogramConnection) Connector.open("radiogram://:" + NECK_PORT);
        } catch (IOException ex) {
            Logger.getLogger(SunSpotHostApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
//        pickConnection.setTimeout(5 * SECONDS);
        neckConnection.setTimeout(5 * SECONDS);
        player = new Player();
    }

    private void strum(int distance) {
        int i = 0;
        if (distance < 0) {
            chord = "Cmini"; //do
        } else if ((distance >= 0) && (distance < 5)) {
            chord = "Dmini"; //re
        } else if ((distance >= 5) && (distance < 10)) {
            chord = "Emini"; //mi
        } else if ((distance >= 10) && (distance < 15)) {
            chord = "Fmini"; //fa
        } else if ((distance >= 20) && (distance < 25)) {
            chord = "Gmini"; //sol
        } else if ((distance >= 25) && (distance < 30)) {
            chord = "Amini"; //la
        } else if (distance >= 30) {
            chord = "Bmini"; //si
        }
        System.out.println("Chord played: " + chord);
        player.play("V0 I[DISTORTION_GUITAR] V0 " + chord);
        if (i >= 5) {
            i = 0;
            player.close();
            player = new Player();
        } else {
            i++;
        }

    }
}
