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

        //property??? ????????? User ????????? ????????? load
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

      
        //?????? ?????? ????????? MainActivity??? CurrentUser??? ????????? ????????? ????????? ?????????
        //????????? ????????? property??? ??????
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

                Toast.makeText(getActivity().getApplicationContext(), "?????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                MainActivity.currentUser = newUser;
                return true;
            }
            return false;
        });
        return view;
    }

    //property??? user????????? ???????????? Methode
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
            Log.d(TAG,"writeLastUser() Last User Write done");
        }catch (IOException eio){eio.printStackTrace();}
    }

    //property??? ????????? User????????? currentUser??? ???????????? Methode
    private static String readLastUser(String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName, "lastuser.properties");
        Log.d(TAG,"readLastUser() path : "+file.getPath());
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
                Log.d(TAG, "readLastUser() property read Done : "+lastUserName);
            } catch (IOException eio) {
                eio.printStackTrace();
            }
            Log.d("testPataAU","Last User Name : "+lastUserName);
            return lastUserName;
        }
    }

    //property??? ???????????? ?????? ?????? ????????? ???????????????
    // ??????????????? ????????? User??? Property file??? ????????? ???????????? ?????? ?????? Property??? ??????
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
