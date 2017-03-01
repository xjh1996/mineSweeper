package com.xjh.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by john on 17/2/27.
 */

/***
 * 随机坐标生成工具
 */
public class RandomPointGenerator {

    /***
     * 用于在一个二维区域内生成多个不重复的随机坐标
     * @param width 地图宽度
     * @param height 地图高度
     * @param count 雷的数目
     * @return 不重复坐标的雷的集合
     */
    public static Set<Point> getRandomPoints(int width, int height, int count) {
        Random random = new Random();
        Set<Point> set = new HashSet<>();
        int length = width * height;
        //可先用list创建[0,width*height)的一个集合，用Collections.shuffle取出前count个来节省时间，
        //但是费内存，而且初始化一个超长的集合循环赋值也费时
        //而且hashSet判断是否重复直到取满指定数量在密度不高时还好
        do {
            //random.nextInt(length)的范围为[0,length)
            int position = random.nextInt(length);
            //System.out.println(position);
            Point p=new Point(position % width, position / width);
            //pos从坐标（0,0）开始算
            set.add(p);
        } while (set.size() < count);
        return set;
    }
}
