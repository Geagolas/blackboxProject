package kr.co.himedia.blackboxproject.setting.filemanager;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileFM{
    String fileSize;
    String fileName;
    LocalDateTime time;
    boolean protection = false;
    boolean selected = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FileFM(boolean protection, String fileSize, String fileName) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.protection = protection;
        String nameToDate = fileName.replace(".mkv","");
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss");
        this.time = LocalDateTime.parse(nameToDate,dateTimeFormatter);
    }

}
