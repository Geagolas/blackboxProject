package kr.co.himedia.blackboxproject.files;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import kr.co.himedia.blackboxproject.R;

public class FileAdapterWebDav extends RecyclerView.Adapter<FileAdapterWebDav.FileViewHolder>
        implements OnFileListClickListener{

    private static final String TAG="FileAdapterWD";
    private Context context;
    public ArrayList<FileWebDav> adapterFileList = new ArrayList<>();
    OnFileListClickListener listener;

    public FileAdapterWebDav(ArrayList<FileWebDav> fileLists, Context context) {
        this.adapterFileList.addAll(fileLists);
        this.context = context;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filewebdavcardview,parent,false);
        return new FileViewHolder(itemView,this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileWebDav item = adapterFileList.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return adapterFileList.size();
    }

    @Override
    public void onItemClick(FileViewHolder holder, View view, int position) {
        if(listener!=null){
            listener.onItemClick(holder, view, position);
        }
    }

    public void setOnItemClickListener(OnFileListClickListener listener){
        this.listener = listener;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder{
        TextView textViewFile, textViewDate, textViewTime;

        public FileViewHolder(@NonNull View itemView, final OnFileListClickListener listener) {
            super(itemView);
            textViewFile = itemView.findViewById(R.id.textFile);
            textViewDate = itemView.findViewById(R.id.textDate);
            textViewTime = itemView.findViewById(R.id.textTime);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(listener!=null){
                    listener.onItemClick(FileViewHolder.this, v, position);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setItem(FileWebDav item){
            textViewFile.setText(item.fileName);
            textViewDate.setText(item.time.toLocalDate().toString());
            textViewTime.setText(item.time.toLocalTime().toString());
        }
    }


    public void addItem(FileWebDav item){
        adapterFileList.add(item);
    }

    public void setItems(ArrayList<FileWebDav> items){
        adapterFileList.addAll(items);
        Log.d("testParaAdapterFileList","items size : "+adapterFileList.size());
    }

    public FileWebDav getItem(int position){
        return adapterFileList.get(position);
    }

    public void setItem(int position, FileWebDav item){
        adapterFileList.set(position,item);
    }
}
