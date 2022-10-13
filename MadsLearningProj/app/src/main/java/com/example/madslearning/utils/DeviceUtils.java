package com.example.madslearning.utils;

import android.os.Build;

import com.example.bitmapdemo.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DeviceUtils {
    private static int sCPUCount = -1;
    private static boolean sIsCpuCountInit = false;
    private static final FileFilter CPU_FILTER = pathname -> {
        String path = pathname.getName();
        if (path.startsWith("cpu")) {
            for(int i = 3; i < path.length(); ++i) {
                if (!Character.isDigit(path.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    };

    public static int getNumberOfCPUCores() {
        Runtime.getRuntime().availableProcessors();
        if (sIsCpuCountInit) {
            return sCPUCount;
        } else {
            if (Build.VERSION.SDK_INT <= 10) {
                sCPUCount = 1;
            } else {
                try {
                    sCPUCount = getCoresFromFileInfo("/sys/devices/system/cpu/possible");
                    if (sCPUCount == -1) {
                        sCPUCount = getCoresFromFileInfo("/sys/devices/system/cpu/present");
                    }

                    if (sCPUCount == -1) {
                        sCPUCount = getCoresFromCPUFileList();
                    }
                } catch (Exception var1) {
                }
            }

            sIsCpuCountInit = true;
            return sCPUCount;
        }
    }

    private static int getCoresFromFileInfo(String fileLocation) {
        InputStream is = null;
        BufferedReader buf = null;

        byte var4;
        try {
            is = new FileInputStream(fileLocation);
            buf = new BufferedReader(new InputStreamReader(is));
            String fileContents = buf.readLine();
            int var10 = getCoresFromFileString(fileContents);
            return var10;
        } catch (IOException var8) {
            var4 = -1;
        } finally {
            IOUtils.INSTANCE.closeQuietly(buf);
            IOUtils.INSTANCE.closeQuietly(is);
        }

        return var4;
    }

    private static int getCoresFromFileString(String str) {
        if (str != null && str.matches("0-[\\d]+$")) {
            int cores = Integer.valueOf(str.substring(2)) + 1;
            return cores;
        } else {
            return -1;
        }
    }

    private static int getCoresFromCPUFileList() {
        return (new File("/sys/devices/system/cpu/")).listFiles(CPU_FILTER).length;
    }
}
