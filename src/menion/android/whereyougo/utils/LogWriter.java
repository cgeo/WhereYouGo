package menion.android.whereyougo.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LogWriter {
	static DateFormat dateFormat = 
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static void log(String fname, String msg){
		String loc = menion.android.whereyougo.utils.FileSystem.getRoot() + File.separator + fname;
		try {
			FileWriter fstream = new FileWriter(loc, true);
			PrintWriter out = new PrintWriter(fstream);
			out.println(""+dateFormat.format(new java.util.Date())+ " - " + msg);
			out.close();
		} catch (IOException e) {
		}
	}
	
	public static void log(String fname, Throwable ex){
		String loc = menion.android.whereyougo.utils.FileSystem.getRoot() + File.separator + fname;
		try {
			FileWriter fstream = new FileWriter(loc, true);
			PrintWriter out = new PrintWriter(fstream);
			out.println(""+dateFormat.format(new java.util.Date())+ ":");
			ex.printStackTrace(out);
			out.close();
		} catch (IOException e) {
		}
	}
}
