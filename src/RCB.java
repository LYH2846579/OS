import java.util.LinkedList;
import java.util.Objects;

/**
 * @author LYHstart
 * @create 2021-11-04 19:17
 *
 * 资源控制单元
 */
public class RCB
{
    private int RID;
    private boolean isFree;
    //被本资源阻塞的进程链表
    private LinkedList<Integer> blockedlist;

    public RCB() {
    }
    public RCB(int RID, boolean isFree, LinkedList<Integer> blockedlist) {
        this.RID = RID;
        this.isFree = isFree;
        this.blockedlist = blockedlist;
    }

    public int getRID() {
        return RID;
    }
    public void setRID(int RID) {
        this.RID = RID;
    }
    public boolean isFree() {
        return isFree;
    }
    public void setFree(boolean free) {
        isFree = free;
    }
    public LinkedList<Integer> getBlockedlist() {
        return blockedlist;
    }
    public void setBlockedlist(LinkedList<Integer> blockedlist) {
        this.blockedlist = blockedlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RCB rcb = (RCB) o;
        return RID == rcb.RID &&
                isFree == rcb.isFree &&
                Objects.equals(blockedlist, rcb.blockedlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RID, isFree, blockedlist);
    }


    //将对应的进程编号UID加入等待队列中
    public void addBlocked(int id)
    {
        this.blockedlist.offerLast(id);
    }
}
