package exp2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    //为CPU分配最小存储空间
    private static final int MinSpace = 512;

    //存储已经运行完毕的进程
    private LinkedList<Process> donelist;

    //为了实现Process和PID之间的灵活转换，这里创建一个进程存储列表
    private IdentityHashMap<Integer,Process> processlist;
    //为了校验是否有重复的资源存在，这里创建一个资源存储列表
    private IdentityHashMap<Integer,Resource> resourcelist;

    //针对于挂起，创建一个存储程序内存空间的链表
    private IdentityHashMap<Integer,Integer> memorylist;

    public CtrlBlock() {
        this.memorySize = 1000;
        this.readylist = new ArrayList<>();
        this.runninglist = new LinkedList<>();
        this.blockedlist = new LinkedList<>();
        this.suspendlist = new LinkedList<>();
        this.busylist = new LinkedList<>();
        this.freelist = new LinkedList<>();
        this.processlist = new IdentityHashMap<>();
        this.donelist = new LinkedList<>();
        this.resourcelist = new IdentityHashMap<>();
        this.memorylist = new IdentityHashMap<>();
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
        this.resourcelist = new IdentityHashMap<>();
        this.memorylist = new IdentityHashMap<>();
        this.donelist = new LinkedList<>();
        //针对readylist进行初始化
        for (int i = 0; i < 10; i++)
        {
            readylist.add(new LinkedList<>());
        }
    }
    public CtrlBlock(int memorySize, ArrayList<LinkedList<Process>> readylist, LinkedList<Process> runninglist, LinkedList<Process> blockedlist, LinkedList<Process> suspendlist, LinkedList<Resource> busylist, LinkedList<Resource> freelist, LinkedList<Process> donelist, IdentityHashMap<Integer, Process> processlist, IdentityHashMap<Integer, Resource> resourcelist, IdentityHashMap<Integer, Integer> memorylist) {
        this.memorySize = memorySize;
        this.readylist = readylist;
        this.runninglist = runninglist;
        this.blockedlist = blockedlist;
        this.suspendlist = suspendlist;
        this.busylist = busylist;
        this.freelist = freelist;
        this.donelist = donelist;
        this.processlist = processlist;
        this.resourcelist = resourcelist;
        this.memorylist = memorylist;
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
    public LinkedList<Process> getDonelist() {
        return donelist;
    }
    public void setDonelist(LinkedList<Process> donelist) {
        this.donelist = donelist;
    }
    public IdentityHashMap<Integer, Resource> getResourcelist() {
        return resourcelist;
    }
    public void setResourcelist(IdentityHashMap<Integer, Resource> resourcelist) {
        this.resourcelist = resourcelist;
    }
    public IdentityHashMap<Integer, Integer> getMemorylist() {
        return memorylist;
    }
    public void setMemorylist(IdentityHashMap<Integer, Integer> memorylist) {
        this.memorylist = memorylist;
    }

    //初始化内存资源
    public void initMemory()
    {
        this.memorySize -= MinSpace;
    }

    //一下需要实现内存分配，进程调度
    public boolean MemoryMelloc(Process process,int reqSize)
    {
        //首先判断CPU工作内存是否充足
        //int size = memorySize-reqSize;

        //倘若剩余的内存仍然充足 -> 剩余的内存充足且大于CPU基本工作内存
        if(memorySize >= reqSize /*&& size >= MinSpace*/)
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
            process.getPcb().setState("Suspend");       //-> 释放其内存
            //从原序列中将进程移出
            LinkedList<Process> statelist = process.getPcb().getStatelist();
            statelist.remove(process);
            //需要针对Readylist单独处理
            //if(statelist.equals(this.))
            //修改序列指针
            process.getPcb().setStatelist(this.suspendlist);
            //将该进程加入阻塞队列之中
            this.suspendlist.offerLast(process);

            //为了尽可能使得资源得到有效利用且尽可能避免死锁的情况发生，要将将要被挂起的进程的所拥有的所有资源进行释放!
            //1.首先将其拥有的资源记录下来 ->　释放与分配等下几步处理   --> 考虑唤醒的情况
            for(Integer integer:process.getPcb().getResourcelistH())
            {
                process.getPcb().getResourcelistR().offerFirst(integer);
                //将资源对应的状态及队列进行修改
                Resource resource = this.resourcelist.get(integer);
                resource.getRcb().setFree(true);
                //修改busylist和freelist
                this.busylist.remove(resource);
                this.freelist.offerLast(resource);
                //等待进一步的资源分配 -> 首先实现一个分配方法
            }
            //全部清空
            process.getPcb().getResourcelistH().remove();
            //分配闲置的资源
            this.redOfRes();

            //释放内存资源 -> 并将其加入序列
            LinkedList<Integer> memory = process.getPcb().getMemory();
            int sum = 0;
            for(Integer integer:memory)
                sum += integer;
            //加入memorylist
            this.memorylist.put(process.getPcb().getID(),sum);
            //清空memory
            process.getPcb().getMemory().remove();
            return false;
        }
    }

    //资源分配方法 ->用于处理挂起与死锁的时候的资源重新分配问题
    public void redOfRes()        //redistribution of resources
    {
        //遍历freelist,分析每一个资源是否有被其阻塞的进程，倘若有，则将其分配给该进程 -> 注意，该进程的状态必须是Blocked
        //并判断该进程的状态是否会发生改变
        //经过思路演变，被阻塞的进程必然是处于Blockedlist中的进程，不可能处于挂起状态!
        //增加一个临时的辅助链表
        LinkedList<Resource> delList = new LinkedList<>();
        for(Resource resource:freelist)
        {
            if(resource.getRcb().getBlockedlist().size() != 0)
            {
                //获取该进程
                Process process = this.processlist.get(resource.getRcb().getBlockedlist().get(0));
                //
                //for循环在执行的过程之中操作的对象不允许被修改!         --> 有无好的解决方案?
                //将该资源RID加入辅助链表中 -> 等待被加入busylist
                delList.offerLast(resource);
                //修改process的资源列表
                process.getPcb().getResourcelistR().remove(new Integer(resource.getRcb().getRID()));
                process.getPcb().getResourcelistH().offerLast(new Integer(resource.getRcb().getRID()));

                //判断该资源是否可以加入Readylist中
                if(process.getPcb().getResourcelistR().size() == 0)
                {
                    //从Blockedlist中移出
                    this.blockedlist.remove(process);
                    //倘若待申请的资源数目为0 -> 加入Readylist
                    this.readylist.get(process.getPcb().getPriority()).offerLast(process);
                    //修改Process属性
                    process.getPcb().setState("Ready");
                    process.getPcb().setStatelist(this.readylist.get(process.getPcb().getPriority()));
                }
            }
        }
        //接下来将辅助链表中的资源属性进行处理
        for(Resource resource:delList)
        {
            this.freelist.remove(resource);
            resource.getRcb().setFree(false);
            this.busylist.offerLast(resource);
        }
    }

    //创建进程
    public boolean createProcess(int id,int priority)
    {
        //必须校验是否已经存在拥有该PID的进程
        Process process1 = this.processlist.get(id);
        if(process1 != null)
        {
            System.out.println("申请创建的进程已存在!");
            return false;
        }

        //当该PID不存在时创建新的进程!
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
            //修改进程statelist
            process.getPcb().setStatelist(this.readylist.get(priority));
            return true;
        }
        else
        {
            //内存都没申请到，还运行啥 -> 不允许创建!    -> 挂起的机会都没有
            System.out.println("内存空间不足!不允许创建进程!!!");
            return false;
        }

    }

    //修改运行时间
    public void setTimes(int pid,int times)
    {
        //获取进程
        Process process = this.processlist.get(pid);
        //设置进程运行时间
        process.getPcb().setTimes(times);
    }

    //申请内存
    public boolean memoryReq(int pid,int memorySize)
    {
        //获取Process
        Process process = this.processlist.get(pid);

        //加一个进程状态判断         -> 解决运行完毕仍申请资源的问题
        if(process.getPcb().getState().equals("Done"))
        {
            System.out.println("该进程已经运行完毕!无法申请内存");
            return false;
        }

        //申请内存
        boolean b = this.MemoryMelloc(process, memorySize);
        return b;
    }

    //申请资源
    public boolean resourceReq(Integer Pid,int Rid)
    {
        //这里出现了一个很严重的问题，在createProcess的时候并不会返回Process对象
        //而这里请求资源又需要Process对象作为参数去申请 -> 处理方案为传入一个process对象对应的PID
        //在链表中寻找到该PID对应的Process
        Process process = this.processlist.get(Pid);

        //加一个防止空指针异常的判断 -> 必须在进程状态判断之前!
        if(process == null)
        {
            System.out.println("指定的进程不存在!");
            return false;
        }

        //加一个进程状态判断
        if(process.getPcb().getState().equals("Done"))
        {
            System.out.println("该进程已经运行完毕!无法申请资源");
            return false;
        }



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
            //注意注意 -> 必须保证资源不会被重复申请!!!!
            LinkedList<Integer> resourcelistH = process.getPcb().getResourcelistH();
            for(Integer integer:resourcelistH)
            {
                if(integer == Rid)
                {
                    System.out.println("该资源已被该进程拥有!");
                    return false;
                }
            }


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
        for(Integer integer:resourcelistR)        //一般动态申请只会有一个申请资源的请求 -> 这可不一定!
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
                    //设置statelist
                    process.getPcb().setStatelist(this.blockedlist);
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


            //就让他执行一次!
            break;

        }
        if(flag == 1)
            return false;
        else
        {
            //此时将其所需要的资源均申请到 -> 将其加入Readylist中
            process.getPcb().setState("Ready");
            this.readylist.get(process.getPcb().getPriority()).offerLast(process);
            //设置statelist
            process.getPcb().setStatelist(this.readylist.get(process.getPcb().getPriority()));

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
                //设置statelist
                process.getPcb().setStatelist(this.runninglist);
                break;
            }
        }

        if(this.runninglist.size() == 0)
        {
            System.out.println("当前无进程正在执行");
            return false;
        }

        return true;
    }

    //进程运行控制模块  -> 实现对处于Runninglist中进程的控制   -> 进入debug的时候修改process的值,并作出判断
    public boolean runningCtrl()
    {
        //针对于runningCtrl -> 首先判断runninglist是否为空
        if(this.runninglist.size() == 0)
        {
            System.out.println("当前无进程执行!");
            return false;
        }

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

    //死锁校验模块    //当当前无进程正在执行的时候，但是Blockedlist中存在进程的时候进行调度
    public boolean dealDeadLock()
    {
        //首先进行死锁判断 -> 在该方法被调用之前，一定是scheduler方法被调用之后返回值为false
        //接下来对Blockedlist进行检查
        if(this.blockedlist.size() == 0)
        {
            //此时是真的被执行完毕了
            return true;
        }
        else
        {
            //此时为存在被阻塞的进程，选取其中优先级最低的第一个进程并释放


            return false;
        }

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
        //this.processlist.remove(process);
        //this.processlist.remove(process.getPcb().getID(),process);            //->这里最好还是保留着
        //修改状态
        process.getPcb().setState("Done");
        //将该进程加入已经执行完毕的列表之中
        this.donelist.offerLast(process);

        //释放对应的资源
        LinkedList<Integer> resourcelistH = process.getPcb().getResourcelistH();
        for(Integer integer:resourcelistH)          //ConcurrentModificationException
        {
            Resource resourceTag = null;
            //在busylist中删除对应的资源
            for(Resource resource1:busylist)
            {
                resourceTag = null;
                if(resource1.getRcb().getRID() == integer)
                {
                    //在这里加一个判断 -> 分析由该进程阻塞的进程是否可以进入Readylist
                    //将这个资源分配给等待队列中第一个的进程
                    LinkedList<Integer> blockedlist = resource1.getRcb().getBlockedlist();
                    if(blockedlist.size() == 0)
                    {
                        //没有因为该资源阻塞的进程 -> 释放资源
                        resource1.getRcb().setFree(true);
                        resourceTag = resource1;            //->等待后期从busylist中取出将其加入freelist

                        //仍然不可以在迭代的时候对链表进行增删!
                        //this.freelist.offerLast(resource1);
                        //busylist.remove(resource1);
                        //退出循环
                        break;
                    }
                    //倘若有被阻塞的进程
                    Integer integer1 = blockedlist.get(0);

                    //获取该被阻塞的进程 -> 当然也可以从当前被阻塞的资源队列中获取
                    Process process1 = this.processlist.get(integer1);

                    //?
                    //LinkedList<Process> blockedlist1 = this.blockedlist;

                    //同样设置标志位来判断是否可以将其加入Readylist之中
                    int flag = 0;

                    //for(Process process1:blockedlist1)
                    if(process1.getPcb().getID() == integer1)           //->一定会成功
                    {
                        //获取到该进程之后 -> 将该资源加入其拥有的资源列表
                        //同样的问题? -> 当然不是 --> 一定看清楚是哪一个进程!!!
                        process1.getPcb().getResourcelistR().remove(new Integer(resource1.getRcb().getRID()));
                        process1.getPcb().getResourcelistH().offerLast(resource1.getRcb().getRID());


                        //判断是否可以进入Readylist
                        if(process1.getPcb().getResourcelistR().size() == 0)
                        {
                            //所需的资源序列为0 -> 加入Readylist之中
                            //this.readylist.get(process1.getPcb().getPriority()).offerLast(process1);
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
                        else
                            flag = 2;

                        //倘若不为零 -> 放Blockedlist中呆着ba
                    }



                    //根据标志位作出处理
                    if(flag == 1)
                    {
                        //integer2即为被阻塞队列的首个进程
                        Integer integer2 = resource1.getRcb().getBlockedlist().pollFirst();     //去除首个进程
                        process1.getPcb().setState("Ready");
                        this.blockedlist.remove(process1);
                        this.readylist.get(process1.getPcb().getPriority()).offerLast(process1);    //一定找到对应的进程!!!
                        //修改对应的statelist
                        process1.getPcb().setStatelist(this.readylist.get(process1.getPcb().getPriority()));
                    }
                    else if(flag == 2)
                    {
                        //此时仍被其他资源阻塞
                        Integer integer2 = resource1.getRcb().getBlockedlist().pollFirst();
                    }
                    //break;
                }
            }
            //倘若标识不为null -> 修改busy-freelist
            if(resourceTag != null)
            {
                this.freelist.offerLast(resourceTag);
                this.busylist.remove(resourceTag);
            }
        }

        //释放内存 -> 这个时候需要扫描被阻塞的进程序列
        LinkedList<Integer> memory = process.getPcb().getMemory();
        int sum = 0;
        for(Integer integer:memory)
        {
            sum += integer;
        }
        //修改内存
        this.memorySize += sum;

        //遍历挂起队列    -> 将合适的进程加载到内存
        IdentityHashMap<Integer, Integer> memorylist = this.memorylist;
        //判断是否拥有元素 -> 拥有元素才判断是否要唤醒
        if(memorylist.size() != 0)
        {
            //实现线程安全的加减
            AtomicInteger valueTemp = new AtomicInteger();
            AtomicInteger keyTemp = new AtomicInteger();
            memorylist.forEach((key,value) -> {
                if(key < this.memorySize)
                {
                    keyTemp.set(key);
                    valueTemp.set(value);
                }
            });
            if(valueTemp.equals(0)) //倘若为0 -> 这里默认没有标号为0的进程 -> 当然也可以在foreach中添加boolean标志位
            {
                return true;
            }
            //将该进程从外存加载到内存中
            Integer integer = memorylist.get(keyTemp);
            //获取到该被挂起的进程
            Process process1 = this.processlist.get(keyTemp);
            //为其分配内存资源
            this.MemoryMelloc(process1,integer);
            //将其从挂起队列中取出
            this.suspendlist.remove(keyTemp);

            //必须对其状态进行判断
            //倘若其请求的资源已经全部获得
            if(process1.getPcb().getResourcelistR().size() == 0)
            {
                //将其加入Readylist之中
                this.readylist.get(process1.getPcb().getPriority()).offerLast(process1);
                //修改进程状态
                process1.getPcb().setState("Ready");
                //修改进程的状态队列
                process1.getPcb().setStatelist(this.readylist.get(process1.getPcb().getPriority()));
            }
            else
            {
                //倘若没有获得所有的元素 -> 将其放入阻塞队列
                this.blockedlist.offerLast(process1);
                //修改进程状态
                process1.getPcb().setState("Blocked");
                //修改进程的状态队列
                process1.getPcb().setStatelist(this.blockedlist);
            }
        }

        return true;
    }



    //初始化资源
    public void initSource()
    {
        //创建一系列处于free状态的资源并将其加入freelist链表之中
        for (int i = 10; i < 15; i++)
        {
            Resource resource = new Resource(new RCB(i + 1));
            //在freelist中添加资源
            freelist.offerLast(resource);
            //同时将该资源记录在resourcelist中
            resourcelist.put(i,resource);
        }
    }

    //创建资源
    public boolean createResource(int id)
    {
        //必须确保每一个资源是不重复的!
        Resource resource = this.resourcelist.get(id);
        if(resource == null)
        {
            this.resourcelist.put(id, new Resource(new RCB(id)));
            return true;
        }
        else
        {
            System.out.println("申请创建的资源已存在!");
            return false;
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
        System.out.println("================================");
    }

    //打印运行队列
    public void printRunningList()
    {
        System.out.println("RunningList:");
        this.runninglist.forEach(process -> {
            System.out.println(process);
        });
        System.out.println("================================");
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
        System.out.println("================================");
    }

    //打印已经运行完毕的进程
    public void printDoneList()
    {
        System.out.println("DoneList:");
        this.donelist.forEach(process -> {
            System.out.println(process);
        });
        System.out.println("================================");
    }

    //打印所有的进程
    public void printProcess()
    {
        System.out.println("ProcessList:");
        this.processlist.forEach((key,value) ->{
            System.out.println(value);
        });
        System.out.println("================================");
    }

    //打印被挂起的进程
    public void printSuspendList()
    {
        System.out.println("SuspendList:");
        this.suspendlist.forEach(process ->{
            System.out.println(process);
        });
        System.out.println("================================");
    }

}
