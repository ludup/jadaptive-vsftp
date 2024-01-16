package com.jadaptive.plugins.debrep;

public enum GPGKeyAlgo {
	RSA_RSA, ELGAMAL, DSA_ELGAMAL, DSA_SIGN_ONLY, RSA_SIGN_ONLY;

	public static GPGKeyAlgo fromCode(int code) {
//		   ID           Algorithm
//		      --           ---------
//		      1          - RSA (Encrypt or Sign) [HAC]
//		      2          - RSA Encrypt-Only [HAC]
//		      3          - RSA Sign-Only [HAC]
//		      16         - Elgamal (Encrypt-Only) [ELGAMAL] [HAC]
//		      17         - DSA (Digital Signature Algorithm) [FIPS186] [HAC]
//		      18         - Reserved for Elliptic Curve
//		      19         - Reserved for ECDSA
//		      20         - Reserved (formerly Elgamal Encrypt or Sign)
//		      21         - Reserved for Diffie-Hellman (X9.42,
//		                   as defined for IETF-S/MIME)
//		      100 to 110 - Private/Experimental algorithm
		switch(code) {
		case 1:
			return RSA_RSA;
		case 16:
			return ELGAMAL;
		case 17:
			return DSA_ELGAMAL;
		case 3:
			return RSA_SIGN_ONLY;
		default:
			/* TODO */
			throw new IllegalArgumentException();
		}
	}
}
