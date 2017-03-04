package monolith52.test.util;

public class ValidateUtil {
	public static int parseInt(String str, int def) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
