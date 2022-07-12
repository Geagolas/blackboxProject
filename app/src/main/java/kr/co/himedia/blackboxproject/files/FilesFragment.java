package kr.co.himedia.blackboxproject.files;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.himedia.blackboxproject.MainActivity;
import kr.co.himedia.blackboxproject.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FilesFragment extends Fragment {

    private static final String TAG="FileFragment";
    public ArrayList<FileWebDav> webdavLists = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayoutFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        swipeRefreshLayoutFiles = view.findViewById(R.id.swipeRefreshFiles);

        Log.d("testPara", "Frag webdav size : " + webdavLists.size());

        RecyclerView recyclerView = view.findViewById(R.id.fileRecyclerView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        FileAdapterWebDav adapter = new FileAdapterWebDav(webdavLists, getContext());

        Log.d("testParameter", Thread.currentThread() + "adapter size : " + adapter.adapterFileList.size());

        recyclerView.setAdapter(adapter);
        swipeRefreshLayoutFiles.setOnRefreshListener(() -> {
            onCreate(savedInstanceState);
            swipeRefreshLayoutFiles.setRefreshing(false);
        });

            //ItemClickListener
        adapter.setOnItemClickListener((holder, view1, position) -> {
            FileWebDav item = adapter.getItem(position);
            Intent intent = new Intent(getActivity().getApplicationContext(), VideoActivity.class).putExtra("videoURL",getWebdavUrl()+"/"+item.fileName);
                startActivity(intent);
            });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testPara","onCreate Start");
        Thread setFileThread = new Thread(this::setFileLists);
        Log.d("testPara","Thread Start : "+Thread.currentThread());
        try {
            setFileThread.join();
            setFileThread.start();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webdavLists.clear();
    }

    //webdav로 생성된 page에서 파일명을 parsing 하여 FileWebDav class를 생성하여 arraylist로 저장
    public void setFileLists(){
        new Thread("setFileListThread") {
            @Override
            public void run() {
                try {
                    Connection conn = Jsoup.connect(getWebdavUrl())
                            .method(Connection.Method.GET)
                            .timeout(600);
                    setConnection(conn);

                    Log.d("testPara","conn status : "+conn.request());

                    Document doc = conn.get();
                    Elements links = doc.select("a[href]");
                    for(Element href : links){
                        if(href.text().contains(".mkv")) {
                            webdavLists.add(new FileWebDav(href.text()));
                            Log.d("testParaJsoup",href.text());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Jsoup","IOException");
                }
                Log.d("testParameter",Thread.currentThread()+"setFileList size : "+webdavLists.size());
            }
        }.start();
    }

    //webdav 연결을 위한 정보를 입력한 user 정보에서 불러와서 String type 으로 준비
    private String getWebdavUrl(){
        String webdavHost = MainActivity.currentUser.getDns().replace("http://","");
        String webdavId = MainActivity.currentUser.getWebdavid();
        String webdavPw = MainActivity.currentUser.getWebdavpw();
        String webdavPort = MainActivity.currentUser.getWebdav();
        String webdavURL = "http://"+webdavId+":"+webdavPw+"@"+webdavHost+":"+webdavPort;
        Log.d("testParaDAV","get Webdavurl() : "+webdavURL);
        return webdavURL;
    }

    //Apache Webdav에 연결하기 위한 header 정보
    private static Connection setConnection(Connection connection){
        return connection.header("Host",MainActivity.currentUser.getDns().replace("http://","")+":"+MainActivity.currentUser.getWebdav())
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding","gzip, deflate")
                .header("Accept-Language", "ko")
                .header("Authorization","Basic ZGV2OmRldjEyMzQ=")
                .header("Cache-Control","max-age=0")
                .header("Connection","keep-alive")
                .header("Upgrade-Insecure-Requests","1")
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
    }
}
