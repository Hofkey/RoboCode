package nl.saxion.dhi1vsqr2;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.util.Utils;
import nl.saxion.dhi1vsqr2.Point;
import nl.saxion.dhi1vsqr2.RobotColors;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

public class ProjectLeader extends TeamRobot {
    final static double BULLET_POWER=3;//Our bulletpower.
    final static double BULLET_DAMAGE=BULLET_POWER*4;//Formula for bullet damage.
    final static double BULLET_SPEED=20-3*BULLET_POWER;//Formula for bullet speed.


    static double dir=1;
    static double oldEnemyHeading;
    static double enemyEnergy;

    /**
     * run:  Leader's default behavior
     */
    public void run() {
        // Prepare RobotColors object
        RobotColors c = new RobotColors();

        c.bodyColor = Color.yellow;
        c.gunColor = Color.yellow;
        c.radarColor = Color.yellow;
        c.scanColor = Color.red;
        c.bulletColor = Color.green;

        // Set the color of this robot containing the RobotColors
        setBodyColor(c.bodyColor);
        setGunColor(c.gunColor);
        setRadarColor(c.radarColor);
        setScanColor(c.scanColor);
        setBulletColor(c.bulletColor);
        try {
            // Send RobotColors object to our entire team
            broadcastMessage(c);
        } catch (IOException ignored) {}
        // Normal behavior
        while (true) {
            setTurnRadarRight(1000);
            // Tell the game that when we take move,
            // we'll also want to turn right... a lot.
            setTurnRight(1000);
            // Limit our speed to 5
            setMaxVelocity(10);
            // Start moving (and turning)
            ahead(1000);
            // Repeat.
        }
    }

    /**
     * onScannedRobot:  What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // Don't fire on teammates
        if (isTeammate(e.getName())) {
            return;
        } else {
            // Calculate enemy bearing
            double enemyBearing = this.getHeading() + e.getBearing();
            // Calculate enemy's position
            double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
            double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
            try {
                // Send enemy position to teammates
                broadcastMessage(new Point(enemyX, enemyY));
            } catch (IOException ex) {
                out.println("Unable to send order: ");
                ex.printStackTrace(out);
            }

            fireAtBot(e);
        }
    }

    private void fireAtBot(ScannedRobotEvent e) {
        turnGunRight(e.getBearing());
        fire(3);
        if(e.getDistance() < 50) {
            fire(.5);
        }
    }

    /**
     * onHitByBullet:  Turn perpendicular to bullet path
     */
    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }
}
