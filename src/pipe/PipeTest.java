package pipe;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author LYHstart
 * @create 2021-11-07 9:48
 *
 * 使用pipe实现线程之间的通信
 */
public class PipeTest
{
    @Test
    public void test()
    {
        //创建管道输入输出流
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream();
        try
        {
            //关联
            pos.connect(pis);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //启动线程池实现线程之间的通信
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new Sender(pos));
        executorService.submit(new Receiver(pis));
        //关闭线程池
        executorService.shutdown();
    }
}

//实现数据发送类
class Sender extends Thread
{
    private PipedOutputStream outputStream;

    public Sender(PipedOutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        //生成一个全局唯一标识符
        //String str = UUID.randomUUID().toString();
        String str = "周老师是个大好人!";
        //System.out.println("Sender"+str);
        try
        {
            outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally{
            try
            {
                if(outputStream != null)
                    outputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

//接收类
class Receiver implements Runnable
{
    private PipedInputStream inputStream;

    public Receiver(PipedInputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] buff = new byte[36];
        try
        {
            while((read = inputStream.read(buff)) != -1)
            {
                baos.write(buff,0,read);
            }
            System.out.println(baos.toString());
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if(inputStream != null)
                    inputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
