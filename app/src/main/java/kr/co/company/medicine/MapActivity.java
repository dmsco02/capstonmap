package kr.co.company.medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView;

public class MapActivity extends AppCompatActivity {
    SupportMapFragment smf;

    GoogleMap gmap;

    MarkerOptions mo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



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
            }
        });

        //지도 사용시 예전 스마트폰에서 오류가 발생할수 있으므로 예방차원에서 아래와 같은 초기화 작업을 해준다.
        MapsInitializer.initialize(MapActivity.this);

        //현재의 위치정보 얻어오기
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        //Ex93처럼 위험권한에 대해 명시적으로 체크하는 코드를 작성하라는 경고의 의미로 빨간줄이 생겼지만
        //이미 위험권한에 대한 작업처리를 완료하였으므로 아래의 빨간줄은 그냥 무시하면 됨.

        int perm = ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(perm == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0.0f, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    LatLng point = new LatLng(lat, lon);   //LatLng은 위도와 경도 위치값을 저장하는 벡터 자료형 클래스임.


                    //지도를 바라보는 카메라 정보(위치, 줌) 설정
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point, 15.0f);  //2번째 매개변수는 줌 배율.

                    //구글맵 객체에 카메라 정보(위치, 줌) 설정하기.
                    //즉, 카메라의 위치와 줌값으로 구글맵이 animate된다.
                    gmap.animateCamera(cu);

                    //아이콘으로 위치 표시하기.
                    //1. 구글맵의 카메라 위치 가운데에 아이콘 표시하기. (강좌에서는 onResume에서 이작업을 해줬으나
                    //    굳이 onResume에서 해줄 이유를 모르겠음. 이 위치에서 작업하는것이 보다 안정적임.)
                    if(gmap != null) {
                        gmap.setMyLocationEnabled(true);   //구글맵 자체의 기능으로서, 카메라 위치로 설정한 위치에 아이콘 표시를 해준다.
                    }
                    //2. 구글맵의 특정 위치에 아이콘 표시하기.
                    //Marker는 한번 추가하면 계속해서 지도에 표시가 되는 방식이므로 같은 위치의 동일한 Marker가 중복해서
                    //지도에 add되지 않도록 주의해야 한다. 따라서, 아래와 같이 if문을 사용해야만 한다.
                    if (mo == null) {
                        //지도상에 표시될 아이콘의 각종 정보(장소명, 아이콘 이미지파일, 위치등)를 설정한다.
                        mo = new MarkerOptions();
                        mo.title("약국");
                        mo.snippet("위치정보");
                        //icon 메소드의 매개변수형은 BitmapDescriptor형이므로 리소스의 id값을 곧바로 설정할수 없으므로
                        //BitmapDescriptorFactory 클래스의 fromResource 메소드를 사용하여 리소스의 id값을
                        //BitmapDescriptor형으로 변환하여 사용한다.
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                        mo.position(new LatLng(37.55894558798571, 127.04941379690118 ));

                        gmap.addMarker(mo); //동일한 Marker가 null인 경우에만 add해준다. (매우 중요)
                    } else {

                        //이미 동일한 Marker가 존재하는 경우에는 GPS 위치가 변함에 따라서 기존 Marker의 위치값만
                        //변경처리 해주면 된다. 그러면 기존의 Marker의 위치값만 갱신되어 지도에 표시된다.
                        //만약, if else문으로 처리하지 않고, 즉, Marker가 이미 생성되었는지 확인하지 않고
                        //Marker를 add하게 되면 위치가 갱신될때마다 Marker가 중복해서 지도상에 추가되므로 주의한다.
                        mo.position(new LatLng(37.55894558798571, 127.04941379690118));
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

        } else {
            Toast.makeText(MapActivity.this, "사용자로 부터 ACCESS_FINE_LOCATION 권한 승인을 받지 못함."
                    , Toast.LENGTH_LONG).show();
        }


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