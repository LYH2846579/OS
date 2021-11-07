package exp2;

import java.util.Objects;

/**
 * @author LYHstart
 * @create 2021-11-04 19:31
 *
 * 资源类
 */
public class Resource
{
    //资源控制块
    private RCB rcb;

    public Resource() {
    }
    public Resource(RCB rcb) {
        this.rcb = rcb;
    }

    public RCB getRcb() {
        return rcb;
    }
    public void setRcb(RCB rcb) {
        this.rcb = rcb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(rcb, resource.rcb);
    }
    @Override
    public int hashCode() {
        return Objects.hash(rcb);
    }
}
