package kr.co.company.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.check).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    signUp();
                    break;
            }
        }
    };
    private void signUp(){
        String id=((EditText)findViewById(R.id.user_id)).getText().toString();
        String password=
                ((EditText)findViewById(R.id.user_password)).getText().toString();
        String passwordCheck=
                ((EditText)findViewById(R.id.user_password_check)).getText().toString();

        if(id.length()>0 && password.length()>0 && passwordCheck.length()>0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(id, password)
                        .addOnCompleteListener(this, new
                                OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult>
                                                                   task) {
                                        if (task.isSuccessful()) {

                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다." ,Toast.LENGTH_SHORT).show();
                                        } else {
                                            if(task.getException().toString() !=null){
                                                Toast.makeText(RegisterActivity.this,"회원가입에 실패했습니다." ,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
            }
            else{
                Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(RegisterActivity.this, "아아디와 비밀번호를 확인해주세요." ,Toast.LENGTH_SHORT).show();
        }
    }
}
