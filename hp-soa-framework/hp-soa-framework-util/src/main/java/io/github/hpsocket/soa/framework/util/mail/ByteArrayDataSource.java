/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Version    : JessMA 3.5.1
 * Author    : Bruce Liang
 * Website    : http://www.jessma.org
 * Project    : http://www.oschina.net/p/portal-basic
 * Blog        : http://www.cnblogs.com/ldcsaa
 * WeiBo    : http://weibo.com/u/1402935851
 * QQ Group    : 75375912
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hpsocket.soa.framework.util.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import jakarta.activation.DataSource;


/** Byte Array 数据源 */
class ByteArrayDataSource implements DataSource
{
    public static final String DEFAULT_ENCODING = GeneralHelper.DEFAULT_ENCODING;
    
    private ByteArrayOutputStream   baos = null;
    private String                  type = "application/octet-stream";
    private String                  name = "ByteArrayDataSource";
    
    private void init(String type, String name)
    {
        if(type != null)
            this.type = type;
        if(name != null)
            this.name = name;        
    }

    public ByteArrayDataSource(byte[] data, String type, String name) throws IOException
    {
        ByteArrayInputStream bis = null;

        try
        {
            bis = new ByteArrayInputStream(data);
            this.write(bis, type, name);
        }
        catch (IOException ioex)
        {
            throw ioex;
        }
        finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }

    }

    public ByteArrayDataSource(InputStream aIs, String type, String name) throws IOException
    {
        this.write(aIs, type, name);
    }

    private void write(InputStream aIs, String type, String name) throws IOException
    {
        init(type, name);
        
        BufferedInputStream bis        = null;
        BufferedOutputStream bos    = null;
        
        try
        {
            int length = 0;
            byte[] buffer = new byte[4096];

            bis        = new BufferedInputStream(aIs);
            baos    = new ByteArrayOutputStream();
            bos        = new BufferedOutputStream(baos);

            while ((length = bis.read(buffer)) != -1)
            {
                bos.write(buffer, 0, length);
            }        
        }
        catch (IOException ioex)
        {
            throw ioex;
        }
        finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }
                if (baos != null)
                {
                    baos.close();
                }
                if (bos != null)
                {
                    bos.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
    }

    public ByteArrayDataSource(String data, String type, String name) throws IOException
    {
        this(data, DEFAULT_ENCODING, type, name);
    }

    public ByteArrayDataSource(String data, String encoding, String type, String name) throws IOException
    {
        init(type, name);

        try
        {
            baos = new ByteArrayOutputStream();
            baos.write(data.getBytes(encoding));
        }
        finally
        {
            try
            {
                if (baos != null)
                {
                    baos.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
    }

    @Override
    public String getContentType()
    {
        return type;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        if (baos == null)
        {
            throw new IOException("no data");
        }
        
        return new ByteArrayInputStream(toByteArray());
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        baos = new ByteArrayOutputStream();
        return baos;
    }
    
    public byte[] toByteArray()
    {
        return baos.toByteArray();
    }
}

