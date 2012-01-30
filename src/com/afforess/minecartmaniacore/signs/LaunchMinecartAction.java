package com.afforess.minecartmaniacore.signs;

import org.bukkit.util.Vector;

import com.afforess.minecartmaniacore.config.ControlBlockList;
import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public class LaunchMinecartAction implements SignAction {
    private volatile Vector launchSpeed = null;
    private volatile boolean previous = false;
    protected Sign sign;
    
    public LaunchMinecartAction(final Sign sign) {
        this.sign = sign;
    }
    
    public boolean execute(final MinecartManiaMinecart minecart) {
        if (ControlBlockList.getLaunchSpeed(minecart.getSpecificMaterialBeneath()) == 1.0D)
            return false;
        if (minecart.isMoving())
            return false;
        final Vector launch = calculateLaunchSpeed(false);
        if (previous) {
            if ((minecart.getPreviousDirectionOfMotion() != null) && (minecart.getPreviousDirectionOfMotion() != CompassDirection.NO_DIRECTION)) {
                minecart.setMotion(minecart.getPreviousDirectionOfMotion(), 0.6D);
            }
        } else {
            minecart.minecart.setVelocity(launch);
        }
        
        return true;
    }
    
    private Vector calculateLaunchSpeed(final boolean force) {
        if ((launchSpeed == null) || force) {
            previous = false;
            CompassDirection dir = null;
            for (int i = 0; i < sign.getNumLines(); i++) {
                if (sign.getLine(i).toLowerCase().contains("previous dir")) {
                    previous = true;
                    break;
                }
                final String line = sign.getLine(i).toLowerCase().trim();
                if (line.contains("launch")) {
                    if (line.contains("player")) {
                        // TODO: Handle "launch player"
                    } else {
                        dir = CompassDirection.valueOf(line.substring(7+line.toLowerCase().lastIndexOf("launch")).toUpperCase());
                        if (dir != null) {
                            break;
                        }
                    }
                }
            }
            if ((dir != null) || previous) {
                sign.addBrackets();
                launchSpeed = dir.toVector(0.6);
            }
        }
        return launchSpeed;
    }
    
    public boolean async() {
        return true;
    }
    
    public boolean valid(final Sign sign) {
        calculateLaunchSpeed(true);
        return (launchSpeed != null) || previous;
    }
    
    public String getName() {
        return "launchersign";
    }
    
    public String getFriendlyName() {
        return "Launcher Sign";
    }
    
}
