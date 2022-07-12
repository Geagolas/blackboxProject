package kr.co.himedia.blackboxproject.setting.status;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.himedia.blackboxproject.MainActivity;
import kr.co.himedia.blackboxproject.R;

public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";

    WebView webView;
    WebSettings webSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String statusUrl = MainActivity.currentUser.getDns();
        String statusPort = MainActivity.currentUser.getWebdav();
        String statusExtraUrl = "/app/Dashboard/";

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        webView = view.findViewById(R.id.webviewLinuxDash);
        webView.setWebViewClient(new WebViewClient());
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);

        webView.loadUrl(statusUrl+":"+statusPort+statusExtraUrl);
        return view;
    }
}