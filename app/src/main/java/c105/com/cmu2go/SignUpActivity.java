package c105.com.cmu2go;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private EditText etConf;
    private EditText etPhone;
    private EditText etAndrew;
    private Button bReg;
    private FrameLayout spinHolder;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        spinHolder = (FrameLayout) findViewById(R.id.spinHolder2);
        mAuth = FirebaseAuth.getInstance();
        mAuthList = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    spinHolder.setAnimation(outAnimation);
                    spinHolder.setVisibility(View.GONE);
                    Log.v("SIGN UP", "Sign up succeded.");
                    Toast t = Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_LONG);
                    t.show();
                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Log.v("SIGN UP", "Sign up failed.");
                }

            }
        };
        etEmail = (EditText) findViewById(R.id.etEmail2);
        etPass = (EditText) findViewById(R.id.etPass2);
        etConf = (EditText) findViewById(R.id.etPassConf);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etAndrew = (EditText) findViewById(R.id.etAndrew);
        bReg = (Button) findViewById(R.id.bRegister);
        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String pass2 = etConf.getText().toString();
                String phone = etPhone.getText().toString();
                Toast t;
                switch (verify(email, pass, pass2, phone)) {
                    case -3:
                        t = Toast.makeText(SignUpActivity.this, "Phone number has to be 10 digits long.", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                    case -2:
                        t = Toast.makeText(SignUpActivity.this, "Email is wrongly formatted.", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                    case -1:
                        t = Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                    case 0:
                        t = Toast.makeText(SignUpActivity.this, "Password must at least 6 characters long.", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                SignUpTask task = new SignUpTask();
                task.execute(email,pass);
            }
        });
    }

    private int verify(String email, String p1,String p2, String phone) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher verifyer = pattern.matcher("email");
        if(verifyer.matches()){
            return -2;
        }
        if(!(p1.equals(p2)))
            return -1;
        if(p1.length()<6)
            return 0;
        if(phone.length() != 10)
            return -3;
        return 1;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthList);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthList != null) {
            mAuth.removeAuthStateListener(mAuthList);
        }
    }
    class SignUpTask extends AsyncTask<String,Void,Void>{
        String email;
        String pass;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            spinHolder.setAnimation(inAnimation);
            spinHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            email = strings[0];
            pass = strings[1];
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("SIGN UP", "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {

                        if (task.getException().getClass().equals(FirebaseAuthUserCollisionException.class)) {
                            Toast.makeText(SignUpActivity.this, "This email is already registered.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException().getClass().equals(FirebaseNetworkException.class)) {
                                Toast.makeText(SignUpActivity.this, "Couldn't connect to the network.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.e("SIGN UP", task.getException().toString());
                    }
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(getString(R.string.DIR_ACCOUNTS));
                    myRef = myRef.child(mAuth.getCurrentUser().getUid());
                    myRef.setValue(new Account(etAndrew.getText().toString(),etPhone.getText().toString()));
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            spinHolder.setAnimation(outAnimation);
            spinHolder.setVisibility(View.GONE);

        }
    }
}
