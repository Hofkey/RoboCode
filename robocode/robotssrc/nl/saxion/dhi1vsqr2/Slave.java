package nl.saxion.dhi1vsqr2;

import java.awt.geom.Point2D;

import robocode.*;
import robocode.util.Utils;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Slave extends TeamRobot {
    final static int RANK = 2;

    static boolean isChasing = false;

    final static double BULLET_POWER=3;//Our bulletpower.

    public void run(){
        while (true) {
            setTurnRadarRight(10000);

            ahead(100);

            back(100);
        }
    }

    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof Point) {
            Point p = (Point) e.getMessage();
            // Calculate x and y to target
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            // Calculate angle to target
            double theta = Math.toDegrees(Math.atan2(dx, dy));

            // Turn gun to target
            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
            // Fire hard!
            fire(3);
        } // Set our colors
        else if (e.getMessage() instanceof RobotColors) {
            RobotColors c = (RobotColors) e.getMessage();

            setBodyColor(c.bodyColor);
            setGunColor(c.gunColor);
            setRadarColor(c.radarColor);
            setScanColor(c.scanColor);
            setBulletColor(c.bulletColor);
        } else if(e.getMessage() instanceof  ScannedRobotEvent) {
            if(!isChasing) {
                ScannedRobotEvent sre = (ScannedRobotEvent) e.getMessage();
                double angle = sre.getBearing();
                turnRight(angle);
                ahead(80);
                this.isChasing = true;
            }
            fireAtBot((ScannedRobotEvent) e.getMessage());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e){
        if (isTeammate(e.getName())) {
            return;
        } else {
            double angle = e.getBearing();
            turnRight(angle);
            ahead(80);
            fireAtBot(e);
            this.isChasing = false;
        }
    }

    private void fireAtBot(ScannedRobotEvent e) {
        turnGunRight(e.getBearing());
        fire(3);
        if(e.getDistance() < 50) {
            fire(.5);
        }
    }

    public void onHitWall(HitWallEvent e){
        turnRight(90);
    }
}