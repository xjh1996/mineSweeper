package com.xjh.map;

import com.xjh.util.Point;
import com.xjh.util.RandomPointGenerator;

import java.util.*;

/**
 * Created by john on 17/2/28.
 */
public class GameMap {
    //手动枚举变量
    static final char EMPTY = '0';
    static final char MINE = '9';
    static final char UNKNOWN = 'X';
    static final char BOOM = 'B';

    //地图宽度，高度，地雷数，实际地图，玩家可见地图
    private int width;
    private int height;
    private int mineCount;
    private int markedMineCount;
    private char[][] map;
    private char[][] mask;

    /***
     * 扫雷地图初始化
     *
     * @param width     地图宽度
     * @param height    地图高度
     * @param mineCount 地雷数
     */
    public GameMap(int width, int height, int mineCount) {
        //设定地图大小
        this.width = width;
        this.height = height;
        //初始化实际地图和玩家可见地图
        this.map = new char[height][width];
        this.mask = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = EMPTY;
                mask[i][j] = UNKNOWN;
            }
        }
        //将地雷放入地图
        this.mineCount = mineCount;
        //生成mineCount个不重复的坐标作为地雷点
        Set<Point> mines = RandomPointGenerator.getRandomPoints(width, height, mineCount);
        putMines(mines);
        this.markedMineCount=0;
    }

    /***
     * 左键单击，是数字则将mask赋值为该数字，空块则扩展，遇雷则失败
     *
     * @param x 左键单击的横坐标
     * @param y 左键单击的纵坐标
     * @return 是否失败，遇雷返回false
     * @throws RuntimeException 坐标有误抛出异常
     */
    public boolean leftClick(int x, int y)
            throws RuntimeException {
        if (!isInMap(x, y)) {
            throw new RuntimeException("输入坐标不在地图范围内");
        }
        char value = this.map[y][x];

        if (mask[y][x] == MINE) {
            return true;
        }
        //是雷，直接失败
        if (value == MINE) {
            return false;
        }
        //该点不是雷，但是有数字
        else if (value > EMPTY) {
            this.mask[y][x] = value;
            return true;
        }
        //如果为空白点（周围无雷，则可扩展至周围全为数字
        this.mask[y][x] = value;
        LinkedList<Point> aroundField = getAround(x, y);
        while (aroundField.size() > 0) {
            Point next = aroundField.getFirst();
            aroundField.removeFirst();
            int nextX = next.getX();
            int nextY = next.getY();
            if (this.mask[nextY][nextX] == UNKNOWN) {
                this.mask[nextY][nextX] = this.map[nextY][nextX];
                if (this.mask[nextY][nextX] == EMPTY) {
                    // 如果周围区域仍是EMPTY，则将此位置的周围加入待扩展列表
                    aroundField.addAll(this.getAround(nextX, nextY));
                }
            }
        }
        return true;


    }

    /***
     * 左键单击，是数字则将mask赋值为该数字，空块则扩展，遇雷则失败
     *
     * @param point 左键单击的坐标
     * @return 是否失败，遇雷返回false
     * @throws RuntimeException 坐标有误抛出异常
     */
    public boolean leftClick(Point point)
            throws RuntimeException {
        return leftClick(point.getX(), point.getY());
    }

    /***
     * 右键单击，标记or取消标记雷区,
     * @param x 右键单击的横坐标
     * @param y 右键单击的纵坐标
     * @return 是否失败
     * @throws RuntimeException 坐标有误抛出异常
     */
    public boolean rightClick(int x, int y)
            throws RuntimeException {
        if (!isInMap(x, y)) {
            throw new RuntimeException("输入坐标不在地图范围内");
        }
        char value = this.mask[y][x];
        if (this.mask[y][x] == MINE) {
            this.mask[y][x] = UNKNOWN;
            this.markedMineCount--;
            return true;
        }
        if (this.mask[y][x] != UNKNOWN) {
            return true;
        }
        this.mask[y][x] = MINE;
        this.markedMineCount++;
        return true;

    }

    /***
     * 右键单击，标记or取消标记雷区,
     * @param point
     * @return 右键单击的坐标
     * @throws RuntimeException 坐标有误抛出异常
     */
    public boolean rightClick(Point point)
            throws RuntimeException {
        return rightClick(point.getX(),point.getY());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /***
     * 获得地图中总地雷数
     *
     * @return 地图中总地雷数
     */
    public int getMineCount() {
        return this.mineCount;
    }

    /***
     * 取得该坐标点周围的不超过地图边界的坐标点的集合
     *
     * @param x 该点的横坐标
     * @param y 该点的纵坐标
     * @return 该坐标点周围的不超过地图边界的坐标点的集合
     */
    public LinkedList<Point> getAround(int x, int y) {
        LinkedList<Point> list = new LinkedList<>();
        for (int i = y - 1; i <= y + 1; i++) {
            for (int j = x - 1; j <= x + 1; j++) {
                if (isInMap(j, i)) {
                    if (i != y || j != x) {
                        list.add(new Point(j, i));
                    }
                }
            }
        }
        return list;
    }

    /***
     * 点是否超出地图
     *
     * @param x 点的横坐标
     * @param y 点的纵坐标
     * @return 未超出地图返回true，否则返回false
     */
    public boolean isInMap(int x, int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.height;
    }

    /***
     * 点是否超出地图
     *
     * @param point 要检测的点
     * @return 未超出地图返回true，否则返回false
     */
    public boolean isInMap(Point point) {
        return this.isInMap(point.getX(), point.getY());
    }

    /***
     * 检测是否胜利
     *
     * @return 胜利返回true，否则false
     */
    public boolean isWin() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                //只要有一个位置不是雷，且没被翻开，就没获胜
                if (this.map[i][j] != MINE && this.mask[i][j] == UNKNOWN) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getUnmarkedMineCount(){
        return mineCount-markedMineCount;
    }

    /***
     * 放入一颗地雷
     *
     * @param pos 放入点
     * @return 放入成功返回true，否则返回false
     */
    public boolean putMine(Point pos) {

        return this.putMine(pos.getX(), pos.getY());
    }

    /***
     * 放入一颗地雷
     *
     * @param x 放入地雷的横坐标
     * @param y 放入地雷的纵坐标
     * @return 放入成功返回true，否则返回false
     */
    public boolean putMine(int x, int y) {
        if (this.map[y][x] != MINE) {
            this.map[y][x] = MINE;
            List<Point> aroundField = this.getAround(x, y);
            for (Point aroundPos : aroundField) {
                if (this.map[aroundPos.getY()][aroundPos.getX()] != MINE) {
                    this.map[aroundPos.getY()][aroundPos.getX()]++;
                }

            }
            return true;
        }
        return false;
    }

    /***
     * 放入多颗地雷，当有一颗放入失败时，中断循环，返回false
     *
     * @param mines 使用增强for循环，必须是实现Iterable的泛型集合
     * @return 放入成功返回true，否则返回false
     */
    public boolean putMines(Iterable<Point> mines) {
        for (Point mine : mines) {
            if (!putMine(mine)) {
                return false;
            }
        }
        return true;
    }


    /***
     * 手写枚举类转义（用于显示）
     *
     * @param character 传入的手写枚举类
     * @return 转义后的手写枚举类
     */
    public static char enumTransformer(char character) {
        char transformered;
        switch (character) {
            case '9':
                transformered = '*';
                break;
            case '0':
                transformered = ' ';
                break;
            default:
                transformered = character;
                break;
        }
        return transformered;
    }

    /***
     * 实际地图的字符串形式，用于调试，若不需要转义，可使用更方便的Arrays.deepToString()
     *
     * @return 实际地图的字符串形式
     */
    public String getMap() {

        StringBuffer stringBuffer = new StringBuffer();
        for (char[] line : this.map) {
            stringBuffer.append("|");
            for (char posValue : line) {
                stringBuffer.append(posValue + "|");
            }
            stringBuffer.append("\n");
            //System.out.println();
        }
        return stringBuffer.toString();
    }

    /***
     * 得到玩家界面地图
     * @return 二维数组形式的玩家界面地图
     */
    public char[][] getMask(){
        return this.mask;
    }
    @Override
    /***
     * 重写toString方法，在命令台情况下，方便显示，若不需要转义，可使用更方便的Arrays.deepToString()
     * @return 玩家地图的字符串形式
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (char[] line : this.mask) {
            stringBuffer.append("|");
            for (char posValue : line) {
                stringBuffer.append(enumTransformer(posValue) + "|");
            }
            stringBuffer.append("\n");
            //System.out.println();
        }
        return stringBuffer.toString();
    }

    /***
     * 用于左右显示玩家所见地图和实际地图
     *
     * @return 用于左右显示玩家所见地图和实际地图的字符串
     */
    public String getMapDebug() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.height; i++) {
            stringBuffer.append("|");
            for (int j = 0; j < this.width; j++) {
                /*这段代码出现两次改成了函数
                switch (mask[i][j]){
                    case '9':stringBuffer.append("*|");;
                        break;
                    case '0':stringBuffer.append(" |");;
                        break;
                    default:stringBuffer.append(mask[i][j] + "|");
                        break;
                }*/
                stringBuffer.append(enumTransformer(mask[i][j]) + "|");
                //stringBuffer.append(posValue + "|");
                //stringBuffer.append(mask[i][j] + "|");
            }
            stringBuffer.append("\t\t|");
            for (int j = 0; j < this.width; j++) {
                stringBuffer.append(map[i][j] + "|");
            }
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }
}
