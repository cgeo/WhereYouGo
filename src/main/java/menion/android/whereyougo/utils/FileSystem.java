/*
 * This file is part of WhereYouGo.
 * 
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
 */

package menion.android.whereyougo.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Locale;

import menion.android.whereyougo.MainApplication;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class FileSystem {

    private static final String TAG = "FileSystem";

    public static File[] getFiles(File dir, final String prefix, final String suffix) {
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                if (prefix == null && suffix == null)
                    return true;
                String filename = file.getName().toLowerCase(Locale.getDefault());
                if (prefix != null && !filename.startsWith(prefix))
                    return false;
                if (suffix != null && !filename.endsWith(suffix))
                    return false;
                return true;
            }
        };
        return getFiles(dir, fileFilter);
    }

    public static File[] getFiles(File dir, final String suffix) {
        return getFiles(dir, null, suffix);
    }

    public static File[] getFiles(File dir, FileFilter filter) {
        try {
            File[] files = dir.listFiles(filter);
            if (files != null)
                return files;
        } catch (Exception e) {
            Logger.e(TAG, "getFiles(), folder: " + dir);
        }
        return new File[0];
    }

    /**
     * Writes binary data into file
     *
     * @param file     file
     * @param data     binary data
     */
    public static synchronized void saveBytes(File file, byte[] data) {
        try {
            if (data.length == 0)
                return;
            new FileSystemDataWritter(file, data, -1);
        } catch (Exception e) {
            Logger.e(TAG, "saveBytes(" + file + "), e: " + e.toString());
        }
    }

    public static File findFile(File dir, String prefix, String suffix) {
        File file = new File(dir.getAbsolutePath() + File.separator + prefix + suffix);
        if (file.exists())
            return file;
        File[] files = FileSystem.getFiles(dir, prefix, suffix);
        if (files == null || files.length == 0) return null;
        return files[0];
    }

    public static boolean backupFile(File file) {
        try {
            if (file.length() > 0) {
                File backupFile = new File(file.getAbsolutePath() + ".bak");
                FileSystem.copyFile(file, backupFile);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void copyFile(File source, File dest) throws IOException {
        if (source.equals(dest)) {
            return;
        }
        if (!dest.exists()) {
            dest.createNewFile();
        }
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            if (sourceChannel != null) {
                try {
                    sourceChannel.close();
                } catch (IOException e) {
                }
            }
            if (destChannel != null) {
                try {
                    destChannel.close();
                } catch (IOException e) {
                }
            }
        }
        dest.setLastModified(source.lastModified());
    }
}
