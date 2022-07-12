package kr.co.himedia.blackboxproject.setting.connection;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class User {

    private String name;
    private String dns;
    private String piid;
    private String pipw;
    private String pisshport;
    private String streamport;
    private String streamid;
    private String streampw;
    private String webdav;
    private String webdavid;
    private String webdavpw;

    public String[] fieldArray = new String[]{name, dns, piid, pipw, pisshport,
            streamport, streamid, streampw, webdav, webdavid, webdavpw};

    public User() {
    }

    public User(String[] array) {
        for(int i=0; i< fieldArray.length; i++){
            fieldArray[i] = array[i];
        }
    }

    public User(String name, String dns, String piid, String pipw, String pisshport,
                String streamid, String streampw, String streamport,  String webdavid, String webdavpw, String webdav) {
        this.name = name;
        this.dns = dns;
        this.piid = piid;
        this.pipw = pipw;
        this.pisshport = pisshport;
        this.streamport = streamport;
        this.streamid = streamid;
        this.streampw = streampw;
        this.webdav = webdav;
        this.webdavid = webdavid;
        this.webdavpw = webdavpw;
    }

    public String[] getFieldArray() {
        return fieldArray;
    }

    public String getName() {
        return name;
    }

    public String getDns() {
        return dns;
    }

    public String getPiid() {
        return piid;
    }

    public String getPipw() {
        return pipw;
    }

    public String getPisshport() {
        return pisshport;
    }

    public String getStreamport() {
        return streamport;
    }

    public String getStreamid() {
        return streamid;
    }

    public String getStreampw() {
        return streampw;
    }

    public String getWebdav() {
        return webdav;
    }

    public String getWebdavid() {
        return webdavid;
    }

    public String getWebdavpw() {
        return webdavpw;
    }

    public void writeUserProperty(String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName,name.toLowerCase()+".properties");
        FileOutputStream fileOutputStream = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            Properties prop = new Properties();
            prop.setProperty("Name", name);
            prop.setProperty("Dns", dns);
            prop.setProperty("PiID", piid);
            prop.setProperty("PiPW", pipw);
            prop.setProperty("SshPort", pisshport);
            prop.setProperty("StreamID", streamid);
            prop.setProperty("StreamPW", streampw);
            prop.setProperty("StreamPort", streamport);
            prop.setProperty("WebDavID", webdavid);
            prop.setProperty("WebDavPW", webdavpw);
            prop.setProperty("WebDavPort", webdav);
            prop.store(fileOutputStream,"User Properties");
            Log.d("testPara","Write done");

        }catch (IOException eio){eio.printStackTrace();}
    }

    public boolean readUserProperty(String packageName, String userName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName,userName.toLowerCase()+".properties");
        Log.d("testParaFilDir","read User Dir : "+file.getPath());

        if (!file.exists()) return false;
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Properties prop = new Properties();
                prop.load(fileInputStream);
                name = prop.getProperty("Name");
                dns = prop.getProperty("Dns");
                piid = prop.getProperty("PiID");
                pipw = prop.getProperty("PiPW");
                pisshport = prop.getProperty("SshPort");
                streamid = prop.getProperty("StreamID");
                streampw = prop.getProperty("StreamPW");
                streamport = prop.getProperty("StreamPort");
                webdavid = prop.getProperty("WebDavID");
                webdavpw = prop.getProperty("WebDavPW");
                webdav = prop.getProperty("WebDavPort");
                Log.d("testPara", "redUserProperity() property read Done");
            } catch (IOException eio) {
                eio.printStackTrace();
            }
            return true;
        }
    }
}
