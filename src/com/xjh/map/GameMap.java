package com.xjh.map;

import com.xjh.util.Point;
import com.xjh.util.RandomPointGenerator;

import java.util.*;

/**
 * Created by john on 17/2/28.
 */
public class GameMap {
    //�ֶ�ö�ٱ���
    static final char EMPTY = '0';
    static final char MINE = '9';
    static final char UNKNOWN = 'X';
    static final char BOOM = 'B';

    //��ͼ��ȣ��߶ȣ���������ʵ�ʵ�ͼ����ҿɼ���ͼ
    private int width;
    private int height;
    private int mineCount;
    private int markedMineCount;
    private char[][] map;
    private char[][] mask;

    /***
     * ɨ�׵�ͼ��ʼ��
     *
     * @param width     ��ͼ���
     * @param height    ��ͼ�߶�
     * @param mineCount ������
     */
    public GameMap(int width, int height, int mineCount) {
        //�趨��ͼ��С
        this.width = width;
        this.height = height;
        //��ʼ��ʵ�ʵ�ͼ����ҿɼ���ͼ
        this.map = new char[height][width];
        this.mask = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = EMPTY;
                mask[i][j] = UNKNOWN;
            }
        }
        //�����׷����ͼ
        this.mineCount = mineCount;
        //����mineCount�����ظ���������Ϊ���׵�
        Set<Point> mines = RandomPointGenerator.getRandomPoints(width, height, mineCount);
        putMines(mines);
        this.markedMineCount=0;
    }

    /***
     * �����������������mask��ֵΪ�����֣��տ�����չ��������ʧ��
     *
     * @param x ��������ĺ�����
     * @param y ���������������
     * @return �Ƿ�ʧ�ܣ����׷���false
     * @throws RuntimeException ���������׳��쳣
     */
    public boolean leftClick(int x, int y)
            throws RuntimeException {
        if (!isInMap(x, y)) {
            throw new RuntimeException("�������겻�ڵ�ͼ��Χ��");
        }
        char value = this.map[y][x];

        if (mask[y][x] == MINE) {
            return true;
        }
        //���ף�ֱ��ʧ��
        if (value == MINE) {
            return false;
        }
        //�õ㲻���ף�����������
        else if (value > EMPTY) {
            this.mask[y][x] = value;
            return true;
        }
        //���Ϊ�հ׵㣨��Χ���ף������չ����ΧȫΪ����
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
                    // �����Χ��������EMPTY���򽫴�λ�õ���Χ�������չ�б�
                    aroundField.addAll(this.getAround(nextX, nextY));
                }
            }
        }
        return true;


    }

    /***
     * �����������������mask��ֵΪ�����֣��տ�����չ��������ʧ��
     *
     * @param point �������������
     * @return �Ƿ�ʧ�ܣ����׷���false
     * @throws RuntimeException ���������׳��쳣
     */
    public boolean leftClick(Point point)
            throws RuntimeException {
        return leftClick(point.getX(), point.getY());
    }

    /***
     * �Ҽ����������orȡ���������,
     * @param x �Ҽ������ĺ�����
     * @param y �Ҽ�������������
     * @return �Ƿ�ʧ��
     * @throws RuntimeException ���������׳��쳣
     */
    public boolean rightClick(int x, int y)
            throws RuntimeException {
        if (!isInMap(x, y)) {
            throw new RuntimeException("�������겻�ڵ�ͼ��Χ��");
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
     * �Ҽ����������orȡ���������,
     * @param point
     * @return �Ҽ�����������
     * @throws RuntimeException ���������׳��쳣
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
     * ��õ�ͼ���ܵ�����
     *
     * @return ��ͼ���ܵ�����
     */
    public int getMineCount() {
        return this.mineCount;
    }

    /***
     * ȡ�ø��������Χ�Ĳ�������ͼ�߽�������ļ���
     *
     * @param x �õ�ĺ�����
     * @param y �õ��������
     * @return ���������Χ�Ĳ�������ͼ�߽�������ļ���
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
     * ���Ƿ񳬳���ͼ
     *
     * @param x ��ĺ�����
     * @param y ���������
     * @return δ������ͼ����true�����򷵻�false
     */
    public boolean isInMap(int x, int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.height;
    }

    /***
     * ���Ƿ񳬳���ͼ
     *
     * @param point Ҫ���ĵ�
     * @return δ������ͼ����true�����򷵻�false
     */
    public boolean isInMap(Point point) {
        return this.isInMap(point.getX(), point.getY());
    }

    /***
     * ����Ƿ�ʤ��
     *
     * @return ʤ������true������false
     */
    public boolean isWin() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                //ֻҪ��һ��λ�ò����ף���û����������û��ʤ
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
     * ����һ�ŵ���
     *
     * @param pos �����
     * @return ����ɹ�����true�����򷵻�false
     */
    public boolean putMine(Point pos) {

        return this.putMine(pos.getX(), pos.getY());
    }

    /***
     * ����һ�ŵ���
     *
     * @param x ������׵ĺ�����
     * @param y ������׵�������
     * @return ����ɹ�����true�����򷵻�false
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
     * �����ŵ��ף�����һ�ŷ���ʧ��ʱ���ж�ѭ��������false
     *
     * @param mines ʹ����ǿforѭ����������ʵ��Iterable�ķ��ͼ���
     * @return ����ɹ�����true�����򷵻�false
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
     * ��дö����ת�壨������ʾ��
     *
     * @param character �������дö����
     * @return ת������дö����
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
     * ʵ�ʵ�ͼ���ַ�����ʽ�����ڵ��ԣ�������Ҫת�壬��ʹ�ø������Arrays.deepToString()
     *
     * @return ʵ�ʵ�ͼ���ַ�����ʽ
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
     * �õ���ҽ����ͼ
     * @return ��ά������ʽ����ҽ����ͼ
     */
    public char[][] getMask(){
        return this.mask;
    }
    @Override
    /***
     * ��дtoString������������̨����£�������ʾ��������Ҫת�壬��ʹ�ø������Arrays.deepToString()
     * @return ��ҵ�ͼ���ַ�����ʽ
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
     * ����������ʾ���������ͼ��ʵ�ʵ�ͼ
     *
     * @return ����������ʾ���������ͼ��ʵ�ʵ�ͼ���ַ���
     */
    public String getMapDebug() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.height; i++) {
            stringBuffer.append("|");
            for (int j = 0; j < this.width; j++) {
                /*��δ���������θĳ��˺���
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
