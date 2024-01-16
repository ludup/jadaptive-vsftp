package com.jadaptive.plugins.debrep;

public enum GPGValidity {
	UNKNOWN, INVALID, DISABLED, REVOKED, EXPIRED, NONE, UNDEFINED, NOT_VALID, MARGINAL_VALID, FULLY_VALID,
	ULTIMATELY_VALID, KNOWN_PRIVATE_PART, SPECIAL_VALIDITY, SIGNATURE_GOOD, SIGNATURE_BAD, NO_PUBLIC_KEY, ERROR;

	public String toCode() {
		switch (this) {
		case UNKNOWN:
			return "o";
		case INVALID:
			return "i";
		case DISABLED:
			return "d";
		case REVOKED:
			return "r";
		case EXPIRED:
			return "e";
		case NONE:
			return "-";
		case UNDEFINED:
			return "q";
		case NOT_VALID:
			return "n";
		case MARGINAL_VALID:
			return "m";
		case FULLY_VALID:
			return "f";
		case ULTIMATELY_VALID:
			return "u";
		case KNOWN_PRIVATE_PART:
			return "w";
		case SPECIAL_VALIDITY:
			return "s";
		default:
			return "%";
		}
	}

	public static GPGValidity fromCode(String code) {
		if (code.equals("o"))
			return GPGValidity.UNKNOWN;
		if (code.equals("i"))
			return GPGValidity.INVALID;
		if (code.equals("d"))
			return GPGValidity.DISABLED;
		if (code.equals("r"))
			return GPGValidity.REVOKED;
		if (code.equals("e"))
			return GPGValidity.EXPIRED;
		if (code.equals("-"))
			return GPGValidity.NONE;
		if (code.equals("q"))
			return GPGValidity.UNDEFINED;
		if (code.equals("n"))
			return GPGValidity.NOT_VALID;
		if (code.equals("m"))
			return GPGValidity.MARGINAL_VALID;
		if (code.equals("f"))
			return GPGValidity.FULLY_VALID;
		if (code.equals("u"))
			return GPGValidity.ULTIMATELY_VALID;
		if (code.equals("w"))
			return GPGValidity.KNOWN_PRIVATE_PART;
		if (code.equals("s"))
			return GPGValidity.SPECIAL_VALIDITY;
		throw new IllegalArgumentException(String.format("No such code %s", code));
	}
}
