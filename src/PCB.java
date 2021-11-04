import com.sun.xml.internal.bind.v2.model.core.ID;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author LYHstart
 * @create 2021-11-04 19:06
 */
public class PCB
{
    //进程ID
    private int ID;
    //内存 -> 目前将int类型当做内存进行分配
    private LinkedList<Integer> Memory;
    //内存分配指示
    private boolean MemoryState;
    //已经获取的资源列表和未获取的资源列表
    private LinkedList<Integer> resourcelistH;     //已经获取的资源列表
    private LinkedList<Integer> resourcelistR;     //未获取的资源列表
    //进程状态
    private String state;
    private LinkedList<Process> statelist;
    //生成树
    private PCB ppcb;   //父进程指针
    private PCB cpcb;   //子进程指针
    //优先级
    private int priority;


    public PCB() {
    }
    public PCB(int ID) {
        this.ID = ID;
        this.MemoryState = false;
        this.resourcelistH = new LinkedList<>();
        this.resourcelistR = new LinkedList<>();
        this.state = "Ready";
        //暂时不赋值
        this.statelist = null;
        this.ppcb = null;
        this.cpcb = null;
        //默认优先级为3
        this.priority = 3;
    }
    public PCB(int ID, int priority) {
        this.ID = ID;
        this.MemoryState = false;
        this.resourcelistH = new LinkedList<>();
        this.resourcelistR = new LinkedList<>();
        this.state = "Ready";
        //暂时不赋值
        this.statelist = null;
        this.ppcb = null;
        this.cpcb = null;
        this.priority = priority;
    }
    public PCB(int ID, LinkedList<Integer> memory, boolean memoryState, LinkedList<Integer> resourcelistH, LinkedList<Integer> resourcelistR, String state, LinkedList<Process> statelist, PCB ppcb, PCB cpcb, int priority) {
        this.ID = ID;
        Memory = memory;
        MemoryState = memoryState;
        this.resourcelistH = resourcelistH;
        this.resourcelistR = resourcelistR;
        this.state = state;
        this.statelist = statelist;
        this.ppcb = ppcb;
        this.cpcb = cpcb;
        this.priority = priority;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public LinkedList<Integer> getMemory() {
        return Memory;
    }
    public void setMemory(LinkedList<Integer> memory) {
        Memory = memory;
    }
    public LinkedList<Integer> getResourcelistH() {
        return resourcelistH;
    }
    public void setResourcelistH(LinkedList<Integer> resourcelistH) {
        this.resourcelistH = resourcelistH;
    }
    public LinkedList<Integer> getResourcelistR() {
        return resourcelistR;
    }
    public void setResourcelistR(LinkedList<Integer> resourcelistR) {
        this.resourcelistR = resourcelistR;
    }
    public boolean isMemoryState() {
        return MemoryState;
    }
    public void setMemoryState(boolean memoryState) {
        MemoryState = memoryState;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public LinkedList<Process> getStatelist() {
        return statelist;
    }
    public void setStatelist(LinkedList<Process> statelist) {
        this.statelist = statelist;
    }
    public PCB getPpcb() {
        return ppcb;
    }
    public void setPpcb(PCB ppcb) {
        this.ppcb = ppcb;
    }
    public PCB getCpcb() {
        return cpcb;
    }
    public void setCpcb(PCB cpcb) {
        this.cpcb = cpcb;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PCB pcb = (PCB) o;
        return ID == pcb.ID &&
                MemoryState == pcb.MemoryState &&
                priority == pcb.priority &&
                Objects.equals(Memory, pcb.Memory) &&
                Objects.equals(resourcelistH, pcb.resourcelistH) &&
                Objects.equals(resourcelistR, pcb.resourcelistR) &&
                Objects.equals(state, pcb.state) &&
                Objects.equals(statelist, pcb.statelist) &&
                Objects.equals(ppcb, pcb.ppcb) &&
                Objects.equals(cpcb, pcb.cpcb);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, Memory, MemoryState, resourcelistH, resourcelistR, state, statelist, ppcb, cpcb, priority);
    }


}
