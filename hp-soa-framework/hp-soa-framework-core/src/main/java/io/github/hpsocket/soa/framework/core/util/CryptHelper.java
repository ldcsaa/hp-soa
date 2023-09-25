package io.github.hpsocket.soa.framework.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/** <b>编解码、加解密帮助类</b> */
public class CryptHelper
{
    /** AES 密匙长度 */
    public static final int AES_KEY_SIZE        = 128;
    /** DES 密匙长度 */
    public static final int DES_KEY_SIZE        = 56;
    /** 加密模式 */
    public static final int ENCRYPT_MODE        = Cipher.ENCRYPT_MODE;
    /** 解密模式 */
    public static final int DECRYPT_MODE        = Cipher.DECRYPT_MODE;
    /** 默认字符集（UTF-8） */
    public static final String DEFAULT_ENCODING    = GeneralHelper.DEFAULT_ENCODING;
    /** 加密方法：MD5 */
    public static final String MD5                = "MD5";
    /** 加密方法：SHA */
    public static final String SHA                = "SHA";
    /** 加密方法：SHA-1 */
    public static final String SHA_1            = "SHA-1";
    /** 加密方法：SHA-256 */
    public static final String SHA_256            = "SHA-256";
    /** 加密方法：AES */
    public static final String AES                = "AES";
    /** 加密方法：DES */
    public static final String DES                = "DES";
    
    private static final String SEC_RAN_ALG        = "SHA1PRNG";
    
    private static final String MYSQL_CIPHER    = "AES/ECB/PKCS5Padding";

    /** byte[] -> 十六进制字符串 (小写) */
    public final static String bytes2HexStr(byte[] bytes)
    {
        return bytes2HexStr(bytes, false);
    }
    
    /** byte[] -> 十六进制字符串 */
    public final static String bytes2HexStr(byte[] bytes, boolean capital)
    {
        StringBuilder sb = new StringBuilder();
        
        for(byte b : bytes)
            sb.append(byte2Hex(b, capital));
        
        return sb.toString();
    }

    /** byte -> 十六进制双字符 (小写) */
    public final static char[] byte2Hex(byte b)
    {
        return byte2Hex(b, false);
    }

    /** byte -> 十六进制双字符 */
    public final static char[] byte2Hex(byte b, boolean capital)
    {
        byte bh    = (byte)(b >>> 4 & 0xF);
        byte bl    = (byte)(b & 0xF);

        return new char[] {halfByte2Hex(bh, capital), halfByte2Hex(bl, capital)};
    }
    
    /** 半 byte -> 十六进制单字符 (小写) */
    public final static char halfByte2Hex(byte b)
    {
        return halfByte2Hex(b, false);
    }
    
    /** 半 byte -> 十六进制单字符 */
    public final static char halfByte2Hex(byte b, boolean capital)
    {
        return (char)(b <= 9 ? b + '0' : (capital ? b + 'A' - 0xA : b + 'a' - 0xA));
    }
    
    /** 十六进制字符串 -> byte[] */
    public final static byte[] hexStr2Bytes(String str)
    {
        int length = str.length();
        
        if(length % 2 != 0)
        {
            str = "0" + str;
            length = str.length();
        }
        
        byte[] bytes = new byte[length / 2];
        
        for(int i = 0; i < bytes.length; i++)
            bytes[i] = hex2Byte(str.charAt(2 * i), str.charAt(2 * i + 1));
        
        return bytes;
    }

    /** 十六进制双字符 -> byte */
    public final static byte hex2Byte(char ch, char cl)
    {
        byte bh    = hex2HalfByte(ch);
        byte bl    = hex2HalfByte(cl);
        
        return (byte)((bh << 4) + bl);
    }
    
    /** 十六进制单字符 -> 半 byte */
    public final static byte hex2HalfByte(char c)
    {
        return (byte)(c <= '9' ? c - '0' : (c <= 'F' ? c - 'A' + 0xA : c - 'a' + 0xA));
    }
    
    /** 使用默认字符集对字符串编码后再进行 MD5 加密 */
    public final static String md5(String input)
    {
        return md5(input, null);
    }
    
    /** 使用指定字符集对字符串编码后再进行 MD5 加密 */
    public final static String md5(String input, String charset)
    {
        return encode(getMd5Digest(), input, charset);
    }
    
    /** MD5 加密 */
    public final static byte[] md5(byte[] input)
    {
        MessageDigest algorithm = getMd5Digest();
        return encode(algorithm, input);
    }
    
    /** 使用默认字符集对字符串编码后再进行 SHA 加密 */
    public final static String sha(String input)
    {
        return sha(input, null);
    }
    
    /** 使用指定字符集对字符串编码后再进行 SHA 加密 */
    public final static String sha(String input, String charset)
    {
        return encode(getShaDigest(), input, charset);
    }
    
    /** 使用默认字符集对字符串编码后再进行 SHA-{X} 加密，其中 {X} 由 version 参数指定 */
    public final static String sha(String input, int version)
    {
        return sha(input, null, version);
    }
    
    /** 使用指定字符集对字符串编码后再进行 SHA-{X} 加密，其中 {X} 由 version 参数指定 */
    public final static String sha(String input, String charset, int version)
    {
        return encode(getShaDigest(version), input, charset);
    }
    
    /** SHA加密 */
    public final static byte[] sha(byte[] input)
    {
        MessageDigest algorithm = getShaDigest();
        return encode(algorithm, input);
    }
    
    /** SHA-{X} 加密，其中 {X} 由 version 参数指定 */
    public final static byte[] sha(byte[] input, int version)
    {
        MessageDigest algorithm = getShaDigest(version);
        return encode(algorithm, input);
    }
    
    /** 使用指定算法对字符串加密 */
    public final static String encode(MessageDigest algorithm, String input)
    {
        return encode(algorithm, input, null);
    }
    
    /** 使用指定字符集对字符串编码后再进行 SHA-{X} 加密，字符串的编码由 charset 参数指定 */
    public final static String encode(MessageDigest algorithm, String input, String charset)
    {
        try
        {
            byte[] bytes    = input.getBytes(safeCharset(charset));
            byte[] output    = encode(algorithm, bytes);
            
            return bytes2HexStr(output);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /** 使用指定算法对 byte[] 加密 */
    public final static byte[] encode(MessageDigest algorithm, byte[] input)
    {
        return algorithm.digest(input);
    }
    
    /** 获取 MD5 加密摘要对象 */
    public final static MessageDigest getMd5Digest()
    {
        return getDigest(MD5);
    }
    
    /** 获取 SHA 加密摘要对象 */
    public final static MessageDigest getShaDigest()
    {
        return getDigest(SHA);
    }
    
    /** 获取 SHA-1 加密摘要对象 */
    public final static MessageDigest getSha1Digest()
    {
        return getDigest(SHA_1);
    }
    
    /** 获取 SHA-256 加密摘要对象 */
    public final static MessageDigest getSha256Digest()
    {
        return getDigest(SHA_256);
    }
    
    /** 获取 SHA-{X} 加密摘要对象，其中 {X} 由 version 参数指定 */
    public final static MessageDigest getShaDigest(int version)
    {
        String algorithm = String.format("%s-%d", SHA, version);
        return getDigest(algorithm);
    }
    
    /** 根据加密方法名称获取加密摘要对象 */
    public final static MessageDigest getDigest(String algorithm)
    {
        try
        {
            return MessageDigest.getInstance(algorithm);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 根据加密方法名称和提供者获取加密摘要对象 */
    public final static MessageDigest getDigest(String algorithm, String provider)
    {
        try
        {
            return MessageDigest.getInstance(algorithm, provider);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch(NoSuchProviderException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 根据加密方法名称和提供者获取加密摘要对象 */
    public final static MessageDigest getDigest(String algorithm, Provider provider)
    {
        try
        {
            return MessageDigest.getInstance(algorithm, provider);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** URL编码 （使用默认字符集） */
    public final static String urlEncode(String url)
    {
        return urlEncode(url, null);
    }
    
    /** URL编码 （使用指定字符集） */
    public final static String urlEncode(String url, String charset)
    {
        try
        {
            return URLEncoder.encode(url, safeCharset(charset));
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** URL解码 （使用默认字符集） */
    public final static String urlDecode(String url)
    {
        return urlDecode(url, null);
    }
    
    /** URL解码 （使用指定字符集） */
    public final static String urlDecode(String url, String enc)
    {
        try
        {
            return URLDecoder.decode(url, safeCharset(enc));
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 使用默认字符集对字符串编码后再进行 AES 加密 */
    public final static String aesEncrypt(String content, String password)
    {
        return aesEncrypt(content, null, password);
    }
    
    /** 使用指定字符集对字符串编码后再进行 AES 加密，字符串的编码由 charset 参数指定 */
    public final static String aesEncrypt(String content, String charset, String password)
    {
        return encrypt(AES, AES_KEY_SIZE, content, charset, password);
    }
    
    /** AES 加密 */
    public final static byte[] aesEncrypt(byte[] content, String password)
    {
        return crypt(AES, ENCRYPT_MODE, AES_KEY_SIZE, content, password);
    }
    
    /** AES 解密，并使用默认字符集生成解密后的字符串 */
    public final static String aesDecrypt(String content, String password)
    {
        return aesDecrypt(content, null, password);
    }
    
    /** AES 解密，并使用指定字符集生成解密后的字符串，字符串的编码由 charset 参数指定 */
    public final static String aesDecrypt(String content, String charset, String password)
    {
        return decrypt(AES, AES_KEY_SIZE, content, charset, password);
    }

    /** AES 解密 */
    public final static byte[] aesDecrypt(byte[] content, String password)
    {
        return crypt(AES, DECRYPT_MODE, AES_KEY_SIZE, content, password);
    }

    /** 使用默认字符集对字符串编码后再进行 DES 加密 */
    public final static String desEncrypt(String content, String password)
    {
        return desEncrypt(content, null, password);
    }
    
    /** 使用指定字符集对字符串编码后再进行 DES 加密，字符串的编码由 charset 参数指定 */
    public final static String desEncrypt(String content, String charset, String password)
    {
        return encrypt(DES, DES_KEY_SIZE, content, charset, password);
    }
    
    /** DES 加密 */
    public final static byte[] desEncrypt(byte[] content, String password)
    {
        return crypt(DES, ENCRYPT_MODE, DES_KEY_SIZE, content, password);
    }

    /** DES 解密，并使用默认字符集生成解密后的字符串 */
    public final static String desDecrypt(String content, String password)
    {
        return desDecrypt(content, null, password);
    }
    
    /** DES 解密，并使用指定字符集生成解密后的字符串，字符串的编码由 charset 参数指定 */
    public final static String desDecrypt(String content, String charset, String password)
    {
        return decrypt(DES, DES_KEY_SIZE, content, charset, password);
    }

    /** DES 解密 */
    public final static byte[] desDecrypt(byte[] content, String password)
    {
        return crypt(DES, DECRYPT_MODE, DES_KEY_SIZE, content, password);
    }

    /**
     * 加密字符串
     * 
     * @param method    ：加密方法（AES、DES）
     * @param keysize    ：密匙长度
     * @param content    ：要加密的内容
     * @param charset    ：加密内容的编码字符集
     * @param password    ：密码
     * @return            ：加解密结果
     * @throws GeneralSecurityException    加密失败抛出异常
     */
    public final static String encrypt(String method, int keysize, String content, String charset, String password)
    {
        try
        {
            byte[] bytes    = content.getBytes(safeCharset(charset));
            byte[] output    = crypt(method, ENCRYPT_MODE, keysize, bytes, password);
            
            return bytes2HexStr(output);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 解密字符串
     * 
     * @param method    ：解密方法（AES、DES）
     * @param keysize    ：密匙长度
     * @param content    ：要解密的内容
     * @param charset    ：解密结果的编码字符集
     * @param password    ：密码
     * @return            ：加解密结果
     * @throws GeneralSecurityException    解密失败抛出异常
     */
    public final static String decrypt(String method, int keysize, String content, String charset, String password)
    {
        try
        {
            byte[] bytes    = hexStr2Bytes(content);
            byte[] output    = crypt(method, DECRYPT_MODE, keysize, bytes, password);
            
            return new String(output, safeCharset(charset));
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 加/解密
     * 
     * @param method    ：加/解密方法（AES、DES）
     * @param mode        ：模式（加密/解密）
     * @param keysize    ：密匙长度
     * @param content    ：要加/解密的内容
     * @param password    ：密码
     * @return            ：加解密结果
     * @throws GeneralSecurityException    解密失败抛出异常
     */
    public final static byte[] crypt(String method, int mode, int keysize, byte[] content, String password)
    {
        try
        {
            KeyGenerator kgen    = KeyGenerator.getInstance(method);
            SecureRandom secure    = SecureRandom.getInstance(SEC_RAN_ALG);
            String seed            = GeneralHelper.safeString(password);
            
            secure.setSeed(seed.getBytes());
            kgen.init(keysize, secure);

            SecretKey secretKey    = kgen.generateKey();
            byte[] enCodeFormat    = secretKey.getEncoded();
            SecretKeySpec key    = new SecretKeySpec(enCodeFormat, method);
            Cipher cipher        = Cipher.getInstance(method);
            
            cipher.init(mode, key);
            return cipher.doFinal(content);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private final static String safeCharset(String charset)
    {
        if(GeneralHelper.isStrEmpty(charset))
            charset = DEFAULT_ENCODING;
        
        return charset;
    }
    
    public static String mysqlAESEncrypt(String content, String password)
    {
        return mysqlAESEncrypt(content, password, DEFAULT_ENCODING);
    }
    
    public static final String mysqlAESEncrypt(String content, String password, String charset)
    {
        try
        {
            charset  = safeCharset(charset);
            password = GeneralHelper.safeString(password);
            
            SecureRandom secure = new SecureRandom();
            secure.setSeed(password.getBytes());

            SecretKeySpec key = genMySQLAESKey(password, charset);
            byte[] bytes = content.getBytes(charset);

            Cipher cipher = Cipher.getInstance(MYSQL_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return bytes2HexStr(cipher.doFinal(bytes));
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static final String mysqlAESDecrypt(String content, String password)
    {
        return mysqlAESDecrypt(content, password, DEFAULT_ENCODING);
    }

    public static final String mysqlAESDecrypt(String content, String password, String charset)
    {
        try
        {
            charset  = safeCharset(charset);
            password = GeneralHelper.safeString(password);
                        
            SecureRandom secure = new SecureRandom();
            secure.setSeed(password.getBytes());

            SecretKeySpec key = genMySQLAESKey(password, charset);
            byte[] bytes = hexStr2Bytes(content);

            Cipher cipher = Cipher.getInstance(MYSQL_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(bytes), charset);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static final SecretKeySpec genMySQLAESKey(final String key, final String charset)
    {
        try
        {
            final byte[] finalKey = new byte[16];
            byte[] bytes = key.getBytes(charset);

            for(int i = 0; i < bytes.length; i++)
                finalKey[i % 16] ^= bytes[i];

            return new SecretKeySpec(finalKey, AES);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
