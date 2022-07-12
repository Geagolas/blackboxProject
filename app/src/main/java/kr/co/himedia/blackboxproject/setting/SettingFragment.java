package kr.co.himedia.blackboxproject.setting;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.co.himedia.blackboxproject.R;
import kr.co.himedia.blackboxproject.setting.connection.AddUserFragment;
import kr.co.himedia.blackboxproject.setting.filemanager.FileManageFragment;
import kr.co.himedia.blackboxproject.setting.gps.GpsFragment;
import kr.co.himedia.blackboxproject.setting.status.StatusFragment;

public class SettingFragment extends Fragment {

    Button buttonConnect,buttonFileManage,buttonGPS,buttonStatus;
    FileManageFragment fileManageFragment;
    GpsFragment gpsFragment;
    StatusFragment statusFragment;
    AddUserFragment addUserFragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        buttonConnect = view.findViewById(R.id.btnConnect);
        buttonFileManage = view.findViewById(R.id.btnFileManage);
        buttonGPS = view.findViewById(R.id.btnGPS);
        buttonStatus = view.findViewById(R.id.btnStatus);

        fileManageFragment = new FileManageFragment();
        gpsFragment = new GpsFragment();
        statusFragment = new StatusFragment();
        addUserFragment = new AddUserFragment();

        buttonConnect.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack("conn").replace(R.id.container,addUserFragment).commit());
        buttonFileManage.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack("fileManage").replace(R.id.container,fileManageFragment).commit());
        buttonGPS.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack("gps").replace(R.id.container,gpsFragment).commit());
        buttonStatus.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack("status").replace(R.id.container,statusFragment).commit());
//
        return view;
    }

}