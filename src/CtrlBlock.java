import java.sql.PreparedStatement;
import java.util.ArrayList;
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

    public CtrlBlock() {
        this.memorySize = 1000;
        this.readylist = new ArrayList<>();
        this.runninglist = new LinkedList<>();
        this.blockedlist = new LinkedList<>();
        this.suspendlist = new LinkedList<>();
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
        //针对readylist进行初始化
        for (int i = 0; i < 10; i++)
        {
            readylist.add(new LinkedList<>());
        }
    }
    public CtrlBlock(int memorySize, ArrayList<LinkedList<Process>> readylist, LinkedList<Process> runninglist, LinkedList<Process> blockedlist, LinkedList<Process> suspendlist, LinkedList<Resource> busylist, LinkedList<Resource> freelist) {
        this.memorySize = memorySize;
        this.readylist = readylist;
        this.runninglist = runninglist;
        this.blockedlist = blockedlist;
        this.suspendlist = suspendlist;
        this.busylist = busylist;
        this.freelist = freelist;
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
        if(b)
        {
            //将其加入就绪队列
            process.getPcb().setStatelist(this.readylist.get(priority));
            //将创建的进程加入就绪队列 -> 根据优先级的不同进行添加
            LinkedList<Process> processes = readylist.get(priority);
            processes.offerLast(process);
            return true;
        }
        else
            return false;

    }

    //申请资源
    public boolean resourceReq(Process process,int id)
    {
        //必须判断是否存在该资源id
        int flag = 0;
        for(Resource resource:freelist)
        {
            if(resource.getRcb().getRID() == id)
            {
                flag = 1;
                break;
            }
        }
        if(flag == 0)
        {
            for(Resource resource:busylist)
            {
                if(resource.getRcb().getRID() == id)
                {
                    flag = 2;
                    break;
                }
            }
        }

        if(flag == 0)
        {
            return false;
        }
        else
        {
            //首先将申请的资源id加入未申请到的资源列表中
            process.addSourceId(id);
            //接下来等待总控单元的分配

            //此时对应的资源RCB中的申请列表中也应该获取到该进程
            //只有阻塞的时候才会加入

            //调用资源分配程序
            boolean scheduler = this.scheduler();

            return scheduler;    //加入申请成功
        }
    }

    //实现资源分配 -> 进入调度队列?
    public boolean scheduler()
    {
        /*
        这里实现多级反馈系统,首先自底向上扫描Readylist中的内容，判断是否存在二级链表
        若存在，则开始扫描这个链表。分析其内部的进程所需的资源 -> 在其未申请到的资源列表中进行遍历
             -> 同时针对于每一个请求的资源都会在主控单元中对资源序列进行扫描，查询对应请求的资源是否处于free状态
             -> 倘若所申请的资源被其他进程所占用，则将该进程置于blockedlist中，等待所需资源的释放
             -> 倘若所申请的资源处于空闲状态，则将该资源加入该进程的资源列表中,并将该进程加入Ready队列中重新等待系统调度
             -> 倘若没有申请的资源(即资源列表为空).则即为执行完毕的资源，就将其所拥有的资源及内存全部释放
             -> 注意可能出现的死锁问题

         */

        return true;
    }



    //初始化资源




}
