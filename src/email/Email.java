package email;

/**
 * @author LYHstart
 * @create 2021-11-08 22:29
 */

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

    public static void main(String[] args) {

        //测试sendEmail()方法
        String state = sendEmail();
        System.out.println(state);

    }


    public static String  sendEmail() {

        String flag = "";

        //建立邮件会话
        Properties pro = new Properties();
        pro.put("mail.smtp.host","smtp.qq.com");//存储发送邮件的服务器
        pro.put("mail.smtp.auth","true");  //通过服务器验证

        Session s =Session.getInstance(pro); //根据属性新建一个邮件会话
        //s.setDebug(true);

        //由邮件会话新建一个消息对象
        MimeMessage message = new MimeMessage(s);

        //设置邮件
        InternetAddress fromAddr = null;
        InternetAddress toAddr = null;

        try
        {
            fromAddr = new InternetAddress(1783094900+"@qq.com");   //邮件发送地址
            message.setFrom(fromAddr);         //设置发送地址

            toAddr = new InternetAddress("1557902391@qq.com");       //邮件接收地址
            message.setRecipient(Message.RecipientType.TO, toAddr);  //设置接收地址

            message.setSubject("Hello SMTP");   //设置邮件标题

            message.setContent(
                    "<div>" +
                            "<br>" +
                            "</div>" +
                            "<div>" +
                            "![](+)"+

                            "<br>" +
                            "</div>" +

                            "<div>" +
                            "<includetail>  亲爱的用户： </includetail>" +
                            "</div>" +
                            "<div>" +
                            "<includetail><br></includetail>" +
                            "</div>" +
                            "<div>" +
                            "<includetail>         您好，您的xxxxOA帐号密码重置为： <b><font color="+"\""+"#ff0000"+"\""+">"+"new password"+"</font></b>(此密码为临时密码)，</includetail>" +
                            "</div>" +
                            "<div>" +
                            "<includetail><br></includetail>" +
                            "</div>" +
                            "<div>" +
                            "<includetail>  请登录后到我的账号信息中重新修改密码如果您对此通知有任何疑问，请联系管理员。</includetail>" +
                            "</div>" +
                            "<div>"+
                            "<includetail><br></includetail>"+
                            "</div>" +
                            "<div>" +
                            "<includetail>  </includetail>" +
                            "<span style="+"\""+"color: rgb(255, 0, 0); font-family: 微软雅黑, sans-serif; line-height: 1.5;"+"\""+">此信由支xxxOA系统发出，系统不接受回信，因此请勿直接回复。</span>" +
                            "</div>" +
                            "<div>" +
                            "<br>" +
                            "</div>" +
                            "<div>" +
                            "<span style="+"\""+"font-family: 微软雅黑, sans-serif;"+"\""+"></span>" +
                            "</div>","text/html;charset=UTF-8");

            message.setSentDate(new Date()); //设置邮件日期

            message.saveChanges();    //保存邮件更改信息

            Transport transport = s.getTransport("smtp");
            transport.connect("smtp.qq.com", "1783094900@qq.com", "mcpossicvuwobfdh"); //服务器地址，邮箱账号，邮箱密码
            transport.sendMessage(message, message.getAllRecipients());   //发送邮件
            transport.close();//关闭

            return flag = "true";

        }
        catch (Exception e)
        {
            e.printStackTrace();
            flag = "false";//发送失败
        }

        return flag;
    }
}


