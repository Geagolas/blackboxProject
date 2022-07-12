package kr.co.himedia.blackboxproject.setting.connection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import kr.co.himedia.blackboxproject.MainActivity;
import kr.co.himedia.blackboxproject.R;
import kr.co.himedia.blackboxproject.setting.SettingFragment;

public class AddUserFragment extends Fragment {
    private static final String TAG = "AddUserFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        String packageName = getActivity().getPackageName();

        EditText editTextUserName, editTextDNS, editTextPiID, editTextPiPW, editTextSshPort
                ,editTextStreamID, editTextStreamPW, editTextStreamPort
                ,editTextWebDAVID, editTextWebDAVPW, editTextWebDAVIDPort;

        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextDNS = view.findViewById(R.id.editTextDNS);
        editTextPiID = view.findViewById(R.id.editTextPiID);
        editTextPiPW = view.findViewById(R.id.editTextPiPW);
        editTextSshPort = view.findViewById(R.id.editTextSshPort);
        editTextStreamID = view.findViewById(R.id.editTextStreamID);
        editTextStreamPW = view.findViewById(R.id.editTextStreamPW);
        editTextStreamPort = view.findViewById(R.id.editTextStreamPort);
        editTextWebDAVID = view.findViewById(R.id.editTextWebDAVID);
        editTextWebDAVPW = view.findViewById(R.id.editTextWebDAVPW);
        editTextWebDAVIDPort = view.findViewById(R.id.editTextWebDAVIDPort);

        //property로 저장된 User 정보가 있으면 load
        if(readLastUser(packageName)!=null) {
            User setUser = new User();
            setUser.readUserProperty(packageName,readLastUser(packageName));
            editTextUserName.setText(setUser.getName());
            editTextDNS.setText(setUser.getDns());
            editTextPiID.setText(setUser.getPiid());
            editTextPiPW.setText(setUser.getPipw());
            editTextSshPort.setText(setUser.getPisshport());
            editTextStreamID.setText(setUser.getStreamid());
            editTextStreamPW.setText(setUser.getStreampw());
            editTextStreamPort.setText(setUser.getStreamport());
            editTextWebDAVID.setText(setUser.getWebdavid());
            editTextWebDAVPW.setText(setUser.getWebdavpw());
            editTextWebDAVIDPort.setText(setUser.getWebdav());
        }

        //test user 정보를 editText에 입력
        editTextUserName.setText("picamera");
        editTextDNS.setText("http://geagolas.iptime.org");
        editTextPiID.setText("picamera");
        editTextPiPW.setText("12345");
        editTextSshPort.setText("50022");
        editTextStreamID.setText("dev");
        editTextStreamPW.setText("dev1234");
        editTextStreamPort.setText("58081");
        editTextWebDAVID.setText("dev");
        editTextWebDAVPW.setText("dev1234");
        editTextWebDAVIDPort.setText("55080");

        //확인 탭을 누르면 MainActivity의 CurrentUser의 정보가 입력된 정보로 바뀌고
        //변경된 정보를 property로 저장
        NavigationBarView navigationBarView = view.findViewById(R.id.addUserBottomBar);
        navigationBarView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.tabAddUser){
                User newUser = new User(
                        editTextUserName.getText().toString(), editTextDNS.getText().toString(),editTextPiID.getText().toString()
                        ,editTextPiPW.getText().toString(), editTextSshPort.getText().toString()
                        ,editTextStreamID.getText().toString()
                        ,editTextStreamPW.getText().toString(), editTextStreamPort.getText().toString(), editTextWebDAVID.getText().toString()
                        ,editTextWebDAVPW.getText().toString(), editTextWebDAVIDPort.getText().toString());

                newUser.writeUserProperty(getActivity().getPackageName());
                writeLastUser(newUser,packageName);

                Toast.makeText(getActivity().getApplicationContext(), "유저 정보가 저장 되었습니다.", Toast.LENGTH_SHORT).show();
                MainActivity.currentUser = newUser;
                return true;
            }
            return false;
        });
        return view;
    }

    //property로 user정보를 저장하는 Methode
    private static void writeLastUser(User user, String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName,"lastuser.properties");

        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Properties prop = new Properties();
            prop.setProperty("lastUserName", user.getName());
            prop.store(fileOutputStream,"LastUser Properties");
            Log.d("testPara","writeLastUser() Last User Write done");
        }catch (IOException eio){eio.printStackTrace();}
    }

    //property로 저장된 User정보를 currentUser로 불러오는 Methode
    private static String readLastUser(String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName, "lastuser.properties");
        Log.d("testParaAU","readLastUser() path : "+file.getPath());
        String lastUserName = null;

        if (!file.exists()) {
            Log.d("testPataAU","readLastUser() file not exist");
            return null;
        }
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Properties prop = new Properties();
                prop.load(fileInputStream);
                lastUserName = prop.getProperty("lastUserName");
                Log.d("testPara", "readLastUser() property read Done : "+lastUserName);
            } catch (IOException eio) {
                eio.printStackTrace();
            }
            Log.d("testPataAU","Last User Name : "+lastUserName);
            return lastUserName;
        }
    }

    //property는 유저명에 따라 다른 파일로 저장되므로
    // 마지막으로 저장된 User의 Property file이 이름이 무엇인지 저장 하는 Property를 생성
    public static User getLastUser(String packageName){
        User user = new User();
        String lastUserName = readLastUser(packageName);
        if (lastUserName!=null) {
            user.readUserProperty(packageName, lastUserName);
            return user;
        }
        return null;
    }

}