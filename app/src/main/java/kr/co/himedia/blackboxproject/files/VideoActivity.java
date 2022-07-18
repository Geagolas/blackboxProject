package kr.co.himedia.blackboxproject.files;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import kr.co.himedia.blackboxproject.MainActivity;
import kr.co.himedia.blackboxproject.R;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG="VideoActivity";

    //Apache webdav에 연결하기 위한 header 정보
    public static Map<String, String> header = new HashMap<String, String>() {
        {
            put("Host", MainActivity.currentUser.getDns().replace("http://","")+":"+MainActivity.currentUser.getWebdav());
            put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            put("Accept-Encoding","gzip, deflate");
            put("Accept-Language", "ko,en;q=0.9,en-US;q=0.8,zh-CN;q=0.7,zh;q=0.6,ja;q=0.5");
            put("Authorization","Basic ZGV2OmRldjEyMzQ=");
            put("Cache-Control","max-age=0");
            put("Connection","keep-alive");
            put("Upgrade-Insecure-Requests","1");
            put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        Uri url = Uri.parse(getIntent().getStringExtra("videoURL"));
        videoView.setVideoURI(url);

        videoView.setVerticalScrollBarEnabled(true);
        videoView.setMediaController(new MediaController(this));

        //header 정보 입력
        try {
            Field field = VideoView.class.getDeclaredField("mHeaders");
            field.setAccessible(true);
            field.set(videoView,header);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //video 파일을 보기위한 Activity 에 Media Control 설정
        MediaController mc = new MediaController(VideoActivity.this);
        mc.setAnchorView(videoView);
        videoView.setMediaController(mc);
        videoView.start();

        Log.d(TAG,"video url : "+url.toString());
    }

}