import org.junit.Test;

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

        //创建一个进程
        ctrlBlock.createProcess(1,1);
        ctrlBlock.resourceReq(1,11);
        ctrlBlock.scheduler();
        boolean b = ctrlBlock.runningCtrl();
        if(b)
            ctrlBlock.scheduler();

        ctrlBlock.createProcess(2,2);
        ctrlBlock.resourceReq(2,11);
        b = ctrlBlock.runningCtrl();
        if(b)
            ctrlBlock.scheduler();

        ctrlBlock.createProcess(3,1);
        ctrlBlock.resourceReq(3,12);


        //ctrlBlock.scheduler();
        //ctrlBlock.runningCtrl();

        ctrlBlock.printRunningList();
        ctrlBlock.printReadyList();
        ctrlBlock.printBlockedList();
    }
}
