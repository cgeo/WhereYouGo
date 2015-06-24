/*
 * Copyright 2014 biylda <biylda@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package menion.android.whereyougo.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LogWriter {
  static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  public static void log(String fname, String msg) {
    String loc = menion.android.whereyougo.utils.FileSystem.getRoot() + File.separator + fname;
    try {
      FileWriter fstream = new FileWriter(loc, true);
      PrintWriter out = new PrintWriter(fstream);
      out.println("" + dateFormat.format(new java.util.Date()) + " - " + msg);
      out.close();
    } catch (IOException e) {
    }
  }

  public static void log(String fname, Throwable ex) {
    String loc = menion.android.whereyougo.utils.FileSystem.getRoot() + File.separator + fname;
    try {
      FileWriter fstream = new FileWriter(loc, true);
      PrintWriter out = new PrintWriter(fstream);
      out.println("" + dateFormat.format(new java.util.Date()) + ":");
      ex.printStackTrace(out);
      out.close();
    } catch (IOException e) {
    }
  }
}
