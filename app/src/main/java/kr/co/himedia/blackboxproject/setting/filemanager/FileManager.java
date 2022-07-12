package kr.co.himedia.blackboxproject.setting.filemanager;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.himedia.blackboxproject.MainActivity;

public class FileManager {
    private static final String TAG="FileManager";

    private static final int tmpSize = 8 * 1024;
    private static String stringBuffer = new String();
    private static String HOST = MainActivity.currentUser.getDns().replace("http://","");
    private static String USER = MainActivity.currentUser.getPiid();
    private static String PASSWORD = MainActivity.currentUser.getPipw();
    private static String PI_SSH_PORT = MainActivity.currentUser.getPisshport();
    public static String FILETYPE = "mkv";
    private static final String SELECT_ALL_MOVIE_FILE = "*." + FILETYPE;
    private static final String PROTECT_FILE_CMD = "644";
    private static final String UNPROTECT_FILE_CMD = "664";

    private static String motionPath = "/home/" + USER + "/motion/";
    public ArrayList<FileFM> selectedFileList = new ArrayList<>();

    private static String lsCommand = "ls -lh " + motionPath + SELECT_ALL_MOVIE_FILE;
    private static String rmCommand = "";
    private static String chmodCommad = "";
    private static String strlsPattern = "([rwe-]{10})\\+?\\s+\\d+\\s+\\w+\\s+\\w+\\s+([0-9.]+\\w)\\s.*"+motionPath+"(\\w*\\.mkv)";
    private static String strHostIp = "";
    private static final String DELETE_CMD = "sudo rm -f ";
    private static final String CHMOD_CMD = "sudo chmod ";

    private static Session session = null;
    private static Channel channel = null;

    public static boolean isWorkDone = false;
    public static int deleteListSize = 0;

    // GetListSSH를 시작
    public void getFileList() {
        isWorkDone = false;
        getHostIP();
        GetListSSH getListSSH = new GetListSSH();
        getListSSH.start();
        try {
            getListSSH.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // GetListSSH에서 받은 String을 Matcher로 구분하여 FileFM class를 생성 하여 Arraylist에 저장하여 return
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<FileFM> setArrayList(){
        Matcher m = Pattern.compile(strlsPattern).matcher(stringBuffer);
        ArrayList<FileFM> arrayList = new ArrayList<>();
        while(m.find()){
            Log.d("testPara", "matcher result :"+m.group(1));
            boolean protect = (m.group(1).charAt(5)=='-') ? true : false;
            arrayList.add(new FileFM(protect, m.group(2), m.group(3)));
            Log.d("testPara", "protect : "+protect);
        }
        Log.d("testPara","Array size : "+arrayList.size());
        isWorkDone = true;
        return arrayList;
    }

    //selectedFileList로 받은 목록에서 보호 상태가 아닌 파일을 rmCommand로 생성하여 DeleteSSH로 전달
    public void setDeleteFiles(ArrayList<FileFM> arrayList){
        DeleteSSH deleteSSH = new DeleteSSH();
        rmCommand = DELETE_CMD;
        selectedFileList.clear();
        deleteListSize = 0;
        selectedFileList = arrayList;
        for (FileFM fileFM : selectedFileList){
            if(!fileFM.protection) rmCommand += motionPath+fileFM.fileName+" ";
            deleteListSize++;
        }
        Log.d("testPara",rmCommand);
        deleteSSH.start();
        try {
            deleteSSH.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (FileFM fileFM : selectedFileList){
            fileFM.selected = false;
        }
    }

    //SelectedFileList로 받은 파일목록에서 보호상태의 파일은 비보호로
    //비보호상태의 파일은 보호 상태로 변경하기 위한 cmd를 chmodCommand로 작성하여 protectSSH로 전달
    public void setProtectFiles(ArrayList<FileFM> arrayList){
        ProtectSSH protectSSH = new ProtectSSH();
        String strProtect = CHMOD_CMD+PROTECT_FILE_CMD+" ";
        String strUnProtect = CHMOD_CMD+UNPROTECT_FILE_CMD+" ";
        selectedFileList.clear();
        selectedFileList = arrayList;
        int[] cnt = {0,0};
        for(FileFM fileFM : selectedFileList) {
            Log.d("testPara", "before protection : "+fileFM.protection);
            if (fileFM.protection) {
                strProtect += motionPath + fileFM.fileName + " ";
                cnt[0]++;
            } else {
                strUnProtect += motionPath + fileFM.fileName + " ";
                cnt[1]++;
            }
            fileFM.protection = !fileFM.protection;
            Log.d("testPara", "after protection : "+fileFM.protection);
            Log.d("testPara", "strProtect : "+strProtect);
            Log.d("testPara", "strUnProtect : "+strUnProtect);
        }
        if(cnt[0]!=0 && cnt[1]!=0) chmodCommad = strProtect + " | " + strUnProtect;
        if(cnt[0]==0 && cnt[1]!=0) chmodCommad = strUnProtect;
        if(cnt[0]!=0 && cnt[1]==0) chmodCommad = strProtect;
        Log.d("testPara", "chmodCommand method() : "+chmodCommad);

        protectSSH.start();
        try {
            protectSSH.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (FileFM fileFM : selectedFileList){
            fileFM.selected = false;
        }
    }

    //ssh로 raspi에 접속하고 ls 명령어를 이용하여 저장되어 있는 동영상 fileList를 String으로 생성
    class GetListSSH extends Thread{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run(){
            try{
                Log.d("testPara","Conn Thread Start");
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jSch = new JSch();
                Log.d("testPara","GetListSSH session strHostIP: "+strHostIp);
                session = jSch.getSession(USER,strHostIp,Integer.parseInt(PI_SSH_PORT));
                Thread.sleep(300);
                Log.d("testPara","GetListSSH session : "+session.getUserName());
                Log.d("testPara","GetListSSH session : "+session.getHost()+":"+session.getPort());
                session.setPassword(PASSWORD);
                session.setConfig(config);
                session.connect();
                Log.d("testPara","session isConnected :"+session.isConnected());
                channel = session.openChannel("exec");

                ((ChannelExec)channel).setCommand(lsCommand);
                Log.d("testPara", "list Command : "+lsCommand);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream inputStream=channel.getInputStream();
                channel.connect();
                Log.d("testPara","channel isConnected :"+channel.isConnected());

                byte[] tmp = new byte[tmpSize];
                while (true){
                    while (inputStream.available()>0){
                        int i = inputStream.read(tmp,0,tmpSize);
                        if(i<0) break;
                        stringBuffer = new String(tmp,0,tmpSize);
                    }if(!channel.isConnected()){
                        Log.d("testPara","exit-status : "+channel.getExitStatus());
                        break;
                    }
                    Thread.sleep(300);
                    channel.disconnect();
                    Log.d("testPara","Exit channel is connected : "+channel.isConnected());
                    session.disconnect();
                    Log.d("testPara","Exit session is connected : "+session.isConnected());
                }
                Log.d("testPara","stringBuffer : "+stringBuffer);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    //생성된 rm 명령어를 SSH로 raspi에 접속하여 실행
    class DeleteSSH extends Thread{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run(){
            try{
                Log.d("testPara","delete Thread Start");
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jSch = new JSch();
                session = jSch.getSession(USER,strHostIp,Integer.parseInt("50022"));
                Thread.sleep(200);
                session.setPassword(PASSWORD);
                session.setConfig(config);
                session.connect();
                Log.d("testPara","session isConnected :"+session.isConnected());
                channel = session.openChannel("exec");

                ((ChannelExec)channel).setCommand(rmCommand);
                Log.d("testPara", "rm Command : "+rmCommand);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream inputStream=channel.getInputStream();
                channel.connect();
                Thread.sleep(200);
                Log.d("testPara","channel isConnected :"+channel.isConnected());

                channel.disconnect();
                Log.d("testPara","Exit channel is connected : "+channel.isConnected());
                session.disconnect();
                Log.d("testPara","Exit session is connected : "+session.isConnected());

            }catch (Exception e){e.printStackTrace();}
        }
    }

    //생성된 chmod 명령어를 SSH로 raspi에 접속하여 실행
    class ProtectSSH extends Thread{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run(){
            try{
                Log.d("testPara","chmod Thread Start");
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jSch = new JSch();
                session = jSch.getSession(USER,strHostIp,Integer.parseInt(PI_SSH_PORT));
                Thread.sleep(200);
                session.setPassword(PASSWORD);
                session.setConfig(config);
                session.connect();
                Log.d("testPara","session isConnected :"+session.isConnected());
                channel = session.openChannel("exec");

                ((ChannelExec)channel).setCommand(chmodCommad);
                Log.d("testPara", "chmod Command : "+chmodCommad);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream inputStream=channel.getInputStream();
                channel.connect();
                Thread.sleep(200);
                Log.d("testPara","channel isConnected :"+channel.isConnected());

                channel.disconnect();
                Log.d("testPara","Exit channel is connected : "+channel.isConnected());
                session.disconnect();
                Log.d("testPara","Exit session is connected : "+session.isConnected());

            }catch (Exception e){e.printStackTrace();}
        }
    }

    //JSCH의 경우 dns로는 접속이 불가능 하여 dns를 IP로 변경하는 Methode
    private void getHostIP(){
        Thread getHostIpThread = new Thread(()->{
            InetAddress inetAddress[] = null;
            try {
                 inetAddress = InetAddress.getAllByName(MainActivity.currentUser.getDns().replace("http://",""));
                Log.d("testPara","getHostIP() strHostIP: "+strHostIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            for(int i=0; i<inetAddress.length ; i++){
                strHostIp = inetAddress[i].getHostAddress();
            }
            Log.d("testParaHostIP",strHostIp);
        });
        getHostIpThread.start();

    }
}