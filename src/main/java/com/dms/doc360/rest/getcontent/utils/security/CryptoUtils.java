/**
 * 
 */
package com.dms.doc360.rest.getcontent.utils.security;

import java.io.Serializable;

import com.dms.doc360.rest.getcontent.utils.Doc360Constants;

/**
 * The utility class to use CloudSDK Crypto libraries.
 * 
 * @author Tarun Verma
 */
//@Component
//@Slf4j
public class CryptoUtils implements Doc360Constants, Serializable {
	private static final long serialVersionUID = 1L;

//	@Autowired
//	private StandardPBEStringEncryptor stringEncryptor;
//
//	@Autowired
//	private BouncyCastleProvider bcProvider;

	/**
	 * Encrypt the passed string using PBE encryption.
	 * 
	 * @param input
	 * @return Encrypted String
	 */
//	public String encrypt(String input) {
//		Provider provider = Security.getProvider(BOUNCY_CASTLE_PROVIDER_NAME);
//		if (provider == null) {
//			// add Bouncy Castle security provider if not already added
//			// in java.security policy file.
//			Security.addProvider(bcProvider);
//		}
//		return stringEncryptor.encrypt(input);
//	}

	/**
	 * Decrypt the passed string using PBE encryption.
	 * 
	 * @param input
	 * @return Decrypted String
	 */
//	public String decrypt(String input) {
//		Provider provider = Security.getProvider(BOUNCY_CASTLE_PROVIDER_NAME);
//		if (provider == null) {
//			// add Bouncy Castle security provider if not already added
//			// in java.security policy file.
//			Security.addProvider(bcProvider);
//		}
//		return stringEncryptor.decrypt(input);
//	}

	/**
	 * Main starting method.
	 * 
	 * @param args
	 */
//	public static void main(String[] args) {
//		// add Bouncy Castle security provider if not already added
//		// in java.security policy file.
//		Security.addProvider(new BouncyCastleProvider());
//
//		// list all the providers configured
//		Provider[] providers = Security.getProviders();
//		for (Provider provider : providers) {
//			System.out.println(provider.getName());
//		}
//
//		// create PBE and KBE crypto classes
//		Crypto cryptoPBE = CryptoFactory.getCrypto(CryptoType.PBE);
//		Crypto cryptoKBE = CryptoFactory.getCrypto(CryptoType.KBE);
//
//		// check for usage
//		if (args.length < 3) {
//			displayUsageMessage();
//		} else {
//			//
//			switch (args[0]) {
//			case "-pbe": {
//				switch (args[1]) {
//				case "-es":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoPBE.encryptString(args[2]));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ds":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoPBE.decryptString(args[2]));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-eb":
//					try {
//						validateParameters(args, 3);
//						// System.out.println(Arrays.toString(args[2].getBytes()));
//						System.out.println(Arrays.toString(cryptoPBE.encryptBytes(args[2].getBytes())));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-db":
//					try {
//						validateParameters(args, 3);
//						String[] byteValues = args[2].substring(1, args[2].length() - 1).split(",");
//						byte[] bytes = new byte[byteValues.length];
//
//						for (int i = 0, len = bytes.length; i < len; i++) {
//							bytes[i] = Byte.valueOf(byteValues[i].trim());
//						}
//
//						byte[] decryptedBytes = cryptoPBE.decryptBytes(bytes);
//						System.out.println(Arrays.toString(decryptedBytes));
//						System.out.println(new String(decryptedBytes));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ed":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoPBE.encryptBigDecimal(new BigDecimal(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-dd":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoPBE.decryptBigDecimal(new BigDecimal(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ei":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoPBE.encryptBigInteger(new BigInteger(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-di":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoPBE.decryptBigInteger(new BigInteger(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ef":
//
//					try {
//						validateParameters(args, 4);
//						cryptoPBE.encryptFile(args[2], args[3]);
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//
//					}
//					break;
//				case "-df":
//
//					try {
//						validateParameters(args, 4);
//						cryptoPBE.decryptFile(args[2], args[3]);
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				} // end args[1]
//				break;
//			}
//
//			case "-kbe": {
//				switch (args[1]) {
//				case "-es":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoKBE.encryptString(args[2]));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ds":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoKBE.decryptString(args[2]));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-eb":
//					try {
//						validateParameters(args, 3);
//						System.out.println(Arrays.toString(args[2].getBytes()));
//						System.out.println(Arrays.toString(cryptoKBE.encryptBytes(args[2].getBytes())));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-db":
//					try {
//						validateParameters(args, 3);
//						String[] byteValues = args[2].substring(1, args[2].length() - 1).split(",");
//						byte[] bytes = new byte[byteValues.length];
//
//						for (int i = 0, len = bytes.length; i < len; i++) {
//							bytes[i] = Byte.valueOf(byteValues[i].trim());
//						}
//
//						byte[] decryptedBytes = cryptoKBE.decryptBytes(bytes);
//						System.out.println(Arrays.toString(decryptedBytes));
//						System.out.println(new String(decryptedBytes));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ed":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoKBE.encryptBigDecimal(new BigDecimal(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-dd":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoKBE.decryptBigDecimal(new BigDecimal(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ei":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoKBE.encryptBigInteger(new BigInteger(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-di":
//					try {
//						validateParameters(args, 3);
//						System.out.println(cryptoKBE.decryptBigInteger(new BigInteger(args[2])));
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				case "-ef":
//
//					try {
//						validateParameters(args, 4);
//						cryptoKBE.encryptFile(args[2], args[3]);
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//
//					}
//					break;
//				case "-df":
//
//					try {
//						validateParameters(args, 4);
//						cryptoKBE.decryptFile(args[2], args[3]);
//					} catch (RuntimeException e) {
//						throw new CryptoException(e);
//					}
//					break;
//				}// end args[1]
//				break;
//			}
//			}
//		}
//	}
//
//	/**
//	 * Display usage message
//	 */
//	protected static void displayUsageMessage() {
//		System.out.println(" -------------------------------------------------------------------------------");
//		System.out.println(" ---------------------------  Crypto Utilities --------------------------------");
//		System.out.println(" -------------------------------------------------------------------------------");
//		System.out.println(" usage: <command> [<src>] [<dest>]");
//		System.out.println("<command> is one of the following:");
//		System.out.println("          '-pbe -es' - encrypt a string using password based encryption");
//		System.out.println("          '-pbe -ds' - decrypt a string using password based encryption");
//		System.out.println("          '-pbe -eb' - encrypt an array of bytes using password based encryption");
//		System.out.println("          '-pbe -db' - decrypt an array of bytes using password based encryption");
//		System.out.println("          '-pbe -ed' - encrypt a bigdecimal using password based encryption");
//		System.out.println("          '-pbe -dd' - decrypt a bigdecimal using password based encryption");
//		System.out.println("          '-pbe -ei' - encrypt a biginteger using password based encryption");
//		System.out.println("          '-pbe -di' - decrypt a biginteger using password based encryption");
//		System.out.println("          '-pbe -ef' - encrypt a file using password based encryption");
//		System.out.println("          '-pbe -df' - decrypt a file using password based encryption");
//		System.out.println("          '-kbe -es' - encrypt a string using key based encryption");
//		System.out.println("          '-kbe -ds' - decrypt a string using key based encryption");
//		System.out.println("          '-kbe -eb' - encrypt an array of bytes using key based encryption");
//		System.out.println("          '-kbe -db' - decrypt an array of bytes using key based encryption");
//		System.out.println("          '-kbe -ed' - encrypt a bigdecimal using key based encryption");
//		System.out.println("          '-kbe -dd' - decrypt a bigdecimal using key based encryption");
//		System.out.println("          '-kbe -ei' - encrypt a biginteger using key based encryption");
//		System.out.println("          '-kbe -di' - decrypt a biginteger using key based encryption");
//		System.out.println("          '-kbe -ef' - encrypt a file using key based encryption ");
//		System.out.println("          '-kbe -df' - decrypt a file using key based encryption");
//		System.out.println("<src> is a source file or a string");
//		System.out.println("<dest> is a destination file");
//		System.out.println("Argument examples:");
//		System.out.println("      to encrypt a string:-pbe  -es \"An apple a day keeps a doctor away\"");
//		System.out.println("      to decrypt a string:-pbe -ds \"encrypted value got from the -pbe -es command\"");
//
//		System.out.println("      to encrypt a byte array:-pbe -eb \"An apple a day keeps a doctor away\"");
//		System.out.println("      to decrypt a byte array:-pbe -db \"encrypted value got from the -pbe -eb command");
//
//		System.out.println("      to encrypt a big decimal :-pbe -ed 98765432123456789.9090");
//		System.out.println("      to decrypt a big decimal:-pbe -dd encrypted value got from the -pbe -ed command");
//
//		System.out.println("      to encrypt a big integer :-pbe -ei 98765432123456789");
//		System.out.println("      to decrypt a big integer:-pbe -di encrypted value got from the -pbe -ei command");
//
//		System.out.println("      for file encryption:-pbe -ef /path/file-origin /path/file-ecrypted");
//		System.out.println("      for file decryption:-pbe -df /path/file-ecrypted /path/file-decrypted");
//
//		System.out.println("      to encrypt a string:-kbe  -es \"An apple a day keeps a doctor away\"");
//		System.out.println("      to decrypt a string:-kbe -ds \"encrypted value got from the -kbe -es command\"");
//
//		System.out.println("      to encrypt a byte array:-kbe -eb \"An apple a day keeps a doctor away\"");
//		System.out.println("      to decrypt a byte array:-kbe -db \"encrypted value got from the -kbe -eb command");
//
//		System.out.println("      to encrypt a big decimal :-kbe -ed 98765432123456789.9090");
//		System.out.println("      to decrypt a big decimal:-kbe -dd encrypted value got from the -kbe -ed command");
//
//		System.out.println("      to encrypt a big integer :-kbe -ei 98765432123456789");
//		System.out.println("      to decrypt a big integer:-kbe -di encrypted value got from the -kbe -ei command");
//
//		System.out.println("      for file encryption:-kbe -ef /path/file-origin /path/file-ecrypted");
//		System.out.println("      for file decryption:-kbe -df /path/file-ecrypted /path/file-decrypted");
//
//		System.out.println("");
//	}

	/**
	 * Validate the parameters.
	 * 
	 * @param args
	 * @param expectedArguments
	 */
//	protected static void validateParameters(final String[] args, final int expectedArguments) {
//		if (args.length < expectedArguments) {
//			displayUsageMessage();
//			throw new RuntimeException("Missing expected parameters");
//		}
//	}
}
