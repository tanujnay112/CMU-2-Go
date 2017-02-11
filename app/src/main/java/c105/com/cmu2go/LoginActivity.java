package c105.com.cmu2go;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LOGIN";
    private EditText etEmail;
    private EditText etPass;
    private Button bLogin;
    private TextView bSignUp;
    private SignInButton bGSignIn;
    private Button bOpenEmail;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FrameLayout spinHolder;
    private AuthCredential credential;
    boolean go = true;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        bSignUp = (TextView) findViewById(R.id.bSignUp);
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        spinHolder = (FrameLayout) findViewById(R.id.spinHolder);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is signed in
                    LoginTask t = new LoginTask();
                    t.execute(true,false);

                    //logIn(currentUser);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + currentUser.getUid());
                }else{
                    //LoginManager.getInstance().logOut();
                }
                // ...
            }
        };
        //bOpenEmail = (Button) findViewById(R.id.bOpenEmail);
        /*bOpenEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                findViewById(R.id.signInHolder).setVisibility(View.VISIBLE);
            }
        });*/
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        bLogin = (Button) findViewById(R.id.bSignIn);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String pass = etPass.getText().toString();
                if(email.isEmpty()||pass.isEmpty()){
                    Toast t = Toast.makeText(LoginActivity.this,"Please enter both an email and password.", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                LoginTask login = new LoginTask();
                login.execute(false,true);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    private void openAnimation(){
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        spinHolder.setAnimation(inAnimation);
        spinHolder.setVisibility(View.VISIBLE);
    }

    private void closeAnimation(){
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        spinHolder.setAnimation(outAnimation);
        spinHolder.setVisibility(View.GONE);
    }
    class LoginTask extends AsyncTask<Boolean,Void,Void> implements OnCompleteListener<AuthResult>{

        String email;
        String pass;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            openAnimation();
            email = etEmail.getText().toString();
            pass = etPass.getText().toString();
        }

        @Override
        protected Void doInBackground(Boolean... bools) {
            if(bools[0]){
                logIn(currentUser);
            }else {
                if (bools[1]) {
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    credential = EmailAuthProvider.getCredential(email, pass);
                                    LoginTask.this.onComplete(task);
                                }
                            });
                } else {
                    mAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, this);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            closeAnimation();
            if(!go)
                return;

        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
            if (!task.isSuccessful()) {
                Log.w(TAG, "signInWithCredential", task.getException());
                go = false;
                if (task.getException().getClass().getName().contains("Network")) {
                    Toast.makeText(LoginActivity.this, "Couldn't connect to the network.",
                            Toast.LENGTH_SHORT).show();
                } else{
                    if(task.getException().getClass().getName().contains("Collision")){
                        Toast.makeText(LoginActivity.this, "There's an existing user associated with this email. Try signing in with the method that this email is registered with, You can link other sign-in methods in settings",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Log.v("LOGIN",  "Authentication failed: Please sign up for an account if you need to.");
                        Toast.makeText(LoginActivity.this, "Authentication failed: Please sign up for an account if you need to.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                //  LoginManager.getInstance().logOut();

                Log.v("LOGIN", task.getException().toString());
            }else{
                logIn(mAuth.getCurrentUser());
            }
        }
        private void logIn(FirebaseUser currentUser) {
            //set settings
            Firebase.setAndroidContext(LoginActivity.this);

            //Takes some time to get data. Executes subsequent code before data is retrieved causing a lag between inflation of
            //XML and the appearance of question/answers. Need to fix this.
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }
}
