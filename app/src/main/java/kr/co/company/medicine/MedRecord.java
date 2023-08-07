package kr.co.company.medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedRecord extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    int i = 1; //pk
    FirebaseAuth firebaseAuth;


    @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_med_record);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mAuth = FirebaseAuth.getInstance();
                firebaseAuth = FirebaseAuth.getInstance();

//        -----------------------------------------------//


            EditText alname = findViewById(R.id.nameEdit);
            EditText aldays = findViewById(R.id.daysEdit);
            TextView timeText = findViewById(R.id.timeText);
            Button submitBtn = findViewById(R.id.submitBtn);
            LinearLayout timeLayout = findViewById(R.id.timeLayout);
            TimePicker timePicker = findViewById(R.id.timePicker);
            timePicker.setIs24HourView(false);

            timeText.setOnClickListener(new View.OnClickListener(){ // 시간지정 버튼
                @Override
                public void onClick(View v) {
                    timeLayout.setVisibility(View.VISIBLE);
                }
            });

            submitBtn.setOnClickListener(new View.OnClickListener() { // 등록 버튼이 눌리면
            @Override
            public void onClick(View v) {


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    String uid = user.getUid();
                }

                String user_mail =  mAuth.getCurrentUser().getEmail();

                // 복약주기 값 불러오기 ----//
                ChipGroup cycle = findViewById(R.id.cycle);
                List<Integer> cycle_a = cycle.getCheckedChipIds();
                StringBuilder cycle_sb = new StringBuilder();
//
                // 복약주기 값 StringBuilder에 저장  ----//
                for (int i = 0; i < cycle_a.size(); i++) {
                    Chip a = (Chip) cycle.getChildAt(cycle.indexOfChild(cycle.findViewById(cycle_a.get(i))));
                    String selectedText = a.getText().toString();
                    cycle_sb.append(selectedText);
                }
                String alarmcycle = cycle_sb.toString();


                // 복약시간 값 불러오기 ----//
                ChipGroup time = findViewById(R.id.time);
                List<Integer> time_a = time.getCheckedChipIds();
                StringBuilder time_sb = new StringBuilder();
//
                // 복약시간 값 StringBuilder에 저장  ----//
                for (int i = 0; i < time_a.size(); i++) {
                    Chip b = (Chip) time.getChildAt(time.indexOfChild(time.findViewById(time_a.get(i))));
                    String selectedText = b.getText().toString();
                    time_sb.append(selectedText);
                }
                String alarmtime = time_sb.toString();





                // DB에 저장  ----//

                Map<String, Object> alarm = new HashMap<>();
                alarm.put("user_id", user_mail);
                alarm.put("allabel", alname.getText().toString());
                alarm.put("alcycle", alarmcycle);
                alarm.put("altime", alarmtime);
                alarm.put("aldays", aldays.getText().toString());


                db.collection("alarm")
                        .add(alarm)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(MedRecord.this, "등록성공.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MedRecord.this, MainActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MedRecord.this, "등록실패.", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        });









    }
}