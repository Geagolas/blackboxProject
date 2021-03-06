package kr.co.himedia.blackboxproject.setting.filemanager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import kr.co.himedia.blackboxproject.MainActivity;
import kr.co.himedia.blackboxproject.R;
import kr.co.himedia.blackboxproject.files.FileWebDav;
import kr.co.himedia.blackboxproject.files.VideoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FileManageFragment extends Fragment {
    private static final String TAG="FileManageFragment";

    public ArrayList<FileFM> fileFMArrayList = new ArrayList<>();
    public ArrayList<FileFM> selectedFileList = new ArrayList<>();
    private DownloadManager mDownload;
    private Long mDownloadQueueId;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayoutFM;

    private static final String dns = MainActivity.currentUser.getDns();
    private static final String webDavId = MainActivity.currentUser.getWebdavid();
    private static final String webDavPw = MainActivity.currentUser.getWebdavpw();
    private static final String webDavPort = MainActivity.currentUser.getWebdav();
    static String url = dns.replace("://","://"+webDavId+":"+webDavPw+"@")+":"+webDavPort+"/";
    static String outputPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS+"/motion")+"/";

    FileManager fileManager = new FileManager();
    RecyclerView recyclerView;

    @SuppressLint({"NotifyDataSetChanged", "NonConstantResourceId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_manage, container, false);
        mContext = getActivity().getApplicationContext();
        fileManager.getFileList();
        fileFMArrayList = fileManager.setArrayList();
        ToggleButton toggleButtonTime = view.findViewById(R.id.toggleBtnTime);
        ToggleButton toggleButtonSize = view.findViewById(R.id.toggleBtnSize);

        swipeRefreshLayoutFM = view.findViewById(R.id.swipeLayoutFM);
        recyclerView = view.findViewById(R.id.recyclerViewFM);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FileAdapterFM adapter = new FileAdapterFM(fileFMArrayList, getContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(((holder, view1, position) -> {
            FileFM item = adapter.getItem(position);
            Log.d(TAG,item.fileName+" selected");
            Log.d(TAG, "selected protection : "+item.protection);
            //selected file list??? ????????? ????????? ???????????? ????????? List?????? ??????            
            if(!selectedFileList.contains(item)){
                selectedFileList.add(item);
                item.selected = true;
                Log.d(TAG,item.fileName+" added");
            }else {
                item.selected = false;
                selectedFileList.remove(item);
                Log.d(TAG,item.fileName+" removed");
            }
            adapter.notifyItemChanged(position);
        }));
        
        swipeRefreshLayoutFM.setOnRefreshListener(()->{
            onCreate(savedInstanceState);
            adapter.notifyDataSetChanged();
            swipeRefreshLayoutFM.setRefreshing(false);
        });

        NavigationBarView navigationBarView = view.findViewById(R.id.filemangeBottomBar);
        navigationBarView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                //Protect ?????? ??????
                //SeletedFileList??? ?????? ????????? ???????????? FileManager?????? ?????? 
                case R.id.tabProtect:
                    for (FileFM fileFM : selectedFileList){
                        int changedPos = adapter.adapterFileList.indexOf(fileFM);
                        fileFM.protection = !fileFM.protection;
                        adapter.notifyItemChanged(changedPos);
                        Log.d(TAG, "isProtection : "+fileFM.fileName+fileFM.protection);
                    }
                    fileManager.setProtectFiles(selectedFileList);
                    Log.d(TAG, "protect selected size : "+selectedFileList.size());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(view,selectedFileList.size()+" ??? ????????? ?????? ????????? ?????? ???????????????.",Snackbar.LENGTH_SHORT).show();
                    return true;
                    
                    //download?????? ?????????
                //SelectedFileList??? ?????? ????????? ???????????? URLDownloading?????? URL??? ???????????? ??????.
                case R.id.tabDownload:
                    Log.d(TAG, "download selected size : "+selectedFileList.size());
                    for(FileFM fileFM : selectedFileList){
                        URLDownloading(Uri.parse(url+fileFM.fileName),fileFM.fileName);
                    }
                    Snackbar.make(view,selectedFileList.size()+" ?????? ????????? ???????????? ??????.",Snackbar.LENGTH_SHORT).show();
                    return true;
                
                    //Delete ?????? ?????????
                //SelectedFileList??? ?????? ????????? ???????????? FileManager??? ??????.
                case R.id.tabDelete:
//                    Snackbar.make(view,"????????? ???????????????.\n ????????? ??????????????????.",Snackbar.LENGTH_SHORT).show();
                    Log.d(TAG, "delete selected size: "+selectedFileList.size());
                    for(FileFM fileFM : selectedFileList){
                        adapter.notifyItemRemoved(adapter.adapterFileList.indexOf(fileFM));
                    }
                    fileManager.setDeleteFiles(selectedFileList);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(view,FileManager.deleteListSize + " ?????? ????????? ?????? ???????????????.",Snackbar.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
        toggleButtonTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Comparator<FileFM> desc = (o1, o2) -> o2.time.compareTo(o1.time);
                adapter.adapterFileList.sort(desc);
                adapter.notifyDataSetChanged();
            }else {
                Comparator<FileFM> asc = (o1, o2) -> o1.time.compareTo(o2.time);
                adapter.adapterFileList.sort(asc);
                adapter.notifyDataSetChanged();
            }
        });
        toggleButtonSize.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Comparator<FileFM> desc = (o1,o2)-> Long.compare(o2.fileSize,o1.fileSize);
                adapter.adapterFileList.sort(desc);
                adapter.notifyDataSetChanged();
            }else {
                Comparator<FileFM> asc = (o1, o2) -> Long.compare(o1.fileSize,o2.fileSize);
                adapter.adapterFileList.sort(asc);
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    //Fragment ????????? ?????????
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileManager.getFileList();
        fileFMArrayList = fileManager.setArrayList();
        Log.d(TAG,"FM fragment FMArraySize : "+fileFMArrayList.size());
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(downloadCompleteReceiver, completeFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(downloadCompleteReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    //Download tab ????????? ???????????? ?????? ??????????????? URL??? ?????? ??????
    //???????????? URL??? ???????????? URI??? download Receiver?????? Queue??? ??????
    private void URLDownloading(Uri downloadUri, String fileName){
        mContext = getActivity().getApplicationContext();
        if(mDownload == null){
            mDownload = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        File outputFile = new File(outputPath+"/"+fileName);
        if(!outputFile.getParentFile().exists()){
            outputFile.getParentFile().mkdir();
        }

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        for( Map.Entry<String, String> entry : VideoActivity.header.entrySet() ){
            request.addRequestHeader(entry.getKey(),entry.getValue());
            Log.d(TAG,entry.getKey()+" / "+entry.getValue());
        }
        List<String> pathSegmentList = downloadUri.getPathSegments();
        request.setTitle("???????????? ??????");
        request.setDestinationUri(Uri.fromFile(outputFile));
        request.setAllowedOverMetered(true);

        mDownloadQueueId = mDownload.enqueue(request);
    }
    
    //?????? ?????? Queue??? Download Manager?????? Receiver??? ????????????
    private final BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            mContext = getActivity().getApplicationContext();

            if(mDownloadQueueId == reference){
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(reference);
                Cursor cursor = mDownload.query(query);

                cursor.moveToNext();

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                int status = cursor.getInt(columnIndex);
                int reason = cursor.getInt(columnReason);

                cursor.close();

                switch (status){
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Toast.makeText(mContext, "???????????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        Toast.makeText(mContext, "???????????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    case DownloadManager.STATUS_FAILED:
                        Toast.makeText(mContext, "???????????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

}