
package io.github.hpsocket.test.data.mysql;

import com.alibaba.druid.filter.config.ConfigTools;

public class DruidPasswordEncryption
{
    private static final String DEFAULT_PRIVATE_KEY_STRING = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAlGEoY2vcgAlyzj7TKz/jObBQmgFioB3HoRzKJYgG3twgVLlB2u5qROYaTxc5U8xXP2M4s6+E9+MvOA+DBoqjpQIDAQABAkBFWDzcbingisnlWtYs9dA3g0/AEdqqcxB7mu1cafywBR/aIA/oSxYAqVP4m64kj1oFKuNp17z+lVWZ9rvPHa2HAiEAq/CZ6dIPCG04JV5D3aGnshKLeah76UzJtwz+eQTHmjMCIQDc69PNTDGpjMMPanuIMW0tYGODCtL/JkSM49Ssdn0ixwIgCmkG6KFPR7NVMu4CLekbvixhRXxuBDIiBHNE9Q7VBwECIEIlAadIFt5y3Lwy34WpdszNPT4w8XefV4rvc++nElRlAiAs6THhTAW1kenBSXhGn99gcaO9T8j7sJ3XmOU9qpqv2Q==";
    public static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJRhKGNr3IAJcs4+0ys/4zmwUJoBYqAdx6EcyiWIBt7cIFS5QdruakTmGk8XOVPMVz9jOLOvhPfjLzgPgwaKo6UCAwEAAQ==";

    public static void main(String[] args) throws Exception
	{
    	String password = args.length > 0 ? args[0] : "123456";
    	
        System.out.println("privateKey: " + DEFAULT_PRIVATE_KEY_STRING);
        System.out.println("publicKey: " + DEFAULT_PUBLIC_KEY_STRING);
        System.out.println();
        
        String encPassword = ConfigTools.encrypt(DEFAULT_PRIVATE_KEY_STRING, password);
        
        System.out.println("enc-password: " +  encPassword);
        System.out.println("dec-password: " +  ConfigTools.decrypt(DEFAULT_PUBLIC_KEY_STRING, encPassword));
	}
}
