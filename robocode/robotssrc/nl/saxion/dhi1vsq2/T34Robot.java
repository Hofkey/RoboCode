package nl.saxion.dhi1vsq2;



import java.awt.Color;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;


public class T34Robot extends AdvancedRobot{

    private boolean moved = false;
    private boolean inCorner = false;
    private String targ;
    private byte spins = 0;
    private byte dir = 1;
    private short prevE;

    @Override
    public void run(){
        setColors(Color.RED, Color.BLACK, Color.WHITE);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        while(true){
            turnRadarLeftRadians(1);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        targ = e.getName();
    }
    @Override
    public void onScannedRobot(ScannedRobotEvent e){
        if(targ == null || spins > 6){
            targ = e.getName();
        }

        if(getDistanceRemaining() == 0 && getTurnRemaining() == 0){
            if(inCorner){
                if(moved){
                    setTurnLeft(90);
                    moved = false;
                }
                else{
                    setAhead(600 * dir);
                    moved = true;
                }
            }
            else{

                if((getHeading() % 90) != 0){
                    setTurnLeft((getY() > (getBattleFieldHeight() / 2)) ? getHeading()
                            : getHeading() - 180);
                }
                else if(getY() > 30 && getY() < getBattleFieldHeight() - 30){
                    setAhead(getHeading() > 90 ? getY() - 20 : getBattleFieldHeight() - getY()
                            - 20);
                }
                else if(getHeading() != 90 && getHeading() != 270){
                    if(getX() < 350){
                        setTurnLeft(getY() > 300 ? 90 : -90);
                    }
                    else{
                        setTurnLeft(getY() > 300? -90 : 90);
                    }
                }
                else if(getX() > 30 && getX() < getBattleFieldWidth() - 30){
                    setAhead(getHeading() < 180 ? getX() - 20 : getBattleFieldWidth() - getX()
                            - 20);
                }
                else if(getHeading() == 270){
                    setTurnLeft(getY() > 200 ? 90 : 180);
                    inCorner = true;
                }
                else if(getHeading() == 90){
                    setTurnLeft(getY() > 200 ? 180 : 90);
                    inCorner = true;
                }
            }
        }
        if(e.getName().equals(targ)){
            spins = 0;

            if((prevE < (prevE = (short)e.getEnergy())) && Math.random() > .85){
                dir *= -1;
            }

            setTurnGunRightRadians(Utils.normalRelativeAngle((getHeadingRadians() + e
                    .getBearingRadians()) - getGunHeadingRadians()));

            if(e.getDistance() < 200){
                setFire(3);
            }
            else{
                setFire(2.4);
            }

            double radarTurn = getHeadingRadians() + e.getBearingRadians()
                    - getRadarHeadingRadians();
            setTurnRadarRightRadians(2 * Utils.normalRelativeAngle(radarTurn));
        }
        else if(targ != null){
            spins++;
        }
    }

}