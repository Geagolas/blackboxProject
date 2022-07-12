package kr.co.himedia.blackboxproject.setting.filemanager;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import kr.co.himedia.blackboxproject.R;
import java.lang.String;

public class FileAdapterFM extends RecyclerView.Adapter<FileAdapterFM.FileViewHolder> implements OnFileListClickListener{
    private final String TAG = "FileAdapterFM" ;

    private Context context;
    public ArrayList<FileFM> adapterFileList = new ArrayList<>();
    OnFileListClickListener listener;

    public FileAdapterFM(ArrayList<FileFM> fileList, Context context){
        this.adapterFileList.addAll(fileList);
        this.context = context;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filemanagecardview,parent,false);
        return new FileViewHolder(itemView,this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileFM item = adapterFileList.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return adapterFileList.size();
    }

    @Override
    public void onItemClick(FileViewHolder holder, View view, int position) {
        if(listener!=null){
            listener.onItemClick(holder,view,position);
        }
    }

    public void setOnItemClickListener(OnFileListClickListener listener){
        this.listener = listener;
    }

    static class FileViewHolder extends RecyclerView.ViewHolder{
        TextView textFileName, textDate, textTime, textSize;
        ImageView camera, protect;

        public FileViewHolder(@NonNull View itemView, final OnFileListClickListener listener) {
            super(itemView);
            textFileName = itemView.findViewById(R.id.textFileNameFM);
            textDate = itemView.findViewById(R.id.textDateFM);
            textTime = itemView.findViewById(R.id.textTimeFM);
            textSize = itemView.findViewById(R.id.textSizeFM);
            protect = itemView.findViewById(R.id.imageProtect);
            camera = itemView.findViewById(R.id.imageCamera);

            itemView.setOnClickListener(v->{
                int position = getAdapterPosition();
                if(listener!=null){
                    listener.onItemClick(FileViewHolder.this,v,position);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setItem(FileFM item){
            textFileName.setText(item.fileName);
            textDate.setText(item.time.toLocalDate().toString());
            textTime.setText(item.time.toLocalTime().toString());
            textSize.setText(item.fileSize+"B");

            if(item.protection) protect.setVisibility(View.VISIBLE);
            else protect.setVisibility(View.INVISIBLE);

            if(item.selected) camera.setImageResource(R.drawable.baseline_check_circle_black_48);
            else camera.setImageResource(R.drawable.baseline_video_camera_back_black_48);
        }
    }

    public void addItem(FileFM item){
        adapterFileList.add(item);
    }

    public void setItems(ArrayList<FileFM> items){
        adapterFileList = items;
    }

    public FileFM getItem(int position){
        return adapterFileList.get(position);
    }

    public void setItem(int position, FileFM item){
        adapterFileList.set(position,item);
    }

}
