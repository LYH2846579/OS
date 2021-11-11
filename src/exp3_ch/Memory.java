package exp3_ch;

//内存类
class Memory
{
    //内存大小
    private int memorySize = 100;
    //内存数组
    private MemoryCtrl[] memoryCtrls = new MemoryCtrl[memorySize];

    public Memory() {
        memoryCtrls[0] = new MemoryCtrl(0,memorySize,memorySize,true);
    }


    //申请内存
    public boolean requestMem(Process process,int size)
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
                //打印未申请的信息
                System.out.println("未申请到:size = "+size);
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
                    System.out.println("申请到内存:["+memoryCtrl1.getBegin()+"~"+memoryCtrl1.getEnd()+"]");
                    //
                    process.setCtrl(memoryCtrl1);
                    return true;
                }
                else if(memoryCtrl.getSize() == size)   //倘若大小刚刚相等
                {
                    memoryCtrl.setFree(false);
                    System.out.println("申请到内存:["+memoryCtrl.getBegin()+"~"+memoryCtrl.getEnd()+"]");
                    process.setCtrl(memoryCtrl);
                    return true;
                }
                //若内存不足了 -> 与内存处于Busy状态合并处理
            }



            //修改下标位置，使得寻找下一个控制块存在的位置
            index += memoryCtrl.getSize();
        }
    }

    //释放内存
    public void releaseMem(Process process)
    {
        //释放内存
        process.getCtrl().setFree(true);
        //判断是否可以合并 -> 其实仅仅扫描前后两个控制块即可，但是由于寻找控制块单元存在着复杂性
        //不妨直接遍历合并
        int index = 0;
        while(true)
        {
            if(index >= this.memorySize)
                break;

            MemoryCtrl memoryCtrl = this.memoryCtrls[index];
            int temp = index + memoryCtrl.getSize();
            if(temp >= this.memorySize)
                break;
            MemoryCtrl memoryCtrl1 = this.memoryCtrls[temp];
            //预防空指针
            if(memoryCtrl1 == null)
                break;
            //倘若两个控制块中均为free状态 -> 则进行合并
            if(memoryCtrl.isFree && memoryCtrl1.isFree)
            {
                //计算大小
                memoryCtrl.setSize(memoryCtrl1.getEnd()-memoryCtrl.getBegin());

                //合并
                memoryCtrl.setEnd(memoryCtrl1.getEnd());

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

class Process
{
    //名字
    private String name;

    //拥有一个控制单元
    private MemoryCtrl ctrl;

    //运行时间
    private int times;

    public Process() {
    }
    public Process(String name, int times) {
        this.name = name;
        this.times = times;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public MemoryCtrl getCtrl() {
        return ctrl;
    }
    public void setCtrl(MemoryCtrl ctrl) {
        this.ctrl = ctrl;
    }
    public int getTimes() {
        return times;
    }
    public void setTimes(int times) {
        this.times = times;
    }

}


