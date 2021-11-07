package exp2;

import java.util.Scanner;

/**
 * @author LYHstart
 * @create 2021-11-05 16:59
 *
 * 进程管理总控类 -> 控制CtrlBlock进行各项操作
 */
public class CPU
{

    public static void main(String[] args)
    {
        //初始化
        CtrlBlock ctrlBlock = new CtrlBlock();
        ctrlBlock.initMemory();         //初始化内存
        ctrlBlock.initSource();         //初始化资源 11-15
        //扫描器
        Scanner scan = new Scanner(System.in);
        //一些辅助变量
        int pid;
        int priority;
        int rid;
        boolean scheduler = true;
        int memory = 0;
        //进入程序
        while(true)
        {
            printMenu();
            int select = scan.nextInt();
            switch (select)
            {
                case 1:
                    System.out.println("请输入新Process的PID:");
                    pid = scan.nextInt();
                    System.out.println("请输入优先级");
                    priority = scan.nextInt();
                    boolean process = ctrlBlock.createProcess(pid, priority);
                    if(process)
                        System.out.println("进程创建成功!");
                    break;
                case 2:
                    System.out.println("请输入所要申请资源的进程PID:");
                    pid = scan.nextInt();
                    System.out.println("请输入申请的资源RID:");
                    rid = scan.nextInt();
                    boolean b = ctrlBlock.resourceReq(pid, rid);
                    if(b)
                        System.out.println("申请资源成功!");
                    break;
                case 3:
                    //首先必须对Runninglist有一个综合判断
                    if(ctrlBlock.getRunninglist().size() == 0)  //倘若没有资源正在运行，就需要先对进程执行资源调度
                    {
                        scheduler = ctrlBlock.scheduler();
                    }
                    if(scheduler)   //倘若存在进程进入runninglist --> 就再次单步调试
                    {
                        //进入单步调试环节
                        boolean b1 = ctrlBlock.runningCtrl();
                        if(b1)
                        {
                            ctrlBlock.release();        //释放进程所需资源
                            ctrlBlock.scheduler();      //重新执行进程调度程序
                        }
                        break;
                    }
                case 4:
                    //进入执行环节
                    while(true)     //连续运行直到该进程执行结束
                    {
                        boolean b2 = ctrlBlock.runningCtrl();
                        if(b2)
                            break;
                    }
                    ctrlBlock.release();
                    ctrlBlock.scheduler();
                    break;
                case 5:
                    ctrlBlock.printProcess();
                    break;
                case 6:
                    ctrlBlock.printRunningList();
                    break;
                case 7:
                    ctrlBlock.printReadyList();
                    break;
                case 8:
                    ctrlBlock.printBlockedList();
                    break;
                case 9:
                    ctrlBlock.printSuspendList();
                    break;
                case 10:
                    ctrlBlock.printDoneList();
                    break;
                case 11:
                    System.out.println("请输入要创建资源的RID:");
                    rid = scan.nextInt();
                    boolean resource = ctrlBlock.createResource(rid);
                    if(resource)
                        System.out.println("创建资源成功!");
                    break;
                case 12:
                    break;
                case 13:
                    System.out.println("请输入申请内存的进程PID:");
                    pid = scan.nextInt();
                    System.out.println("请输入所要申请的内存大小:");
                    memory = scan.nextInt();
                    boolean b1 = ctrlBlock.memoryReq(pid, memory);
                    if(b1)
                        System.out.println("进程申请内存成功!");
                    break;
                case 14:
                    System.out.println("CPU运行已被结束!");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    //打印菜单
    public static void printMenu()
    {
        System.out.println("*****************************************************");
        System.out.println("*  [1] createProcess         [2] resourceReq        *");
        System.out.println("*  [3] debug                 [4] running            *");
        System.out.println("*  [5] printProcess          [6] printRunninglist   *");
        System.out.println("*  [7] printReadylist        [8] printBlockedlist   *");
        System.out.println("*  [9] printSuspendlist     [10] printDonelist      *");
        System.out.println("* [11] createResource       [12]                    *");
        System.out.println("* [13] MemoryReq            [14] exit_system        *");
        System.out.println("*****************************************************");
        System.out.println("请输入您的选择:");
    }
}
