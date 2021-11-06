import java.util.Objects;
import java.util.Scanner;

/**
 * @author LYHstart
 * @create 2021-11-04 19:32
 *
 * 进程类
 */
public class Process
{
    //进程控制块
    private PCB pcb;

    public Process() {
    }
    public Process(PCB pcb) {
        this.pcb = pcb;
    }

    public PCB getPcb() {
        return pcb;
    }
    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return Objects.equals(pcb, process.pcb);
    }
    @Override
    public int hashCode() {
        return Objects.hash(pcb);
    }

    @Override
    public String toString() {
        return "Process{" +
                "pcb=" + pcb +
                '}';
    }

    //添加进程需要的资源id
    public void addSourceId(int id)
    {
        //加入尚未获取的资源链表
        this.pcb.getResourcelistR().offerLast(id);
    }
    //删除对应的资源申请id
    public void delsourceId(int id)
    {
        //删除对应的id元素
        this.pcb.getResourcelistR().remove(new Integer(id));
    }
    //加入已经申请到的资源链表
    public void addHavingList(int id)
    {
        this.pcb.getResourcelistH().offerLast(id);
    }
    //从已经申请到的资源列表中删除
    public void delHavingList(int id)
    {
        this.pcb.getResourcelistH().remove(new Integer(id));
    }
}
