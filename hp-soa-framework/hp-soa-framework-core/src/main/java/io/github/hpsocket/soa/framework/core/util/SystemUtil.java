
package io.github.hpsocket.soa.framework.core.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/** <b>系统信息帮助类</b> */
public class SystemUtil
{

    private static String pid;
    private static Addr addr;

    public static class Addr
    {
        public String host;
        public String ip;
        public Set<String> ips = new LinkedHashSet<String>();
    }

    public static String getPid()
    {
        checkPid();
        
        return pid;
    }

    public static String getHosName()
    {
        checkAddr();
        
        return addr.host;
    }

    public static String getAddress()
    {
        checkAddr();
        
        return addr.ip;
    }

    public static Set<String> getAddresses()
    {
        checkAddr();
        
        return addr.ips;
    }

    private static void checkPid()
    {
        if(pid == null)
        {
            synchronized(SystemUtil.class)
            {
                if(pid == null)
                {
                    String arr[] = ManagementFactory.getRuntimeMXBean().getName().split("@");
                    
                    if(arr.length > 0)
                        pid = arr[0];
                }
            }
        }
    }

    private static void checkAddr()
    {
        if(addr == null)
        {
            synchronized(SystemUtil.class)
            {
                if(addr == null)
                {
                    addr = getNetworkAddress();
                }
            }
        }
    }

    public static Addr getNetworkAddress()
    {
        Addr addr = new Addr();

        try
        {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();

            while(ifs.hasMoreElements())
            {
                NetworkInterface ni = ifs.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();

                while(addresses.hasMoreElements())
                {
                    InetAddress ia = addresses.nextElement();

                    if(ia.isSiteLocalAddress())
                    {
                        addr.ips.add(ia.getHostAddress());
                    }
                }
            }

            InetAddress localAddr = InetAddress.getLocalHost();

            if(!addr.ips.isEmpty())
                addr.ip = addr.ips.iterator().next();
            else
            {
                addr.ip = localAddr.getHostAddress();

                if(GeneralHelper.isStrNotEmpty(addr.ip))
                    addr.ips.add(addr.ip);
            }

            addr.host = localAddr.getHostName();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        return addr;
    }

    public static boolean isLocalNetwork(String ip)
    {
        if(GeneralHelper.isStrEmpty(ip))
            return false;
        
        try
        {
            InetAddress addr = InetAddress.getByName(ip);
            return addr.isSiteLocalAddress() || addr.isLoopbackAddress();
        }
        catch(UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

}
