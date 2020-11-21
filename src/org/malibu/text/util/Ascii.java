package org.malibu.text.util;

public class Ascii {
	
	  /**
	   * <p>Checks if the string contains only ASCII printable characters.</p>
	   * 
	   * <p><code>null</code> will return <code>false</code>.
	   * An empty String ("") will return <code>true</code>.</p>
	   * 
	   * <pre>
	   * StringUtils.isAsciiPrintable(null)     = false
	   * StringUtils.isAsciiPrintable("")       = true
	   * StringUtils.isAsciiPrintable(" ")      = true
	   * StringUtils.isAsciiPrintable("Ceki")   = true
	   * StringUtils.isAsciiPrintable("ab2c")   = true
	   * StringUtils.isAsciiPrintable("!ab-c~") = true
	   * StringUtils.isAsciiPrintable("\u0020") = true
	   * StringUtils.isAsciiPrintable("\u0021") = true
	   * StringUtils.isAsciiPrintable("\u007e") = true
	   * StringUtils.isAsciiPrintable("\u007f") = false
	   * StringUtils.isAsciiPrintable("Ceki G\u00fclc\u00fc") = false
	   * </pre>
	   *
	   * @param str the string to check, may be null
	   * @return <code>true</code> if every character is in the range
	   *  32 thru 126
	   * @since 2.1
	   */
		public static boolean isAsciiPrintable(String str) {
			if (str == null) {
				return false;
			}
			int sz = str.length();
			for (int i = 0; i < sz; i++) {
				if (isAsciiPrintable(str.charAt(i)) == false) {
					return false;
				}
			}
			return true;
		}
	  
	  /**
	   * <p>Checks whether the character is ASCII 7 bit printable.</p>
	   *
	   * <pre>
	   *   CharUtils.isAsciiPrintable('a')  = true
	   *   CharUtils.isAsciiPrintable('A')  = true
	   *   CharUtils.isAsciiPrintable('3')  = true
	   *   CharUtils.isAsciiPrintable('-')  = true
	   *   CharUtils.isAsciiPrintable('\n') = false
	   *   CharUtils.isAsciiPrintable('&copy;') = false
	   * </pre>
	   * 
	   * @param ch  the character to check
	   * @return true if between 32 and 126 inclusive
	   */
		public static boolean isAsciiPrintable(char ch) {
			return ch >= 32 && ch < 127;
		}

}
