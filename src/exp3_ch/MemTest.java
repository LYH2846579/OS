package exp3_ch;

import org.junit.Test;

import java.lang.management.LockInfo;
import java.util.LinkedList;

/**
 * @author LYHstart
 * @create 2021-11-11 22:39
 */
public class MemTest
{
    @Test
    public void test()
    {
        Memory memory = new Memory();

        LinkedList<Process> runninglist = new LinkedList<>();
        LinkedList<Process> blockedlist = new LinkedList<>();
        LinkedList<Process> donelist = new LinkedList<>();


        Process p1 = new Process("p1",3);
        Process p2 = new Process("p2",2);
        Process p3 = new Process("p3",2);
        Process p4 = new Process("p4",6);
        Process p5 = new Process("p4",5);

        runninglist.offerLast(p1);
        runninglist.offerLast(p2);
        runninglist.offerLast(p3);
        runninglist.offerLast(p4);
        runninglist.offerLast(p5);

        //templist
        LinkedList<Process> templist = new LinkedList<>();
        runninglist.forEach(process -> {
                        //(int) (memory.getMemorySize() * Math.random())
            boolean b = memory.requestMem(process,(int) (memory.getMemorySize()* Math.random()));
            if(!b)
            {
                templist.offerLast(process);
            }
        });

        templist.forEach(process -> {
            blockedlist.offerLast(process);
            runninglist.remove(process);
        });

        while(true)
        {
            if(donelist.size() == 5)
                break;

            if(templist.size() != 0)
                templist.clear();
            //针对blockedlist中的进程再次分析
            blockedlist.forEach(process -> {
                boolean b = memory.requestMem(process,(int) (memory.getMemorySize()* Math.random()));
                if(b)
                {
                    //若申请成功，加入templist之中
                    templist.offerLast(process);
                }
            });
            //将templist中的所有元素取出
            templist.forEach(process -> {
                blockedlist.remove(process);
                runninglist.offerLast(process);
            });

            //清空
            if(templist.size() != 0)
                templist.clear();

            //接下来运行 -> 将runninglist中的所有process的times值减一
            runninglist.forEach(process -> {
                process.setTimes(process.getTimes()-1);
                if(process.getTimes() == 0)
                {
                    //释放内存资源
                    memory.releaseMem(process);
                    templist.offerLast(process);
                    //加入donelist
                    donelist.offerLast(process);
                }
            });
            //将运行完毕的进程从runninglist之中取出
            templist.forEach(process -> {
                runninglist.remove(process);
            });

        }

    }
}
