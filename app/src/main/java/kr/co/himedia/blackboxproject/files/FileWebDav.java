package kr.co.himedia.blackboxproject.files;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileWebDav {
    private static final String TAG="FileWebDav";

    String fileName;
    LocalDateTime time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FileWebDav(String fileName) {
        this.fileName = fileName;
        String nameToDate = fileName.replace(".mkv","");
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss");
        this.time = LocalDateTime.parse(nameToDate,dateTimeFormatter);
    }
}
