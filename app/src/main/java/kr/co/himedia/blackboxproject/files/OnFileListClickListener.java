package kr.co.himedia.blackboxproject.files;

import android.view.View;

public interface OnFileListClickListener {
    public void onItemClick(FileAdapterWebDav.FileViewHolder holder, View view, int position);
}
