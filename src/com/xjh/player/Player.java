package com.xjh.player;

import com.xjh.map.GameMap;
import com.xjh.util.Point;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by john on 17/2/28.
 */
public class Player {
    //手动枚举变量
    static final char EMPTY = '0';
    static final char MINE = '9';
    static final char UNKNOWN = 'X';
    static final char BOOM = 'B';
    //玩家可见地图
    private char[][] playerMap;
    private Random random;

    public PlayerCommand next(GameMap gameMap){
        this.playerMap=gameMap.getMask();
        this.random=new Random();
        //boolean hasdone=false;
        int width=gameMap.getWidth();
        int height=gameMap.getHeight();
        LinkedList<Point> unknowPoints=new LinkedList<>();
        //map中存放点及其概率
        TreeMap<Double,LinkedList<Point>> highRateNotMinePoint=new TreeMap<>();
        //遍历地图
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char value=this.playerMap[i][j];
                //将未知点全加入列表中，当无法推断确定的解时，随机取点进行左键
                if(value==UNKNOWN){
                    unknowPoints.add(new Point(j,i));
                }
                //如果这个点是数字
                else if (value>='1'&&value<='8') {
                    //取得它周围的点
                    LinkedList<Point> aroundField=gameMap.getAround(j, i);
                    LinkedList<Point>[] classifiedPoints=classifyPoints(aroundField);
                    //周围中已打开的点的集合、已标记成雷的点的集合、未知点的集合
                    LinkedList<Point> aroundNotMinePoints=classifiedPoints[0];
                    //LinkedList<Point> aroundNumPoints=classifiedPoints[0];
                    LinkedList<Point> aroundMinePoints=classifiedPoints[1];
                    LinkedList<Point> aroundUnknowPoints=classifiedPoints[2];

                    //该点周围的雷数
                    int valueInt=Character.getNumericValue(value);

                    //若周围有未知点
                    if(aroundUnknowPoints.size()>0) {
                        //HighRateNotMinePoint.put(unknowPoints,valueInt-aroundMinePoints.size());
                        //若该点周围的雷数等于该点的周围已标记的雷数，
                        // 那么该点周围的其他所有未知点均可打开
                        if (valueInt == aroundMinePoints.size()) {
                            return new PlayerCommand(aroundUnknowPoints, CommandType.left);
                        }
                        //若该点周围的雷数等于周围已标记雷数加上周围未知点数
                        // 则该点周围的未知点均要标记成雷
                        else if (valueInt == (aroundMinePoints.size() + aroundUnknowPoints.size())) {
                            return new PlayerCommand(aroundUnknowPoints, CommandType.rigft);
                        }

                        else{

                            //System.out.println("使用高级找法");

                            for(Point p:aroundNotMinePoints){
                                char anotherValue=this.playerMap[p.getY()][p.getX()];
                                if(anotherValue >= '0' && anotherValue <= '8'){
                                    //取得相邻点的周围的点
                                    LinkedList<Point> anotherAroundField = gameMap.getAround(p.getX(), p.getY());
                                    LinkedList<Point>[] anotherClassifiedPoints=classifyPoints(anotherAroundField);
                                    //周围中已打开的点的集合、已标记成雷的点的集合、未知点和与之前的点的未知点的交集的集合
                                    LinkedList<Point> anotherAroundNotMinePoints = anotherClassifiedPoints[0];
                                    LinkedList<Point> anotherAroundMinePoints = anotherClassifiedPoints[1];
                                    LinkedList<Point> anotherAroundUnknowPoints = anotherClassifiedPoints[2];
                                    LinkedList<Point> publicUnknowPoints = new LinkedList<>();
                                    // 两个相邻点剩余未知点的重合部分
                                    publicUnknowPoints.addAll(aroundUnknowPoints);
                                    publicUnknowPoints.retainAll(anotherAroundUnknowPoints);
                                    if ( publicUnknowPoints.size() > 0) {
                                        // 计算两个点周围的剩余的雷数
                                        int left=value-'0'-aroundMinePoints.size();
                                        int anotherLeft=anotherValue-'0'-anotherAroundMinePoints.size();
                                        //当anotherLeft>left时，公共区最多left个雷，若another点的私有区域的雷数等于another总雷数减去left个雷
                                        //则another的私有未知区全是雷
                                        if(anotherLeft>left && anotherAroundUnknowPoints.size() - publicUnknowPoints.size() == anotherLeft-left){
                                            anotherAroundUnknowPoints.removeAll(publicUnknowPoints);
                                            return new PlayerCommand(anotherAroundUnknowPoints,CommandType.rigft);
                                        }
                                        //两者剩余雷数相同，一者公有区域大小等于其本身未知点数
                                        //则另一个的私有未知区全部可打开
                                        if(anotherLeft==left && aroundUnknowPoints.size() > publicUnknowPoints.size()
                                                && anotherAroundUnknowPoints.size()==publicUnknowPoints.size()){
                                            aroundUnknowPoints.removeAll(publicUnknowPoints);
                                            return new PlayerCommand(aroundUnknowPoints,CommandType.left);
                                        }

                                    }


                                }

                            }
                        }
                        highRateNotMinePoint.put(((double)valueInt-aroundMinePoints.size())/aroundUnknowPoints.size(),aroundUnknowPoints);
                    }

                }
            }
        }
        //System.out.println("无法推断，随机取点");

        /**/
        if (highRateNotMinePoint.size()>0) {
            //System.out.println("最小"+highRateNotMinePoint.firstEntry().getKey());
            //System.out.println("最大"+highRateNotMinePoint.lastEntry().getKey());
            //System.out.println("地图"+((double) gameMap.getUnmarkedMineCount()) / unknowPoints.size());
            //二选一或多选一的偶然性较大效果不是很明显也可能是因为在需要二选一时地图剩余雷数也不多了，两者概率接近
            //二选一或多选一的概率与整体随机点的概率对比选择
            if (highRateNotMinePoint.lastEntry().getKey() - ((double)(unknowPoints.size() - gameMap.getUnmarkedMineCount())) / unknowPoints.size() > 0) {
                Collections.shuffle(highRateNotMinePoint.lastEntry().getValue());
                return new PlayerCommand(highRateNotMinePoint.lastEntry().getValue().subList(0, 1), CommandType.left);
            }
        }
        /**/
        Collections.shuffle(unknowPoints);
        return new PlayerCommand(unknowPoints.subList(0,1),CommandType.left);

    }

    public LinkedList<Point>[]  classifyPoints(LinkedList<Point> points){
        LinkedList<Point>[] pointsArray=new LinkedList[3];
        pointsArray[0]=new LinkedList<>();
        pointsArray[1]=new LinkedList<>();
        pointsArray[2]=new LinkedList<>();
        for (Point p : points) {
            char value = this.playerMap[p.getY()][p.getX()];

            if (value >= '0' && value <= '8') {
                pointsArray[0].add(p);
            }
            else if (value == MINE) {
                pointsArray[1].add(p);
            } else {
                pointsArray[2].add(p);
            }
        }
        return pointsArray;
    }
}
