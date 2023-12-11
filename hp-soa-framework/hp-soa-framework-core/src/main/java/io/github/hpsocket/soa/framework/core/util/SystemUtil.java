
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

    public static final String getPid()
    {
        checkPid();
        
        return pid;
    }

    public static final String getHosName()
    {
        checkAddr();
        
        return addr.host;
    }

    public static final String getAddress()
    {
        checkAddr();
        
        return addr.ip;
    }

    public static final Set<String> getAddresses()
    {
        checkAddr();
        
        return addr.ips;
    }

    private static final void checkPid()
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

    private static final void checkAddr()
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

    public static final Addr getNetworkAddress()
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

                    if(isValidUnicastAddress(ia))
                    {
                        addr.ips.add(ia.getHostAddress());
                    }
                }
            }

            InetAddress localAddr = InetAddress.getLocalHost();
            
            if(isValidUnicastAddress(localAddr))
            {
                addr.ip = localAddr.getHostAddress();
                addr.ips.add(addr.ip);
            }
            else if(!addr.ips.isEmpty())
                addr.ip = addr.ips.iterator().next();

            addr.host = localAddr.getHostName();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        return addr;
    }

    public static final boolean isLocalNetworkAddress(String ip)
    {
        if(GeneralHelper.isStrEmpty(ip))
            return false;
        
        try
        {
            InetAddress addr = InetAddress.getByName(ip);
            return isLocalNetworkAddress(addr);
        }
        catch(UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static final boolean isLocalNetworkAddress(InetAddress addr)
    {
        return addr != null && (addr.isSiteLocalAddress() || addr.isLoopbackAddress());
    }
    
    public static final boolean isValidUnicastAddress(String ip)
    {
        if(GeneralHelper.isStrEmpty(ip))
            return false;
        
        try
        {
            InetAddress addr = InetAddress.getByName(ip);
            return isValidUnicastAddress(addr);
        }
        catch(UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static final boolean isValidUnicastAddress(InetAddress addr)
    {
        return  (addr != null && 
                !(
                    addr.isAnyLocalAddress()  ||
                    addr.isLoopbackAddress()  ||
                    addr.isLinkLocalAddress() ||
                    addr.isMulticastAddress()
                ));
    }

}
