package kr.co.himedia.blackboxproject.stream;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import kr.co.himedia.blackboxproject.MainActivity;
import kr.co.himedia.blackboxproject.R;

public class StreamFragment extends Fragment {
    WebView webView;
    WebSettings webSettings;
    ProgressDialog dialog;

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container,false);
        String userHost = MainActivity.currentUser.getDns();
        String userId = MainActivity.currentUser.getStreamid();
        String userPw = MainActivity.currentUser.getStreampw();
        String userStreamPort = MainActivity.currentUser.getStreamport();

        ProgressBar progressBar = view.findViewById(R.id.streamProgress);
        progressBar.setIndeterminate(true);
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("영상을 수신중 입니다.");

        webView = view.findViewById(R.id.webViewStream);
//        webView.setWebViewClient(new WebViewClient();
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); //wide viewport 사용 설정
        webSettings.setLoadWithOverviewMode(true); //컨텐츠가 웹뷰보다 클 경우 스크린 사이즈 맞춰 조정
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false); //zoom icon disable

        String streamUrl = "http://"+userId+":"+userPw+"@"+userHost.replace("http://","")+":"+userStreamPort;
        Log.d("testParaUrl",streamUrl);

        webView.loadUrl(streamUrl);
        dialog.show();
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d("testPara","stream progress : "+newProgress);
                if (newProgress>=31) dialog.dismiss();
            }
        });

        Log.d("testParaStream",webView.getUrl());

        return view;
    }
}