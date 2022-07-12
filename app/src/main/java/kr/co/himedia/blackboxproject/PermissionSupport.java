package kr.co.himedia.blackboxproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.ArrayList;

public class PermissionSupport {
    private Context context;
    private Activity activity;

    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private List<Object> permissionList;
    private final int MULTIPLE_PERMISSIONS = 666;

    public PermissionSupport(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }
    
    //permmision이 grant되어 있지 않은 List 생성
    public boolean checkPermissions(){
        int result;
        permissionList = new ArrayList<>();
        List<Object> checkedPermissionList = new ArrayList<>();

        for(String permission : permissions){
            result = ContextCompat.checkSelfPermission(context,permission);
            if (result != PackageManager.PERMISSION_GRANTED && !checkedPermissionList.contains(permission)){
                permissionList.add(permission);
            }
            checkedPermissionList.add(permission);
        }
        return permissionList.isEmpty();
    }
    
    //permission 요청
    public void requestPermission(){
        ActivityCompat.requestPermissions(
                activity,
                permissionList.toArray(new String[permissionList.size()]),
                MULTIPLE_PERMISSIONS);
    }
    
    //permission 결과를 확인
    public boolean permissionResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.length >0 )){
            for(int i=0; i<grantResults.length; i++){
                if(grantResults[i]==-1){
                    Log.d("testParaPermission", permissions[i] + " denied");
                    return false;
                }else if(grantResults[i]==0){
                    Log.d("testParaPermission", permissions[i] + " granted");
                }
            }
        }
        return true;
    }

    // permission check 할 activity 에서 호출할 method
    //    private void permissionCheck() {
//        permissionSupport = new PermissionSupport(this,this);
//        if(!permissionSupport.checkPermissions()){
//            permissionSupport.requestPermission();
//        }
//    }

}
