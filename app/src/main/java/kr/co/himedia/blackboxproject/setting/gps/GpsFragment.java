package kr.co.himedia.blackboxproject.setting.gps;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
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
    private static MapPoint markerMapPoint;
    private static String packageName = null;
    private static Context mContext;
//    private static View mView;
    MapView mapView;
    NavigationBarView gpsBottomBar;
    MapPOIItem lastMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_gps, container, false);
        packageName = getActivity().getPackageName();
        mContext = getContext();
//        mView = view;

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
        mapView.setPOIItemEventListener(
                new MapView.POIItemEventListener() {
                    @Override
                    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
//                        Log.d("testParaGPS","POI selected() called");
                        writeLastMarkerPos(mapPOIItem,packageName);
                        Toast.makeText(mContext, "마커 위치가 저장되었습니다.", Toast.LENGTH_SHORT).show();
//                        AlertDialog.Builder dBuilder = new AlertDialog.Builder(mContext)
//                                .setTitle("마커")
//                                .setMessage("무엇을 하시겠습니까/?")
//                                .setPositiveButton("마커 삭제",((dialog, which) -> {
//                                    mapView.removePOIItem(mapPOIItem);
//                                }))
//                                .setNegativeButton("마커 위치 기록",((dialog, which) -> {
//                                    writeLastMarkerPos(mapPOIItem,packageName);
//                                }))
//                                .setNeutralButton("취소",((dialog, which) -> dialog.dismiss()));
//                        AlertDialog markerRemoveDialog = dBuilder.create();
//                        markerRemoveDialog.show();
                    }

                    @Override
                    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {}
                    @Override
                    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
                    }
                    @Override
                    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
                        markerMapPoint = mapPoint;
                    }
                });
                mapView.setCalloutBalloonAdapter(new CalloutBalloonAdapter() {
                    @Override
                    public View getCalloutBalloon(MapPOIItem mapPOIItem) {
                        return null;
                    }

                    @Override
                    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
                        return null;
                    }
                });

        //기록된 마커 위치가 존재하면 실행
        if(readLastMarkerPos(packageName)!=null){
//            lastMarker = new POIItemCustom();
            lastMarker = new MapPOIItem();
            lastMarker.setItemName("기기 위치");
            lastMarker.setTag(0);

            Double dLastLat = readLastMarkerPos(packageName).latitude;
            Double dLastLng = readLastMarkerPos(packageName).longitude;
            MapPoint lastMapPoint = MapPoint.mapPointWithGeoCoord(dLastLat,dLastLng);

            lastMarker.setMapPoint(lastMapPoint);
            lastMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            lastMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            lastMarker.setDraggable(true);
            lastMarker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
            lastMarker.setShowCalloutBalloonOnTouch(true);
            mapView.addPOIItem(lastMarker);
        }
        mapView.fitMapViewAreaToShowAllPOIItems();

        // 마커 추가 탭을 클릭 할 때
        gpsBottomBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tabAddMarker:
//                    if (item.getItemId() == R.id.tabAddMarker) {
                        currentMapPoint = mapView.getMapCenterPoint();
//                Log.d("testParaGPS","current coord : "+currentMapPoint.getMapPointGeoCoord().latitude+", "+currentMapPoint.getMapPointGeoCoord().longitude);
                        try {
                            MapPOIItem findMarker = mapView.findPOIItemByTag(0);
                            if (findMarker != null) mapView.removePOIItem(findMarker);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

//                POIItemCustom marker = new POIItemCustom();
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName("기기 위치");
                        marker.setTag(0);
                        marker.setMapPoint(currentMapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                        marker.setDraggable(true);
                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                        marker.setShowCalloutBalloonOnTouch(true);
                        mapView.addPOIItem(marker);
                        writeLastMarkerPos(marker, packageName);
                        break;
//                        return true;
//                    }
//                    return false;

                case R.id.tabRemoveMarker:
                    try {
                        MapPOIItem findMarker = mapView.findPOIItemByTag(0);
                        if (findMarker != null) mapView.removePOIItem(findMarker);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
            }
                    return false;
                }
        );
        return view;

    }

//    static class OnMakerEventListener implements MapView.POIItemEventListener{
//        @Override
//        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
//            Log.d("testParaGPS","POI selected() called");
//            writeLastMarkerPos(mapPOIItem,packageName);
//            Toast.makeText(mContext, "마커 위치가 저장되었습니다.", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {}
//
//        @Override
//        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
//            Log.d("testParaGPS","balloon() called");
//            AlertDialog.Builder dBuilder = new AlertDialog.Builder(mContext)
//                    .setTitle("마커 삭제")
//                    .setMessage("선택한 마커를 삭제 하시겠습니까?")
//                    .setPositiveButton("네",((dialog, which) -> {
//                        mapView.removePOIItem(mapPOIItem);
//                    }))
//                    .setNegativeButton("아니오",((dialog, which) -> {}));
//            AlertDialog markerRemoveDialog = dBuilder.create();
//            markerRemoveDialog.show();
//        }
//
//        //마커가 이동하면 이동한 위치를 기록
//        @Override
//        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
//            writeLastMarkerPos(mapPOIItem,packageName);
//            Log.d("testParaGPS","onDraggablePOIItemMoved : "+mapPoint.getMapPointGeoCoord().latitude +", "+mapPoint.getMapPointGeoCoord().longitude);
//        }
//    }

//    static class CustomCalloutAdapter implements CalloutBalloonAdapter{
//
//        @Override
//        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
//            return null;
//        }
//
//        @Override
//        public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
//            return null;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    //위치 정보가 변경될 때 호출
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        Log.d("testParaGPS","onCurrentLocationUpdate() start");
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
        currentLat = mapPointGeo.latitude;
        currentLng = mapPointGeo.longitude;
        Log.d("testParaGPS","position : "+currentLat+", "+currentLng);
    }

    //마커의 위치를 property 로 저장
    private static void writeLastMarkerPos(MapPOIItem marker, String packageName){
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
            Log.d("testParaGPS","writeLastMarkerPos() marker"+marker.getMapPoint().getMapPointGeoCoord().latitude+" , "+marker.getMapPoint().getMapPointGeoCoord().longitude);
            Log.d("testParaGPS","writeLastMarkerPos() property"+prop.getProperty("lastLat")+" , "+prop.getProperty("lastLng"));
        }catch (IOException eio){eio.printStackTrace();}
    }

    //property 로 저장된 마커의 위치를 불러옴.
    private static MapPoint.GeoCoordinate readLastMarkerPos(String packageName){
        File file = new File(Environment.getDataDirectory()+"/data/"+packageName, "lastpos.properties");
        Log.d("testParaGPS","readLastMarkerPos() path : "+file.getPath());
        double dLastLat = 0d;
        double dLastLng = 0d;

        if (!file.exists()) {
            Log.d("testParaGPS","readLastMarkerPos() file not exist");
            return null;
        }
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Properties prop = new Properties();
                prop.load(fileInputStream);
                dLastLat = Double.parseDouble(prop.getProperty("lastLat"));
                dLastLng = Double.parseDouble(prop.getProperty("lastLng"));

            } catch (IOException eio) {
                eio.printStackTrace();
            }
                MapPoint.GeoCoordinate getPoint = new MapPoint.GeoCoordinate(dLastLat,dLastLng);
                Log.d("testParaGPS", "readLastUser() property read Done : "+getPoint.latitude+", "+getPoint.longitude);
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