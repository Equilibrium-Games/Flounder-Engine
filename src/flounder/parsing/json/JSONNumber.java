package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;

/**
 * A value of a number in a JSON file.
 */
public class JSONNumber extends JSONValue {
	private String value;

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(byte value) {
		this(Byte.toString(value));
	}

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(String value) {
		if (value == null) {
			throw new NullPointerException("Number cannot have a null value");
		}

		this.value = value;
	}

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(short value) {
		this(Short.toString(value));
	}

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(int value) {
		this(Integer.toString(value));
	}

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(long value) {
		this(Long.toString(value));
	}

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(float value) {
		this(Float.toString(value));
	}

	/**
	 * Creates a JSONNumber from a value.
	 *
	 * @param value The value representing the number.
	 */
	public JSONNumber(double value) {
		this(Double.toString(value));
	}

	/**
	 * Parses a value from a token source.
	 *
	 * @param tokens The tokens to parse.
	 * @param currentToken The current token of interest.
	 *
	 * @return A JSONValue parsed from the tokens.
	 *
	 * @throws IOException If a token cannot be read.
	 * @throws ParseException If the tokens cannot be parsed into a JSONValue.
	 */
	public static JSONValue parse(TokenReader tokens, String currentToken) throws IOException, ParseException {
		return new JSONNumber(currentToken);
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		JSONNumber other = (JSONNumber) obj;

		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(value);
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public int asInt() {
		return Integer.parseInt(value);
	}

	@Override
	public long asLong() {
		return Long.parseLong(value);
	}

	@Override
	public float asFloat() {
		return Float.parseFloat(value);
	}

	@Override
	public double asDouble() {
		return Double.parseDouble(value);
	}
}
