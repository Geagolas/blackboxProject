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
    boolean protection;
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
}
