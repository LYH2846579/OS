package exp3_ch;

/**
 * @author LYHstart
 * @create 2021-11-11 20:08
 *
 * 实现内存管理的类
 */
public class MemoryCtrl
{
    private int begin;  //标识当前占用内存的起始标记
    private int end;    //内存地址结束标识
    private int size;   //记录占用的内存           其中 end-begin=size 在整个过程之中恒成立
    boolean isFree;     //标记当前区域的内存是否处于Free状态

    public MemoryCtrl() {
    }
    public MemoryCtrl(int begin, int end, boolean isFree) {
        this.begin = begin;
        this.end = end;
        this.isFree = isFree;
    }
    public MemoryCtrl(int begin, int size) {
        this.begin = begin;
        this.size = size;
        this.end = begin+size;
    }
    public MemoryCtrl(int begin, int end, int size, boolean isFree) {
        this.begin = begin;
        this.end = end;
        this.size = size;
        this.isFree = isFree;
    }

    public int getBegin() {
        return begin;
    }
    public void setBegin(int begin) {
        this.begin = begin;
    }
    public int getEnd() {
        return end;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public boolean isFree() {
        return isFree;
    }
    public void setFree(boolean free) {
        isFree = free;
    }


}
