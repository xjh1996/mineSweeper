package com.xjh.util;

/**
 * Created by john on 17/2/27.
 */
public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point postion = (Point) obj;
            return (this.x == postion.x) && (this.y == postion.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return ("" + this.x + "," + this.y);
    }

    public int[] toArray(){
        int[] arr={this.x,this.y};
        return arr;
    }

    public static Point parsePoint(String str)throws RuntimeException{
        String[] strs = str.split(",");
        if(strs. length > 2){
            throw new RuntimeException("输入格式有误，请输入以想x,y格式的点");
        }
        return new Point(Integer. valueOf(strs[0]),Integer. valueOf(strs[1]));
    }
}
