/**
 * A simple password Digestion mechanism
 */
package forum.server.domainlayer.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Freifeld Royi
 */
public class PasswordEncryptor
{
	/**
	 * Simple encryption mechanism. Uses MD5 encryption
	 * @param password - the password to be encrypted
	 * @return the encrypted password
	 * @throws NoSuchAlgorithmException
	 */
	public static String encryptMD5(String password)
	{
		try {
			return encrypt("MD5",password);
		}
		catch (NoSuchAlgorithmException e) { // this exception will never be throws - 
											 // there certainly exists MD5 algorithm
			return null;
		}
	}
	
	/**
	 * Simple encryption mechanism. Uses SHA encryption
	 * @param password - the password to be encrypted
	 * @return the encrypted password
	 * @throws NoSuchAlgorithmException
	 */
	public static String  encryptSHA (String password) throws NoSuchAlgorithmException
	{
		return encrypt("SHA",password);
	}
	
	/**
	 * Simple encryption mechanism.
	 * @param algorithm - The algorithm used. Use only MD5 or SHA!
	 * @param password - the password to be encrypted
	 * @return an encrypted password
	 * @throws NoSuchAlgorithmException is thrown in case the algorithm specified does not exist 
	 */
	private static String encrypt (String algorithm, String password) throws NoSuchAlgorithmException
	{
		String toReturn = "";
		MessageDigest tMD;
		byte[] tBytePassword = password.getBytes();
	
		tMD = MessageDigest.getInstance(algorithm);
		tMD.update(tBytePassword);
		tBytePassword = tMD.digest();
		
		for (int tIndex = 0 ; tIndex < tBytePassword.length ; ++tIndex)
		{
			toReturn += String.format("%02x",0xFF & tBytePassword[tIndex]);
		}
		
		return toReturn;
	}
}
