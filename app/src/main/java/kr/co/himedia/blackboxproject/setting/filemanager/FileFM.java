package kr.co.himedia.blackboxproject.setting.filemanager;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFM{
    private static final long KB = 1024L;
    private static final long MB = KB*1024L;
    private static final long GB = MB*1024L;

    String fileSizeHuman;
    String fileName;
    LocalDateTime time;
    long fileSize;
    boolean protection = false;
    boolean selected = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FileFM(boolean protection, long fileSize, String fileName) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.protection = protection;
        String nameToDate = fileName.replace(".mkv","");
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss");
        this.time = LocalDateTime.parse(nameToDate,dateTimeFormatter);
        this.fileSizeHuman = strFileSizeHuman(fileSize);
    }

    @SuppressLint("DefaultLocale")
    private String strFileSizeHuman(long fileSize) {
        double tmp = (double) fileSize;
        if(tmp>GB) {
            tmp/=GB;
            return String.format("%.1f",tmp) +"GB";
        }
        else if(tmp>MB) {
            tmp/=MB;
            return String.format("%.1f",tmp) + "MB";
        }
        else if(tmp>KB){
            tmp/=KB;
            return String.format("%.1f",tmp) + "KB";
        }
        return tmp+"";
    }
//    private Object[] fileSizeMatcher(String fileSize){
//        Object[] array = new Object[2];
//        Matcher m = Pattern.compile("([0-9.]+)([KMG])").matcher(fileSize);
//        array[0] = m.group(1);
//        array[1] = m.group(2);
//        return array;
//    }

//    private long fileSizeStrToLong(String fileSize) {
//        Object[] oArray = fileSizeMatcher(fileSize);
//        long lSize;
//        String strUnit;
//
//        lSize = (long) oArray[0];
//        strUnit = (String) oArray[1];
//
//        if(strUnit.contains("K")) lSize*=KB;
//        else if(strUnit.contains("M")) lSize*=MB;
//        else if(strUnit.contains("G")) lSize*=GB;
//        return lSize;
//    }

}
