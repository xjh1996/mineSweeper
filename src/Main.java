import com.xjh.player.Player;
import com.xjh.player.PlayerCommand;
import com.xjh.map.GameMap;
import com.xjh.util.Point;

import java.util.TreeMap;

public class Main {
    public static boolean play(GameMap map) {
        //Scanner scanner = new Scanner(System. in);
        boolean lose = false;
        Player player=new Player();
        do {
            //String command = scanner.nextLine();
            //if(command.equals("exit")){
                //break;
            //}
            PlayerCommand playerCommand=player.next(map);
            for(Point p:playerCommand.getPoints()){
                switch (playerCommand.getCommandType()){
                    case left:
                        //System.out.println("左键单击坐标"+ p);
                        if(!map.leftClick(p)){
                            lose=true;
                            //System.out.println("you lose");
                            //scanner.close();
                            return false;
                        }
                        break;
                    case rigft:
                        //System.out.println("右键单击坐标"+ p);
                        map.rightClick(p);
                        break;
                    default:break;
                }
            }
            //System.out.println(point);
            //System.out.println(map);
            if(map.isWin()){
                //System.out.println("you win!!");
                break;
            }
        } while (! lose);
        //scanner.close();
        return true;
    }

    public static void main(String[] args) {
        /*
        long before=System.nanoTime();
        Set<Point> posSet= RandomPointGenerator.getMines(30, 16, 100);
        long after=System.nanoTime();
        System.out.print((after-before)/1000000.0);
        */
        //Scanner scanner=new Scanner(System.in);

        int count=60000;
        int winCount=0;
        long before=System.nanoTime();
        for(int i=0;i<count;i++) {
            GameMap map = new GameMap(30, 16, 99);
            //System.out.println(map.getMap().length);
            //System.out.println(map);
            if(play(map)){
                winCount++;
            }
        }
        long after=System.nanoTime();
        double useTime=(after-before)/1000000000.0;
        System.out.println("总场数"+count);
        System.out.println("胜利场数"+winCount);
        System.out.println("本次胜率"+(double)winCount/count*100+"％");
        System.out.println("耗时："+useTime+"s");

        //System.out.println(map.getMap());
        // System.out.println("Hello World!");
    }
}
