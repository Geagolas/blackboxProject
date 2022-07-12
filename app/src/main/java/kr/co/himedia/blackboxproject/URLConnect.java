//삭제 예정

package kr.co.himedia.blackboxproject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;

import java.util.Map;

public class URLConnect {
    private static String url;
    static RequestQueue requestQueue;
    static NetworkResponse networkResponse;

    public static String getUrl() {
        return url;
    }
    public static void setUrl(String url) {
        URLConnect.url = url;
    }

    public static void makeRequest(){
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> Log.d("urlRequest",": response"),
                error -> Log.d("urlRequest",": error")){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d("urlRequest",": request send.");
    }
}
