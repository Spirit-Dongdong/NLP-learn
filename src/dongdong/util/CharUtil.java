package dongdong.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CharUtil {

	public static boolean isChinese(String keyword) {
		try {
			return keyword.getBytes("utf-8").length == keyword.length() * 3;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean allAscChar(String keyword) {
		return keyword.getBytes().length == keyword.length();
	}

}
