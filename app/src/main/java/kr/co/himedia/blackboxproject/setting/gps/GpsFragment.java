package kr.co.himedia.blackboxproject.setting.gps;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import kr.co.himedia.blackboxproject.R;

public class GpsFragment extends Fragment implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener{

    private static final String TAG ="GpsFragment";
    private static MapPoint currentMapPoint = null;
    private static double currentLat;
    private static double currentLng;
    private static String packageName = null;
    MapView mapView;
    NavigationBarView gpsBottomBar;
    MapPOIItem lastMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_gps, container, false);
        packageName = getActivity().getPackageName();

        MapView mapView = new MapView(getActivity());

        ViewGroup mapViewContainer = view.findViewById(R.id.mapViewActivity);
        gpsBottomBar = view.findViewById(R.id.gpsBottomBarActivity);
        mapViewContainer.addView(mapView);
        mapView.setMapType(MapView.MapType.Standard);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setShowCurrentLocationMarker(true);

        //기록된 마커 위치가 존재하면 실행
        readSavedMarker(mapView, packageName);

        mapView.fitMapViewAreaToShowAllPOIItems();

        // bottomTap
        gpsBottomBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tabAddMarker:
                    currentMapPoint = mapView.getMapCenterPoint();
                    //이전 마커 제거
                    try {
                        MapPOIItem findMarker = mapView.findPOIItemByTag(0);
                        if (findMarker != null) mapView.removePOIItem(findMarker);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    MapPOIItem marker = new MapPOIItem();
                    createMarker(mapView, marker,currentMapPoint);  //마커 생성
                    writeLastMarkerPos(view, marker, packageName);  //마커 위치 저장
                    break;

                    //화면의 마커 제거
                case R.id.tabRemoveMarker:
                    try {
                        MapPOIItem findMarker = mapView.findPOIItemByTag(0);
                        if (findMarker != null) mapView.removePOIItem(findMarker);
                    } catch (NullPointerException e) {e.printStackTrace();}
                    break;

                    //드래그로 이동한 마커의 위치 저장
                case R.id.tabSaveMarker:
                    try{
                    MapPOIItem mapPOIItem = mapView.findPOIItemByTag(0);
                    writeLastMarkerPos(view, mapPOIItem, packageName);
                    if(mapPOIItem==null){
                        Snackbar.make(view,"마커가 없습니다.\n 마커를 생성해 주세요.",Snackbar.LENGTH_SHORT).show();
                    }
                    } catch (NullPointerException e){e.printStackTrace();}
                    break;
            }
                    return false;
        });
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readLastMarkerPos(packageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        readLastMarkerPos(packageName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    //저장된 마커의 좌표 불러와서 마커 생성
    private void readSavedMarker(MapView mapView, String packageName) {
        if(readLastMarkerPos(packageName)!=null){
            try {
                lastMarker = new MapPOIItem();
                double dLastLat = readLastMarkerPos(packageName).latitude;
                double dLastLng = readLastMarkerPos(packageName).longitude;
                MapPoint lastMapPoint = MapPoint.mapPointWithGeoCoord(dLastLat,dLastLng);
                createMarker(mapView, lastMarker, lastMapPoint);

            }catch (NullPointerException e) {e.printStackTrace();}
        }

    }

    //마커 생성
    public void createMarker(MapView mapView, MapPOIItem marker, MapPoint mapPoint){
        marker.setItemName("기기 위치");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        marker.setDraggable(true);
        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
        marker.setShowCalloutBalloonOnTouch(true);
        Log.d(TAG, "createMarker: "+ marker.getTag());
        mapView.addPOIItem(marker);
    }


    //위치 정보가 변경될 때 호출
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        Log.d(TAG,"onCurrentLocationUpdate() start");
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
        currentLat = mapPointGeo.latitude;
        currentLng = mapPointGeo.longitude;
        Log.d(TAG,"position : "+currentLat+", "+currentLng);
    }

    //마커의 위치를 property 로 저장
    private static void writeLastMarkerPos(View v, MapPOIItem marker, String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName,"lastpos.properties");
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Properties prop = new Properties();

            prop.setProperty("lastLat", Double.toString(marker.getMapPoint().getMapPointGeoCoord().latitude));
            prop.setProperty("lastLng", Double.toString(marker.getMapPoint().getMapPointGeoCoord().longitude));

            prop.store(fileOutputStream,"LastMarkerPoint Properties");
            Snackbar.make(v,"마커의 위치가 저장되었습니다.",Snackbar.LENGTH_SHORT).show();

        }catch (IOException eio){eio.printStackTrace();}
    }

    //property 로 저장된 마커의 위치를 불러옴.
    private static MapPoint.GeoCoordinate readLastMarkerPos(String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName, "lastpos.properties");
        Log.d(TAG,"readLastMarkerPos() path : "+file.getPath());
        double dLastLat = 0d;
        double dLastLng = 0d;

        if (!file.exists()) {
            Log.d(TAG,"readLastMarkerPos() file not exist");
            return null;
        }
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Properties prop = new Properties();
                prop.load(fileInputStream);
                dLastLat = Double.parseDouble(prop.getProperty("lastLat"));
                dLastLng = Double.parseDouble(prop.getProperty("lastLng"));

            } catch (IOException|NullPointerException e) {
                e.printStackTrace();
            }
                MapPoint.GeoCoordinate getPoint = new MapPoint.GeoCoordinate(dLastLat,dLastLng);

            return getPoint;
        }
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {}

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {}

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {}

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {}

}