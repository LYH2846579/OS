package exp3;

import org.junit.Test;

/**
 * @author LYHstart
 * @create 2021-11-11 20:12
 *
 * 存储器管理测试类
 */
public class MemoryTest
{
    @Test
    public void test()
    {
        //实例化内存对象
        Memory memory = new Memory();
        //创建进程
        Process p1 = new Process("p1",memory);
        Process p2 = new Process("p2",memory);
        Process p3 = new Process("p3",memory);
        Process p4 = new Process("p4",memory);
        Process p5 = new Process("p5",memory);
        //实例化Thread对象
        Thread t1 = new Thread(p1,"p1");
        Thread t2 = new Thread(p2,"p2");
        Thread t3 = new Thread(p3,"p3");
        Thread t4 = new Thread(p4,"p4");
        Thread t5 = new Thread(p5,"p5");
        //启动线程
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();


        try
        {
            Thread.sleep(100000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

//内存类
class Memory
{
    //内存大小
    private int memorySize = 100;
    //内存数组
    private MemoryCtrl[] memoryCtrls = new MemoryCtrl[memorySize];
    //拥有一个控制单元
    private MemoryCtrl ctrl;

    public Memory() {
        memoryCtrls[0] = new MemoryCtrl(0,memorySize,memorySize,true);
    }

    //proRun()方法
    public synchronized void proRun(int size)
    {
        if(requestMem(size))
        {
            //System.out.println(Thread.currentThread().getName()+"申请到内存:["+ctrl.getBegin()+"~"+ctrl.getEnd()+"]");
            //睡眠
            try
            {
                Thread.sleep(100);
                //每隔一段时间唤醒一次
                wait(100);
                notify();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            //释放内存
            this.releaseMem();
            notify();
        }
        else
        {
            try
            {
                wait();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    //申请内存
    public synchronized boolean requestMem(int size)
    {
        //设置是否已经申请到内存的标志位
        boolean flag = false;
        //设置下标
        int index = 0;


        while(true)
        {
            //增加数组下标判断 ->　防止数组下标越界
            if(index >= this.memorySize)
            {
                //返回一个空指针
                return false;
            }

            //从头开始寻找空间
            MemoryCtrl memoryCtrl = memoryCtrls[index];
            //倘若处于空闲状态
            if(memoryCtrl.isFree)
            {
                //判断大小是否足够
                if(memoryCtrl.getSize() > size)
                {
                    //内存充足 -> 重新规划内存
                    MemoryCtrl memoryCtrl1 = new MemoryCtrl(memoryCtrl.getBegin(),memoryCtrl.getBegin()+size, size, false);
                    //将新申请的控制块放在原来开始的地方
                    memoryCtrls[index] = memoryCtrl1;
                    //修改原来memoryCtrl的属性值
                    memoryCtrl.setBegin(memoryCtrl.getBegin()+size);
                    memoryCtrl.setSize(memoryCtrl.getSize()-size);
                    //修改原来控制块的位置
                    memoryCtrls[memoryCtrl.getBegin()] = memoryCtrl;

                    //输出提示信息
                    System.out.println(Thread.currentThread().getName()+"申请到内存:["+memoryCtrl1.getBegin()+"~"+memoryCtrl1.getEnd()+"]");
                    //
                    ctrl = memoryCtrl1;
                    return true;
                }
                else if(memoryCtrl.getSize() == size)   //倘若大小刚刚相等
                {
                    memoryCtrl.setFree(false);
                    //System.out.println(Thread.currentThread().getName()+"申请到内存:["+memoryCtrl.getBegin()+"~"+memoryCtrl.getEnd()+"]");
                    ctrl = memoryCtrl;
                    return true;
                }

                //若内存不足了 -> 与内存处于Busy状态合并处理
            }

            //修改下标位置，使得寻找下一个控制块存在的位置
            index += memoryCtrl.getSize();
        }


    }

    //释放内存
    public synchronized void releaseMem()
    {
        //释放内存
        ctrl.setFree(true);
        //判断是否可以合并 -> 其实仅仅扫描前后两个控制块即可，但是由于寻找控制块单元存在着复杂性
        //不妨直接遍历合并
        int index = 0;
        while(true)
        {
            if(index >= this.memorySize)
                break;

            MemoryCtrl memoryCtrl = this.memoryCtrls[index];
            if(index + memoryCtrl.getSize() >= this.memorySize)
                break;
            MemoryCtrl memoryCtrl1 = this.memoryCtrls[index + memoryCtrl.getSize()];
            //预防空指针
            if(memoryCtrl1 == null)
                break;
            //倘若两个控制块中均为free状态 -> 则进行合并
            if(memoryCtrl.isFree && memoryCtrl1.isFree)
            {
                //合并
                memoryCtrl.setEnd(memoryCtrl1.getEnd());
                //计算大小
                memoryCtrl.setSize(memoryCtrl1.getEnd()-memoryCtrl.getBegin());
                //释放memoryCtrl1
                this.memoryCtrls[memoryCtrl1.getBegin()] = null;
            }

            //倘若不是 -> 则继续向下扫描
            index += memoryCtrl.getSize();
        }
    }

    public int getMemorySize() {
        return memorySize;
    }
    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }
    public MemoryCtrl[] getMemoryCtrls() {
        return memoryCtrls;
    }
    public void setMemoryCtrls(MemoryCtrl[] memoryCtrls) {
        this.memoryCtrls = memoryCtrls;
    }
}

//创建申请内存及释放内存的进程
class Process implements Runnable
{
    //名字
    private String name;
    //拥有一个私有属性
    private Memory memory;
    //拥有一个控制单元
    private MemoryCtrl ctrl;

    public Process() {
    }
    public Process(Memory memory) {
        this.memory = memory;
    }
    public Process(String name, Memory memory) {
        this.name = name;
        this.memory = memory;
    }
    public Process(Memory memory, MemoryCtrl ctrl) {
        this.memory = memory;
        this.ctrl = ctrl;
    }

    @Override
    public void run() {
        while(true)
        {
            //随机产生一个需要的内存大小
            double random = Math.random()*memory.getMemorySize();

            memory.proRun((int) random);


        }
    }
}


