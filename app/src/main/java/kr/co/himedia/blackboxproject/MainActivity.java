package kr.co.himedia.blackboxproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationBarView;


import kr.co.himedia.blackboxproject.files.FilesFragment;
import kr.co.himedia.blackboxproject.setting.SettingFragment;
import kr.co.himedia.blackboxproject.setting.connection.AddUserFragment;
import kr.co.himedia.blackboxproject.setting.connection.User;
import kr.co.himedia.blackboxproject.stream.StreamFragment;

public class MainActivity extends AppCompatActivity {
    MainFragment mainFragment;
    StreamFragment streamFragment;
    FilesFragment filesFragment;
    SettingFragment settingFragment;
    AddUserFragment addUserFragment;
    public static User currentUser = null;

    private PermissionSupport permissionSupport;
    NavigationBarView navigationBarView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streamFragment = new StreamFragment();
        filesFragment = new FilesFragment();
        settingFragment = new SettingFragment();
        mainFragment = new MainFragment();
        addUserFragment = new AddUserFragment();

        permissionCheck();
//        getAppKeyHash();

        //User 정보가 없으면 app이 정상 작동 하지 않으므로 user 등록화면으로 강제 이동
        currentUser = AddUserFragment.getLastUser(getPackageName());
        if(currentUser==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("사용자 정보가 없습니다.")
                    .setMessage("사용자 정보 입력창으로 이동합니다.")
                    .setPositiveButton("확인", (dialog, which) -> {
                        getSupportFragmentManager().beginTransaction().addToBackStack("adduser")
                                .replace(R.id.container, addUserFragment).commit();
                    });
            AlertDialog addUserAlertDialog = builder.create();
            addUserAlertDialog.show();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container,mainFragment).commit();

        navigationBarView = findViewById(R.id.mainBottomBar);
        navigationBarView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.tabHide:
                    return true;
                case R.id.tabStream:
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack("Stream").replace(R.id.container,streamFragment).commit();
                    return true;
                case R.id.tabFiles:
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack("Files").replace(R.id.container,filesFragment).commit();
                    return true;
                case R.id.tabSetting:
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack("setting").replace(R.id.container,settingFragment).commit();
                    return true;
            }
            return false;
        });
    }

    //permission 상태 확인
    private void permissionCheck() {
        permissionSupport = new PermissionSupport(this,this);
        if(!permissionSupport.checkPermissions()){
            permissionSupport.requestPermission();
        }
    }

    //grant 되어 있지 않은 permission을 requestPermission으로 전달
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!permissionSupport.permissionResult(requestCode, permissions, grantResults)){
            permissionSupport.requestPermission();
        }
    }

    //가로모드 일 경우 bottom tab을 보이지 않도록 변경
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            navigationBarView.setVisibility(View.INVISIBLE);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            navigationBarView.setVisibility(View.VISIBLE);
        }

    }
    //      kakaomap API KeyHash Generator
//    private void getAppKeyHash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key", something);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            Log.e("name not found", e.toString());
//        }
//    }

}