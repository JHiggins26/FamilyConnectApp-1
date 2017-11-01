package familyconnect.familyconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * The type Login.
 */
public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private SignInButton buttonLogin;
    private GoogleSignInResult result;
    private String accountEmail;
    private String realName;
    private String displayName;
    private String picURL;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = (SignInButton) findViewById(R.id.sign_in_button);

        buttonLogin.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    private void updateUI(boolean isLogin) {

        if(isLogin) {
            Intent welcomePage = new Intent(Login.this, UserMainArea.class);
            Login.this.startActivity(welcomePage);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            try {
                accountEmail = acct.getEmail();
                realName = acct.getGivenName();
                displayName = acct.getDisplayName();
                picURL = acct.getPhotoUrl().toString();
                String authCode = acct.getServerAuthCode();
            }catch (NullPointerException npe) {
                picURL = "https://developers.google.com/experts/img/user/user-default.png";
            }

            sharedPreferences = getSharedPreferences("USER_ATTRIBUTES", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putBoolean("isLogin", true);
            editor.putString("name", realName);
            editor.putString("email", accountEmail);
            editor.putString("picture", picURL);
            editor.commit();

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.v("SIGN","SIGNED IN");
            result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}