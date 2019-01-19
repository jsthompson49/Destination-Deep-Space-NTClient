package org.usfirst.frc.team3407.ntclient;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.HttpCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Main {

    private static final int TEAM = 3407;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {

        try {
            //readNavXData(getTable("SmartDashboard"));
            sendHandshake(getTable("MessageServer"));
            //fetchImages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchImages() throws Exception {
        HttpCamera camera = new HttpCamera("video_out", "http://10.34.07.2:1182", HttpCamera.HttpCameraKind.kMJPGStreamer);
        CvSink fetcher = new CvSink("rr_images");
        fetcher.setSource(camera);

        while (true) {
            Mat image = new Mat();
            fetcher.grabFrame(image);
            Imgcodecs.imwrite("C:/Users/jstho/test.jpg", image);
            Thread.sleep(5000);
        }
    }
    private void sendHandshake(NetworkTable table) {
        NetworkTableEntry counter = table.getEntry("processCount");
        long startCount = 0;
        while (true) {
            long count = counter.getNumber(0).longValue();
            System.out.println(String.format("Counter: start=%s count=%s", startCount, count));
            if (count > 0) {
                if ((startCount > 0) && (count > startCount)) {
                    break;
                }
                startCount = count;
            }
            delay(1000);
        }
        table.getEntry("message").setString("Hello World");
        table.getEntry("sender").setString("DriverStationHelper");
        table.getEntry("receiver").setString("RoboRio");

        String response = null;
        while (true) {
            String sender = table.getEntry("sender").getString("");
            if ("RoboRio".equals(sender)) {
                response = table.getEntry("message").getString("");
                break;
            }
        }

        System.out.println("Message from RoboRio is " + response);
    }

    private void readNavXData(NetworkTable table) {
       NetworkTableEntry angleEntry = table.getEntry("NavX/Angle");

        System.out.print("Connecting.");
        while (!table.getInstance().isConnected()) {
            angleEntry.getDouble(0.0);
            delay(1000);
            System.out.print(".");
        }

        while (true) {
            delay(5000);
            double x = angleEntry.getDouble(0.0);
            System.out.println("Angle: " + x);
        }
    }

    private NetworkTable getTable(String name) {
        NetworkTableInstance root = NetworkTableInstance.getDefault();
        root.startClientTeam(TEAM);
        //inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS

        return root.getTable(name);
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            System.out.println("interrupted");
        }
    }
}
