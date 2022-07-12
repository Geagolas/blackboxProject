package kr.co.himedia.blackboxproject.setting.filemanager;

import android.view.View;

public interface OnFileListClickListener {
    public void onItemClick(FileAdapterFM.FileViewHolder holder, View view, int position);
}
