
package kr.co.company.medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView Resigettxt, LoginBtn;
    EditText EmailText, PasswordText;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        //버튼 등록하기
        Resigettxt = findViewById(R.id.register_btn);
        LoginBtn = findViewById(R.id.login_btn);

        Resigettxt.setOnClickListener(new View.OnClickListener(){ //가입 버튼이 눌리면
            @Override
            public void onClick(View v) {
                //intent함수를 통해 register액티비티 함수를 호출한다.
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() { //로그인 버튼이 눌리면
            @Override
            public void onClick(View v) {

                EmailText = findViewById(R.id.username);
                PasswordText = findViewById(R.id.password);

                String email = EmailText.getText().toString().trim();
                String pwd = PasswordText.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                } else {
                                    Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}