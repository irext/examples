package net.irext.ircontrol.utils;

import android.os.Environment;
import android.util.Log;
import net.irext.ircontrol.IRApplication;

import java.io.*;

/**
 * Filename:       FileUtils.java
 * Revised:        Date: 2017-04-14
 * Revision:       Revision: 1.0
 * <p>
 * Description:    File operations
 * <p>
 * Revision log:
 * 2017-04-14: created by strawmanbobi
 */
@SuppressWarnings("unused")
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static final String packageDataDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            Environment.DIRECTORY_DOWNLOADS + File.separator + IRApplication.getApplicationName() + File.separator;
    public static final String binDir = packageDataDir + "bin" + File.separator;

    public static final String FILE_NAME_PREFIX = "irext_";
    public static final String FILE_NAME_EXT = ".ir";

    public static boolean write(File file, InputStream inputStream) {
        if (null == file) {
            return false;
        }

        if (null == inputStream) {
            return false;
        }
        FileOutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        int read;
        try {
            outputStream = new FileOutputStream(file);
            while ((read = inputStream.read(buffer, 0, 1024)) > 0) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static boolean createDirs(String path) {
        File file = new File(path);
        return file.exists() || file.mkdir();
    }

    public static boolean deleteFile(String path) {
        if (null == path) {
            return false;
        }
        File file = new File(path);
        boolean deleted = file.exists() && file.delete();
        if (!deleted) {
            Log.w(TAG, "failed to delete file : " + path);
        }
        return deleted;
    }

    public static File getLocalFile(String fileName) {
        File file = new File(binDir);
        if (file.exists()) {
            String path = binDir + fileName;
            file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    public static byte[] getByteArrayFromFile(String fileName) {
        File file;
        try {
            file = new File(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return null;
        }

        byte[] byteArray = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int count;
            byte[] buffer = new byte[512];
            while ((count = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, count);
            }
            byteArray = baos.toByteArray();
            fis.close();
            baos.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    private static void deleteAllFiles(File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                    try {
                        if(!f.delete()) {
                            Log.w(TAG, "failed to delete file");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (f.exists()) {
                        deleteAllFiles(f);
                        try {
                            if(!f.delete()) {
                                Log.w(TAG, "failed to delete file");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}