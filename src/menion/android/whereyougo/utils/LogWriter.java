package menion.android.whereyougo.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LogWriter {
	static DateFormat dateFormat = 
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static void log(String fname, String msg){
		String loc = menion.android.whereyougo.utils.FileSystem.getRoot() + File.separator+fname;
		try {
			FileWriter fstream;
			fstream = new FileWriter(loc, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(""+dateFormat.format(new java.util.Date())+ " - " + msg);
			out.newLine();
			out.close();
		} catch (IOException e) {
		}
	}
}
