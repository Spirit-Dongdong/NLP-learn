package dongdong.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


public class TxtUtil {

	public static String getFileContent(String path) {
		StringBuilder sb = new StringBuilder();
		String result = "";
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String line;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
					if (sb.length() > 20 * 1024 * 1024) {
						result = result.concat(sb.toString());
						sb = new StringBuilder();
					}
				}
				fr.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	
		
		return sb.toString();
	}
	
	public static void writeToFile(String path, String content, boolean append) {
		FileWriter fw;
		BufferedWriter bw;
		try {
			fw = new FileWriter(path, append);
			bw = new BufferedWriter(fw);
			
			
			bw.write(content);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		writeToFile("abc", "dongdong", true);
	}
}
