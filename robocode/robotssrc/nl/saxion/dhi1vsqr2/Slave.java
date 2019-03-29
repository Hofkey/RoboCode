package nl.saxion.dhi1vsqr2;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;

import robocode.*;
import robocode.util.Utils;
import nl.saxion.dhi1vsqr2.Point;
import nl.saxion.dhi1vsqr2.RobotColors;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Slave extends TeamRobot {
    final static int RANK = 2;

    final static double BULLET_POWER=3;//Our bulletpower.
    final static double BULLET_DAMAGE=BULLET_POWER*4;//Formula for bullet damage.
    final static double BULLET_SPEED=20-3*BULLET_POWER;//Formula for bullet speed.


    static double dir=1;
    static double oldEnemyHeading;
    static double enemyEnergy;

    public void run(){
        while (true) {

            setTurnRadarRight(10000);

            ahead(100);

            back(100);
        }
    }

    public void onMessageReceived(MessageEvent e) {
        // Fire at a point
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
            fireAtBot((ScannedRobotEvent) e.getMessage());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e){
        if (isTeammate(e.getName())) {
            return;
        } else {
            fireAtBot(e);
        }
    }

    private void fireAtBot(ScannedRobotEvent e) {
        double absBearing=e.getBearingRadians()+getHeadingRadians();

        double turn=absBearing+Math.PI/2;

        turn-=Math.max(0.5,(1/e.getDistance())*100)*dir;

        setTurnRightRadians(Utils.normalRelativeAngle(turn-getHeadingRadians()));

        setMaxVelocity(400/getTurnRemaining());

        setAhead(100*dir);

        double enemyHeading = e.getHeadingRadians();
        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
        oldEnemyHeading = enemyHeading;

        double deltaTime = 0;
        double predictedX = getX()+e.getDistance()*Math.sin(absBearing);
        double predictedY = getY()+e.getDistance()*Math.cos(absBearing);
        while((++deltaTime) * BULLET_SPEED <  Point2D.Double.distance(getX(), getY(), predictedX, predictedY)){

            predictedX += Math.sin(enemyHeading) * e.getVelocity();
            predictedY += Math.cos(enemyHeading) * e.getVelocity();

            enemyHeading += enemyHeadingChange;

            predictedX=Math.max(Math.min(predictedX,getBattleFieldWidth()-18),18);
            predictedY=Math.max(Math.min(predictedY,getBattleFieldHeight()-18),18);

        }

        double aim = Utils.normalAbsoluteAngle(Math.atan2(  predictedX - getX(), predictedY - getY()));

        setTurnGunRightRadians(Utils.normalRelativeAngle(aim - getGunHeadingRadians()));
        setFire(BULLET_POWER);

        setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing-getRadarHeadingRadians())*2);
    }

    public void onBulletHit(BulletHitEvent e){
        enemyEnergy-=BULLET_DAMAGE;
    }


    public void onHitWall(HitWallEvent e){
        dir=-dir;
    }
}