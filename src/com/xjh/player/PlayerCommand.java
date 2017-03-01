package com.xjh.player;

import com.xjh.util.Point;

import java.util.List;

/**
 * Created by john on 17/2/28.
 */
public class PlayerCommand {

    private List<Point> points;
    private CommandType commandType;

    public PlayerCommand(List<Point> points,CommandType commandType){
        this.points=points;
        this.commandType=commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }


}
