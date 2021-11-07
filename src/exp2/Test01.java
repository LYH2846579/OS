package exp2;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LYHstart
 * @create 2021-11-04 19:49
 *
 *
 */
public class Test01
{
    @Test
    public void test()
    {
        CtrlBlock ctrlBlock = new CtrlBlock();

        //初始化资源
        ctrlBlock.initSource();
        ctrlBlock.initMemory();

        //测试创建资源
        ctrlBlock.createResource(11);

        //测试空调度
        ctrlBlock.scheduler();

        //测试空运行
        ctrlBlock.runningCtrl();
        //创建一个进程
        ctrlBlock.createProcess(1,1);
        ctrlBlock.resourceReq(1,11);
        ctrlBlock.scheduler();
        boolean b = ctrlBlock.runningCtrl();
        if(b)
            ctrlBlock.scheduler();

        ctrlBlock.createProcess(1,2);
        //ctrlBlock.resourceReq(2,11);
        b = ctrlBlock.runningCtrl();
        if(b)
            ctrlBlock.scheduler();

        ctrlBlock.createProcess(3,1);
        ctrlBlock.resourceReq(3,11);
        b = ctrlBlock.runningCtrl();
        if(b)
        {
            ctrlBlock.release();
            ctrlBlock.scheduler();
        }

        //测试挂起
        ctrlBlock.createProcess(5,5);
        ctrlBlock.memoryReq(5,480);

        ctrlBlock.createProcess(4,4);
        ctrlBlock.resourceReq(4,12);
        b = ctrlBlock.runningCtrl();
        if(b)
        {
            ctrlBlock.release();
            ctrlBlock.scheduler();
        }



        //ctrlBlock.scheduler();
        //ctrlBlock.runningCtrl();

        ctrlBlock.printRunningList();
        ctrlBlock.printReadyList();
        ctrlBlock.printBlockedList();
        ctrlBlock.printDoneList();
        ctrlBlock.printSuspendList();
    }

    //测试AtomicInteger
    @Test
    public void test1()
    {
        AtomicInteger atomicInteger = new AtomicInteger();
        System.out.println(atomicInteger.get());    //->默认值确实为0
    }
}
