package api.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;

import static java.lang.System.getenv;

@Service
public class RedditPasswordEncoder {	
	
	private static final int DEFAULT_ITERATIONS = 51504;
	private static final int DEFAULT_SALT_LENGTH = 16;
	private static final int DEFAULT_HASH_WIDTH = 256;
	private static final String ALGORITHM = getenv("PWD_ALGORITHM");
	private static final String SECRET = getenv("PWD_SECRET");
	
	
	/*
	 * ENCODE
	 */
	public String encode(CharSequence rawPassword) {
		byte[] salt = getSalt();
		byte[] encoded = encode(rawPassword, salt);
		return encode(encoded);
	}
	
	private String encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);	
	}
	
	private byte[] encode(CharSequence rawPassword, byte[] salt) {
		try {
			PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(),
					concatenate(SECRET.getBytes(),salt), DEFAULT_ITERATIONS, DEFAULT_HASH_WIDTH);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
			return concatenate(salt, skf.generateSecret(spec).getEncoded());
		}
		catch (GeneralSecurityException ex) {
			throw new IllegalStateException("Could not create hash", ex);
		}
	}
	
	/*
	 * DECODE & VERIFY
	 */
	
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		byte[] digested = decode(encodedPassword);
		byte[] salt = subArray(digested, 0, DEFAULT_SALT_LENGTH);
		return MessageDigest.isEqual(digested, encode(rawPassword, salt));
	}
	
	private byte[] decode(String encodedBytes) {
		return Base64.getDecoder().decode(encodedBytes);
	}
	
	/*
	 * UTILS
	 */
	
	private byte[] getSalt() {
        byte[] buffer = new byte[DEFAULT_SALT_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(buffer);
        return buffer;
    }
	
	private byte[] concatenate(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] newArray = new byte[length];
		int destPos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, newArray, destPos, array.length);
			destPos += array.length;
		}
		return newArray;
	}
	
	private byte[] subArray(byte[] array, int beginIndex, int endIndex) {
		int length = endIndex - beginIndex;
		byte[] subarray = new byte[length];
		System.arraycopy(array, beginIndex, subarray, 0, length);
		return subarray;
	}
}
