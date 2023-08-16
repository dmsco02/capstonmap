package kr.co.company.medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public CalendarView calendarView;
    public TextView diaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("키해시는 :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

//// 권한ID를 가져옵니다
//        int permission = ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET);
//
//        int permission2 = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
//
//        int permission3 = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
//
//        // 권한이 열려있는지 확인
//        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
//            // 마쉬멜로우 이상버전부터 권한을 물어본다
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
//                requestPermissions(
//                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                        1000);
//            }
////            return;
//        }
        int permission2 = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission2 == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this,"이미 권한이 부여되어 있음.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(MainActivity.this,"권한을 설정해야함.", Toast.LENGTH_LONG).show();
            String[] need_perm = new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this, need_perm, 777);

        }


        Button Button_add = findViewById(R.id.button);
        TextView medView1 = findViewById(R.id.medView1);

        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MedSearch.class);
                startActivity(intent);
            }
        }); //임시 코드


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                diaryTextView.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));


                db.collection("alarm")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d("ssss", document.getId() + " => " + document.getData());
                                        medView1.setText(document.getData().get("allabel").toString());
                                    }
                                } else {
                                    Log.d("ssss", "Error getting documents.", task.getException());
                                }
                            }
                        });


            }
        });


        //지도보기 버튼 클릭시 지도화면(MapActivity.java)으로 이동하기
        Button mapBtn = findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 777) {
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"사용자가 권한을 승인함.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"사용자가 권한을 거부함.", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(MainActivity.this,"권한설정 여부에 대한 결과정보가 존재하지 않음.", Toast.LENGTH_LONG).show();
            }

        }
    }
}