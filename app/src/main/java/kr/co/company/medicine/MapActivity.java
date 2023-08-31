package kr.co.company.medicine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MapActivity extends AppCompatActivity implements GoogleMap.InfoWindowAdapter { //InfoWindowAdapter는 GoogleMap 클래스의 내부 인터페이스
    SupportMapFragment smf;

    GoogleMap gmap;

    MarkerOptions [] mo;

    private ArrayList<ArrayList<String>> mGroupList = null;
    private ArrayList<String> mChildList = null;

    Button bt;
    CameraUpdate cu;
    int perm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //지도 영역밖에 내위치로 이동하기 버튼 구성
        //setContentView(R.layout.activity_map);
        //지도 영역내에 내위치로 이동하기 버튼 구성
        setContentView(R.layout.activity_map2);


        bt = findViewById(R.id.button3);


        //약국 위치정보(phar_xy.xls) 읽어서 ArrayList로 만들기
        mGroupList = new ArrayList<ArrayList<String>>();

        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel sheet
            myInput = assetManager.open("phar_xy_phone_addr.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno =0;

            while (rowIter.hasNext()) {
                //Log.e("geem", " row no "+ rowno );
                HSSFRow myRow = (HSSFRow) rowIter.next(); //헤더(1번째줄) 건너뛰기
                if(rowno !=0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno =0;
                    String name="", lat="", lng="", phone="", addr="";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno==0){
                            name = myCell.toString();
                        }else if (colno==1){
                            lat = myCell.toString();
                        }else if (colno==2){
                            lng = myCell.toString();
                        }else if (colno==3){
                            phone = myCell.toString();
                        }else if (colno==4){
                            addr = myCell.toString();
                        }
                        colno++;
                        //Log.e("geem", " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }

                    mChildList = new ArrayList<String>();
                    mChildList.add(name);
                    mChildList.add(lat);
                    mChildList.add(lng);
                    mChildList.add(phone + "\n" + addr);
                    mGroupList.add(mChildList);
                }
                rowno++;
            }
        } catch (Exception e) {
            Log.e("geem", "error "+ e.toString());
        }


        /*
        //완성된 ArrayList<ArrayList<String>> 출력
        for(int i=0 ; i<mGroupList.size() ; i++){
            Log.d("array",  "" + i);

            for(int j=0 ; j<mChildList.size() ; j++){
                Log.d("array",  "" + mGroupList.get(i).get(j));
            }
        }
        */

        //약국 숫자만큼 mo 배열 만들기
        mo = new MarkerOptions[mGroupList.size()];







        //MapFragment는 뷰가 아니고 프래그먼트이므로 아래와 같은 방법으로 찾는다.
        smf = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        //MapFragment는 틀에 불과하며 지도 데이타(구글맵 객체)는 getMapAsync 메소드를 호출하여 구글맵 객체를 얻어와서 참조해야 프래그먼트에 지도가 표시되게 된다.
        smf.getMapAsync(new OnMapReadyCallback() {   //매개변수가 인터페이스이므로 익명 클래스 방식으로 진행한다.

            //구글에서 제공해주는 지도정보인 구글맵 객체를 해당 맵프래그먼트가 참조하도록 준비완료되면 onMapReady 콜백메소드가 호출되므로, 즉,
            //이제 해당 맵프래그먼트로 구글맵이 보여지게 되므로 이후의 작업처리를 여기서 해주면된다.
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //이 단계에서는 화면의 MapFragment에 구글맵 화면이 보여지게 되고 구글맵 객체를 사용할수 있으므로 매개변수로 전달된 구글맵 객체를 멤버변수로 저장시킨다.
                gmap = googleMap;

                //구글맵에 커스텀 마커뷰 적용
                gmap.setInfoWindowAdapter(MapActivity.this);

                //시작 위치 설정하는 방법
                LatLng sp = new LatLng(37.55894558798571, 127.04941379690118);
                CameraUpdate startPoint = CameraUpdateFactory.newLatLngZoom(sp,15.0f);
                gmap.animateCamera(startPoint);

                //약국 위치 마커 표시하기.
                for(int i=0 ; i<mGroupList.size() ; i++) { //약국 갯수만큼 반복
                    if (mo[i] == null) {
                        //Log.d("array",  "" + mGroupList.get(i).get(j));
                        //지도상에 표시될 아이콘의 각종 정보(장소명, 아이콘 이미지파일, 위치등)를 설정한다.
                        mo[i] = new MarkerOptions();
                        mo[i].title(mGroupList.get(i).get(0));
                        mo[i].snippet(mGroupList.get(i).get(3));
                        //icon 메소드의 매개변수형은 BitmapDescriptor형이므로 리소스의 id값을 곧바로 설정할수 없으므로
                        //BitmapDescriptorFactory 클래스의 fromResource 메소드를 사용하여 리소스의 id값을
                        //BitmapDescriptor형으로 변환하여 사용한다.
                        mo[i].icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2));

                        mo[i].position(new LatLng(Double.parseDouble(mGroupList.get(i).get(1)), Double.parseDouble(mGroupList.get(i).get(2))));

                        gmap.addMarker(mo[i]); //동일한 Marker가 null인 경우에만 add해준다. (매우 중요)
                    } else {
                        mo[i].position(new LatLng(Double.parseDouble(mGroupList.get(i).get(1)), Double.parseDouble(mGroupList.get(i).get(2))));
                    }
                }
            }
        });

        //지도 사용시 예전 스마트폰에서 오류가 발생할수 있으므로 예방차원에서 아래와 같은 초기화 작업을 해준다.
        MapsInitializer.initialize(MapActivity.this);


        //현재의 위치정보 얻어오기
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        perm = ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(perm == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Log.d("Hello", "NETWORK PROVIDER 콜백됨");

                    //현재의 내 위치 얻어오기
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    LatLng point = new LatLng(lat, lon);   //LatLng은 위도와 경도 위치값을 저장하는 벡터 자료형 클래스임.

                    //지도를 바라보는 카메라 정보(위치, 줌) 설정
                    cu = CameraUpdateFactory.newLatLngZoom(point, 15.0f);  //2번째 매개변수는 줌 배율.

                    //구글맵 객체에 카메라 정보(위치, 줌) 설정하기.
                    //즉, 카메라의 위치와 줌값으로 구글맵이 animate된다.
                    //gmap.animateCamera(cu);

                    //아이콘으로 위치 표시하기.
                    if (gmap != null) {
                        //이미 위험권한에 대한 작업처리를 완료하였으므로 아래의 빨간줄은 그냥 무시하면 됨.
                        gmap.setMyLocationEnabled(true);   //구글맵 자체의 기능으로서, 현재 내 위치에 아이콘 표시를 해준다.
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });



            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Log.d("Hello", "GPS PROVIDER 콜백됨");

                    //현재의 내 위치 얻어오기
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    LatLng point = new LatLng(lat, lon);   //LatLng은 위도와 경도 위치값을 저장하는 벡터 자료형 클래스임.

                    //지도를 바라보는 카메라 정보(위치, 줌) 설정
                    cu = CameraUpdateFactory.newLatLngZoom(point, 15.0f);  //2번째 매개변수는 줌 배율.

                    //구글맵 객체에 카메라 정보(위치, 줌) 설정하기.
                    //즉, 카메라의 위치와 줌값으로 구글맵이 animate된다.
                    //gmap.animateCamera(cu);

                    //아이콘으로 위치 표시하기.
                    if (gmap != null) {
                        //이미 위험권한에 대한 작업처리를 완료하였으므로 아래의 빨간줄은 그냥 무시하면 됨.
                        gmap.setMyLocationEnabled(true);   //구글맵 자체의 기능으로서, 현재 내 위치에 아이콘 표시를 해준다.
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        //버튼 클릭시 현재의 내위치로 이동하기.
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(perm == PackageManager.PERMISSION_GRANTED) {
                    Log.d("hihi", gmap + ", " + cu);
                    if(gmap != null && cu != null) {
                        gmap.animateCamera(cu);
                    }
                } else {
                    Toast.makeText(MapActivity.this, "권한을 승인하지 않았으므로 내위치로 이동할수 없음."
                            , Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

        //마커 뷰 레이아웃 구성하기.
        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);

        //마커 타이틀
        TextView title = new TextView(context);
        title.setTextColor(Color.RED);
        title.setTextSize(20.0f);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        //마커 스니펫
        TextView snippet = new TextView(context);
        snippet.setTextColor(Color.BLUE);
        snippet.setGravity(Gravity.CENTER);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        //완성된 마커 뷰 리턴
        return info;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }







    //지도상에 현재의 위치를 아이콘으로 표시해주기.
    //Activity가 백그라운드로 닫힐때
    @Override
    protected void onPause() {
        super.onPause();

        //이 콜백 메소드에서 굳이 아래와 같은 작업처리는 불필요해 보인다.
        /*
        if(gmap != null) {
            gmap.setMyLocationEnabled(false);   //
        }
        */

    }

    //Activity가 화면에 보이기 직전 호출.
    //앱을 실행하면 아직 위치정보를 받기전에 onResume이 먼저 호출되므로 아래와 같이 onResume에서 카메라 위치에 아이톤 표시 설정을
    //해줘도 지도화면에 아이콘이 표시되지 않음에 주의한다. 즉, 일단 위치정보를 받은후에 화면을 닫았다가 다시 띄워줘야만
    //그때부터 지도화면에 아이콘이 표시되기 시작한다.
    @Override
    protected void onResume() {
        super.onResume();

        if(gmap != null) {
            //gmap.setMyLocationEnabled(true);   //구글맵 자체의 기능으로서, 카메라 위치로 설정한 위치에 아이콘 표시를 해준다.
        }

    }
}