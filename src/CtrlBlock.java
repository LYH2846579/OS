import sun.dc.pr.PRError;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;

/**
 * @author LYHstart
 * @create 2021-11-04 19:42
 *
 * 进程控制单元
 *
 * 用于资源生成、进程控制、进程调度、内存分配等
 *
 * readylist必须采用数组+链表的形式进行实现 -> 这里使用使用ArrayList+LinkedList进行实现
 */
public class CtrlBlock
{
    private int memorySize;
    private ArrayList<LinkedList<Process>> readylist;                //存储处于就绪状态进程的队列 -> 用于分配资源
    private LinkedList<Process> runninglist;      //存储处于运行状态进程的队列
    private LinkedList<Process> blockedlist;      //存储处于阻塞状态进程的队列
    private LinkedList<Process> suspendlist;      //存储处于挂起状态进程的队列
    private LinkedList<Resource> busylist;        //处于被占用状态的资源
    private LinkedList<Resource> freelist;        //处于空闲状态的资源

    //为了实现Process和PID之间的灵活转换，这里创建一个进程存储列表
    private IdentityHashMap<Integer,Process> processlist;

    public CtrlBlock() {
        this.memorySize = 1000;
        this.readylist = new ArrayList<>();
        this.runninglist = new LinkedList<>();
        this.blockedlist = new LinkedList<>();
        this.suspendlist = new LinkedList<>();
        this.busylist = new LinkedList<>();
        this.freelist = new LinkedList<>();
        this.processlist = new IdentityHashMap<>();
        //针对readylist进行初始化
        for (int i = 0; i < 10; i++)
        {
            readylist.add(new LinkedList<>());
        }
    }
    public CtrlBlock(int memorySize) {
        this.memorySize = memorySize;
        this.readylist = new ArrayList<>();
        this.runninglist = new LinkedList<>();
        this.blockedlist = new LinkedList<>();
        this.suspendlist = new LinkedList<>();
        this.processlist = new IdentityHashMap<>();
        //针对readylist进行初始化
        for (int i = 0; i < 10; i++)
        {
            readylist.add(new LinkedList<>());
        }
    }
    public CtrlBlock(int memorySize, ArrayList<LinkedList<Process>> readylist, LinkedList<Process> runninglist, LinkedList<Process> blockedlist, LinkedList<Process> suspendlist, LinkedList<Resource> busylist, LinkedList<Resource> freelist, IdentityHashMap<Integer, Process> processlist) {
        this.memorySize = memorySize;
        this.readylist = readylist;
        this.runninglist = runninglist;
        this.blockedlist = blockedlist;
        this.suspendlist = suspendlist;
        this.busylist = busylist;
        this.freelist = freelist;
        this.processlist = processlist;
        //针对readylist进行初始化
        for (int i = 0; i < 10; i++)
        {
            readylist.add(new LinkedList<>());
        }
    }

    public int getMemorySize() {
        return memorySize;
    }
    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }
    public ArrayList<LinkedList<Process>> getReadylist() {
        return readylist;
    }
    public void setReadylist(ArrayList<LinkedList<Process>> readylist) {
        this.readylist = readylist;
    }
    public LinkedList<Process> getRunninglist() {
        return runninglist;
    }
    public void setRunninglist(LinkedList<Process> runninglist) {
        this.runninglist = runninglist;
    }
    public LinkedList<Process> getBlockedlist() {
        return blockedlist;
    }
    public void setBlockedlist(LinkedList<Process> blockedlist) {
        this.blockedlist = blockedlist;
    }
    public LinkedList<Process> getSuspendlist() {
        return suspendlist;
    }
    public void setSuspendlist(LinkedList<Process> suspendlist) {
        this.suspendlist = suspendlist;
    }
    public LinkedList<Resource> getBusylist() {
        return busylist;
    }
    public void setBusylist(LinkedList<Resource> busylist) {
        this.busylist = busylist;
    }
    public LinkedList<Resource> getFreelist() {
        return freelist;
    }
    public void setFreelist(LinkedList<Resource> freelist) {
        this.freelist = freelist;
    }
    public IdentityHashMap<Integer, Process> getProcesslist() {
        return processlist;
    }
    public void setProcesslist(IdentityHashMap<Integer, Process> processlist) {
        this.processlist = processlist;
    }

    //一下需要实现内存分配，进程调度
    public boolean MemoryMelloc(Process process,int reqSize)
    {
        //倘若剩余的内存仍然充足
        if(memorySize >= reqSize)
        {
            //分配内存
            memorySize -= reqSize;
            process.getPcb().getMemory().offerLast(reqSize);
            return true;
        }
        else
        {
            //System.out.println("内存不足,分配失败");
            //此时可以直接将该程序挂起
            process.getPcb().setState("suspend");
            //从原序列中将进程移出
            LinkedList<Process> statelist = process.getPcb().getStatelist();
            statelist.remove(new Integer(process.getPcb().getID()));
            //修改序列指针
            process.getPcb().setStatelist(this.suspendlist);
            //将该进程加入新的序列之中
            this.suspendlist.offerLast(process);
            return false;
        }
    }

    //创建进程
    public boolean createProcess(int id,int priority)
    {
        PCB pcb = new PCB(id,priority);
        Process process = new Process(pcb);
        //默认分配内存5
        boolean b = this.MemoryMelloc(process, 5);
        //此时其默认状态即为"Ready"
        if(b)
        {
            //将其加入就绪队列
            process.getPcb().setStatelist(this.readylist.get(priority));
            //将创建的进程加入就绪队列 -> 根据优先级的不同进行添加
            LinkedList<Process> processes = readylist.get(priority);
            processes.offerLast(process);
            //每创建一个进程之后，都会将其加入Processlist中便于查询
            processlist.put(id,process);
            return true;
        }
        else
            return false;

    }

    //申请资源
    public boolean resourceReq(Integer Pid,int Rid)
    {
        //这里出现了一个很严重的问题，在createProcess的时候并不会返回Process对象
        //而这里请求资源又需要Process对象作为参数去申请 -> 处理方案为传入一个process对象对应的PID
        //在链表中寻找到该PID对应的Process
        Process process = this.processlist.get(Pid);

        //必须判断是否存在该资源id
        int flag = 0;
        for(Resource resource:freelist)
        {
            if(resource.getRcb().getRID() == Rid)
            {
                flag = 1;
                break;
            }
        }
        if(flag == 0)
        {
            for(Resource resource:busylist)
            {
                if(resource.getRcb().getRID() == Rid)
                {
                    flag = 2;
                    break;
                }
            }
        }

        if(flag == 0)
        {
            System.out.println("请求的资源不存在!");
            return false;
        }
        else
        {
            //首先将申请的资源id加入未申请到的资源列表中
            process.addSourceId(Rid);
            //接下来等待总控单元的分配

            //此时对应的资源RCB中的申请列表中也应该获取到该进程
            //只有阻塞的时候才会加入

            //当程序运行的过程之中，倘若调用申请资源命令，则会中断程序的运行 -> 此时Running队列中无元素
            //=> 加入申请资源处理方案之中

            //申请状态判断标志位
            int tag = 0;

            //判断是否处于Running
            if(process.getPcb().getState().equals("Running"))
            {
                //将处于Running队列中的进程取出，此时进程处于悬浮态 -> 必须保证运行该程序的时候将其加入Runninglist中
                for(Process process1:runninglist)
                {
                    if(process1.getPcb().getID() == process.getPcb().getID())
                    {
                        //从Runninglist除去该进程
                        runninglist.remove(process1);
                        break;
                    }
                }
                tag = 1;
            }

            //倘若处于Readylist中，属于静态申请
            if(process.getPcb().getState().equals("Ready"))
            {
                //将其从Readlist中去除
                this.readylist.get(process.getPcb().getPriority()).remove(process);
            }


            //调用资源申请处理方案
            boolean b = this.reqDeal(process);

            if(tag == 1 && b)
            {
                //调用资源分配方法  ->boolean scheduler = scheduler();   在这个地方调用吗?
                //if(process.getPcb().getState().equals("Running"))
                //{
                //    this.scheduler();
                //    process.getPcb().setState("Ready");
                //}
                //经过reqDeal之后 -> process的属性已经发生了改变
                //但是又需要判断是静态申请资源还是动态申请资源 -> 不妨提前设置一个标志位来作为判断
                //若为动态申请 -> 需要启动资源调度
                this.scheduler();
                return b;                           //加入申请成功
            }
            else if(tag == 0 && b)
            {
                //若为静态申请 -> 无需开启调度
                return b;
            }
            else if(!b)
            {
                System.out.println("process"+process.getPcb().getID()+"申请的资源已被占用!");
                return false;
            }
            else    //不可达状态
                return false;



        }
    }

    //申请资源处理方案  -> 处理在进程运行的过程之中，调用申请资源方法所进入的状态
    public boolean reqDeal(Process process)
    {
        //首先必须扫描进程所需要的资源
        LinkedList<Integer> resourcelistR = process.getPcb().getResourcelistR();

        //判断其申请的资源是否都处于free状态的标识位
        int flag = -1;
        //开始扫描
        for(Integer integer:resourcelistR)        //一般动态申请只会有一个申请资源的请求
        {
            //初始化标识位
            flag  = -1;
            //标识
            Resource resourceTag = null;
            //扫描资源是否处于free状态
            for(Resource resource:freelist)
            {
                resourceTag = null;
                if(resource.getRcb().getRID() == integer)
                {
                    //倘若处于free状态 -> 将该资源加入该进程所拥有的资源列表之中,并将其状态置为busy->并加入busylist中
                    //将该进程置于Ready队列之中 -> 按照优先级进行划分
                    //将申请的资源从待申请的资源列表中删除
                    process.getPcb().getResourcelistH().offerLast(resource.getRcb().getRID());
                    resource.getRcb().setFree(false);
                    //资源列表修改 -> 在进行迭代的时候不可以对其进行修改
                    //-> 使用标识指针指向
                    resourceTag = resource;
                    //this.freelist.remove(resource);
                    //this.busylist.offerLast(resource);
                    process.getPcb().getResourcelistR().remove(integer);


                    //倘若使用静态分配方式的时候不可以使用如下方案!
                    //ArrayList<LinkedList<Process>> readylist = this.readylist;
                    //LinkedList<Process> processes = readylist.get(process.getPcb().getPriority());
                    //processes.offerLast(process);
                    flag = 0;
                    //return true;
                    break;
                }
            }


            //倘若扫描完成之后未在freelist中找到所需资源
            //将该进程加入对应资源的阻塞队列之中 -> 从busylist中进行查询
            for(Resource resource:busylist)
            {
                if(resource.getRcb().getRID() == integer)
                {
                    resource.getRcb().getBlockedlist().offerLast(process.getPcb().getID());
                    //在控制单元的阻塞队列中添加
                    this.blockedlist.offerLast(process);
                    process.getPcb().setState("Blocked");
                    flag = 1;
                    //return false;
                }
            }

            //判断是否加载了free资源
            if(resourceTag != null)
            {
                this.freelist.remove(resourceTag);
                this.busylist.offerLast(resourceTag);
            }

            //标志位校验
            //if(flag == 1)
            //    break;

            if(flag == -1)
            {
                System.out.println("所申请的资源不存在!");
                return false;
            }

        }
        if(flag == 1)
            return false;
        else
        {
            //此时将其所需要的资源均申请到 -> 将其加入Readylist中
            process.getPcb().setState("Ready");
            this.readylist.get(process.getPcb().getPriority()).offerLast(process);

            return true;            //将所有的资源全部申请完毕
        }

    }

    //实现资源分配 -> 进入调度队列?
    public boolean scheduler()
    {
        /*
        实现调度

        倘若在进程执行的过程之中出现了资源申请的问题，可以在完成资源分配处理方案之后直接调用资源分配调度
        若为创建进程的时候申请资源，可以不立刻进入资源分配处理方案
         */
        //从Readylist中选择自底向上扫描，选择一个进程放入Runninglist中进行执行
        ArrayList<LinkedList<Process>> readylist = this.readylist;
        LinkedList<Process> processes = null;
        for (int i = 9; i >= 0 ; i--)
        {
            processes = readylist.get(i);
            if(processes.size() == 0)
                continue;
            else
            {
                //倘若不为零，则从最前部取出一个进程加入Runninglist中进行运行
                Process process = processes.pollFirst();
                this.runninglist.offerLast(process);
                process.getPcb().setState("Running");
                break;
            }
        }

        if(this.runninglist.size() == 0)
        {
            System.out.println("当前无进程正在执行");
        }

        return true;
    }

    //进程运行控制模块  -> 实现对处于Runninglist中进程的控制   -> 进入debug的时候修改process的值,并作出判断
    public boolean runningCtrl()
    {
        //这里先作为单核CPU -> runninglist中只有一个进程
        LinkedList<Process> runninglist = this.runninglist;
        Process process = runninglist.get(0);
        //运行时间-1,并判断是否运行完毕
        process.getPcb().setTimes(process.getPcb().getTimes()-1);
        int times = process.getPcb().getTimes();
        if(times == 0)
            return true;
        else
            return false;
    }


    //进程运行完毕控制单元
    public boolean release()
    {
        LinkedList<Process> runninglist = this.runninglist;
        Process process = runninglist.get(0);

        //将其从running队列中取出
        this.runninglist.remove(0);

        //在进程执行完毕之后，需要分析是否有因为该进程阻塞的进程，这样一来可以动态对其控制
        //-> 倘若仅仅由这个资源所阻塞，则获取到这个资源之后就可以被加入Readylist中
        //-> 倘若还被其他资源所阻塞，则从这个资源的阻塞进程列表中删除
        //将该进程从对应的Processlist中删除
        this.processlist.remove(process);

        //释放对应的资源
        LinkedList<Integer> resourcelistH = process.getPcb().getResourcelistH();
        for(Integer integer:resourcelistH)
        {
            //在busylist中删除对应的资源
            for(Resource resource1:busylist)
            {
                if(resource1.getRcb().getRID() == integer)
                {
                    //在这里加一个判断 -> 分析由该进程阻塞的进程是否可以进入Readylist
                    //将这个资源分配给等待队列中第一个的进程
                    LinkedList<Integer> blockedlist = resource1.getRcb().getBlockedlist();
                    if(blockedlist.size() == 0)
                    {
                        //没有因为该资源阻塞的进程 -> 释放资源
                        resource1.getRcb().setFree(true);
                        this.freelist.offerLast(resource1);
                        busylist.remove(resource1);
                        //退出循环
                        break;
                    }
                    //倘若有被阻塞的进程
                    Integer integer1 = blockedlist.get(0);

                    //获取该被阻塞的进程 -> 当然也可以从当前被阻塞的资源队列中获取
                    Process process1 = this.processlist.get(integer1);

                    //?
                    LinkedList<Process> blockedlist1 = this.blockedlist;

                    //同样设置标志位来判断是否可以将其加入Readylist之中
                    int flag = 0;

                    //for(Process process1:blockedlist1)
                    {
                        if(process1.getPcb().getID() == integer1)           //->一定会成功
                        {
                            //获取到该进程之后 -> 将该资源加入其拥有的资源列表
                            process1.getPcb().getResourcelistR().remove(new Integer(resource1.getRcb().getRID()));
                            process.getPcb().getResourcelistH().offerLast(resource1.getRcb().getRID());
                            //判断是否可以进入Readylist
                            if(process1.getPcb().getResourcelistR().size() == 0)
                            {
                                //所需的资源序列为0 -> 加入Readylist之中
                                this.readylist.get(process1.getPcb().getPriority()).offerLast(process1);
                                //将标志位置1
                                flag =1;
                                //此时需要采取如下措施
                                //1、将该资源序列的阻塞列表中的该进程删除
                                //2、将该进程从原来的阻塞队列中删除并将其放置在Readylist之中
                                //3、将该进程的状态修改为Ready
                            }
                            //将标志位置2
                            //此时需要采取如下措施
                            //1、将该资源序列的阻塞列表中的该进程删除
                            //2、仍然保持该序列在阻塞列表中
                            flag = 2;

                            //倘若不为零 -> 放Blockedlist中呆着ba
                        }
                    }

                    //根据标志位作出处理
                    if(flag == 1)
                    {
                        //integer2即为被阻塞队列的首个进程
                        Integer integer2 = resource1.getRcb().getBlockedlist().pollFirst();     //去除首个进程
                        process1.getPcb().setState("Ready");
                        this.blockedlist.remove(process1);
                        this.readylist.get(process1.getPcb().getPriority()).offerLast(process);
                    }
                    else if(flag == 2)
                    {
                        Integer integer2 = resource1.getRcb().getBlockedlist().pollFirst();
                    }
                    //break;
                }
            }
        }

        //释放内存
        LinkedList<Integer> memory = process.getPcb().getMemory();
        int sum = 0;
        for(Integer integer:memory)
        {
            sum += integer;
        }

        //修改内存
        this.memorySize += sum;

        return true;
    }



    //初始化资源
    public void initSource()
    {
        //创建一系列处于free状态的资源并将其加入freelist链表之中
        for (int i = 10; i < 15; i++)
        {
            freelist.offerLast(new Resource(new RCB(i+1)));
        }
    }

    //一些debug时候用的方法
    //显示当前阻塞队列
    public void printBlockedList()
    {
        System.out.println("BlockedList:");
        this.blockedlist.forEach(process -> {
            System.out.println(process);
        });
    }

    //打印运行队列
    public void printRunningList()
    {
        System.out.println("RunningList:");
        this.runninglist.forEach(process -> {
            System.out.println(process);
        });
    }

    //打印就绪队列
    public void printReadyList()
    {
        /*System.out.println("ReadyList");
        this.readylist.forEach(processLinkedList ->{
            processLinkedList.forEach(process -> {
                System.out.println(process);
            });
    });*/
        System.out.println("ReadyList:");
        ArrayList<LinkedList<Process>> readylist = this.readylist;
        for (int i = 9; i >= 0 ; i--)
        {
            readylist.get(i).forEach(process -> {
                System.out.println(process);
            });
        }
    }



}
