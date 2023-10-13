package io.github.hpsocket.soa.framework.util.mail;

import java.io.File;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import lombok.Getter;
import lombok.Setter;

/** 邮件发送器 */
@Getter
@Setter
public class MailSender
{
    /** 默认 Content Type -> text/plain */
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";
    /** 默认字符编码 -> UTF-8 */
    public static final String DEFAULT_ENCODING     = GeneralHelper.DEFAULT_ENCODING;
    /** 默认 SMTP 端口 -> 25 */
    public static final int DEFAULT_PORT            = 25;
    
    private String host         = "";
    private String from         = "";
    private String user         = "";
    private String password     = "";
    private String subject      = "";
    private String text         = "";
    private String contentType  = DEFAULT_CONTENT_TYPE;
    private String charset      = DEFAULT_ENCODING;
    private int port            = DEFAULT_PORT;
    private boolean auth        = true;
    private boolean needReceipt = false;
    private Date sentDate       = null;
    
    private List<String>        to      = new ArrayList<String>();
    private List<String>        cc      = new ArrayList<String>();
    private List<String>        bcc     = new ArrayList<String>();
    private List<String>        replyTo = new ArrayList<String>();
    private List<String>        fileAcc = new ArrayList<String>();
    private List<MimeBodyPart>  byteAcc = new ArrayList<MimeBodyPart>();
    
    private boolean proxySet;
    private String socksProxyHost;
    private int socksProxyPort;
    
    /** 发送邮件 */
    public void send() throws Exception
    {
        Transport transport = null;
        
        try
        {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", Boolean.toString(auth));
            
            if(proxySet)
            {
                props.setProperty("proxySet", "true");
                props.setProperty("socksProxyHost", socksProxyHost);
                props.setProperty("socksProxyPort", String.valueOf(socksProxyPort));                
            }
            
            //props.put("mail.smtp.host", host);
            //props.put("mail.smtp.port", Integer.toString(port));
            //props.put("mail.smtp.user", user);
            //props.put("mail.smtp.password", password);
            
            Session session = Session.getDefaultInstance(props, null);
            // session.setDebug(true);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            
            for(String i : to)
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(i));
            for(String i : cc)
                msg.addRecipient(Message.RecipientType.CC, new InternetAddress(i));
            for(String i : bcc)
                msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(i));

            if(replyTo.size() > 0)
            {
                InternetAddress[] replyAddress = new InternetAddress[replyTo.size()];
                
                for(int i = 0; i < replyAddress.length; i++)
                    replyAddress[i] = new InternetAddress((String)replyTo.get(i));
                
                msg.setReplyTo(replyAddress);
            }
            
            if(needReceipt)
                msg.addHeader("Disposition-Notification-To", from);
            
            if(sentDate != null)
                msg.setSentDate(sentDate);
            else
                msg.setSentDate(new Date());
            
            msg.setSubject(subject, charset);
            
            MimeMultipart mm    = new MimeMultipart();
            MimeBodyPart mbText = new MimeBodyPart();
            mbText.setContent(text, contentType + ";charset=" + charset);
            mm.addBodyPart(mbText);
            
            for(String filePath : fileAcc)
            {
                String fileName = (new File(filePath)).getName();
                fileName = MimeUtility.encodeText(fileName, charset, "B");
                
                MimeBodyPart mbFile   = new MimeBodyPart();
                DataSource datasource = new FileDataSource(filePath);
                
                mbFile.setDataHandler(new DataHandler(datasource));
                mbFile.setFileName(fileName);
                mm.addBodyPart(mbFile);
            }
            
            for(MimeBodyPart part : byteAcc)
                mm.addBodyPart(part);
            
            msg.setContent(mm);
            msg.saveChanges();
            
            transport = session.getTransport();
            transport.connect(host, port, user, password);
            transport.sendMessage(msg, msg.getAllRecipients());
        }
        finally
        {
            if(transport != null) try{ transport.close(); } catch (Exception e) { }
        }
    }

    public void addFileAcc(String accessory)
    {
        fileAcc.add(accessory);
    }
    
    /** 添加 byte array 形式的附件 */
    public void addByteAcc(byte[] accessory, String type, String fileName) throws Exception
    {
        MimeBodyPart mimeFile = new MimeBodyPart();

        //BASE64Encoder enc = new BASE64Encoder();
        //fileName = "=?GBK?B?" + enc.encode(fileName.getBytes()) + "?=";
        //mimeFile.setFileName(ds.getName());
        fileName = MimeUtility.encodeText(fileName, charset, "B");
        mimeFile.setFileName(fileName);

        //ByteArrayDataSource ds = new ByteArrayDataSource(accessory, type, fileName);
        //mimeFile.setDataHandler(new DataHandler(ds));
        mimeFile.setDataHandler(new DataHandler(accessory, type));
        
        byteAcc.add(mimeFile);
    }
    
    public void addReplyTo(String address)
    {
        replyTo.add(address);
    }
    
    public void addTo(String address)
    {
        to.add(address);
    }
    
    public void addCc(String address)
    {
        cc.add(address);
    }
    
    public void addBcc(String address)
    {
        bcc.add(address);
    }

}
