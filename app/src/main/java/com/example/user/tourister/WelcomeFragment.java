package com.example.user.tourister;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Map;


public class WelcomeFragment extends Fragment {

    private static final String USER_CREATION_SUCCESS =  "Successfully created user";
    private static final String USER_CREATION_ERROR =  "User creation error";
    private static final String EMAIL_INVALID =  "email is invalid :";

    //Facebook
    private LoginButton loginButton;
    private CallbackManager mFacebookCallbackManager;
    private AccessTokenTracker mFacebookAccessTokenTracker;

    //Firebase
    private AuthData mAuthData;
    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthStateListener;

    //Firebase Login
    private EditText loginView;
    private EditText passwordView;
    private Button login;
    private TextView createAccount;
    private EditText userNameET;
    private EditText passwordET;
    private EditText nameET;

    private ProgressDialog mAuthProgressDialog;
    private OnFragmentInteractionListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        loginView = (EditText) rootView.findViewById(R.id.email_ed);
        passwordView = (EditText) rootView.findViewById(R.id.pwd_ed);
        login = (Button) rootView.findViewById(R.id.login);
        loginButton = (LoginButton) rootView.findViewById(R.id.fblogin);
        createAccount = (TextView) rootView.findViewById(R.id.newAccount);

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        mFacebookCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email");

        loginButton.setFragment(this);

        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                int i=1;
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });

        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                onFacebookAccessTokenChange(currentAccessToken);
            }
        };

        mAuthProgressDialog = new ProgressDialog(getContext());
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating...");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthProgressDialog.show();
                mFirebaseRef.authWithPassword(loginView.getText().toString().trim(), passwordView.getText().toString().trim(), new AuthResultHandler("password"));
            }
        });
        mFirebaseRef.addAuthStateListener(mAuthStateListener);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getContext());
                final View DialogView = li.inflate(
                        R.layout.new_account_dialog, null);
                userNameET = (EditText) DialogView.findViewById(R.id.acc_mail);
                passwordET = (EditText) DialogView.findViewById(R.id.acc_pass_new);
                nameET = (EditText) DialogView.findViewById(R.id.name);
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Create Account").create();
                dialog.setCancelable(true);
                dialog.setView(DialogView);
                dialog.show();

                Button submit = (Button) DialogView.findViewById(R.id.acc_submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createUser();
                        if(nameET.getText().length() > 0 && userNameET.getText().length() > 0 && passwordET.getText().length() > 0)
                        {
                            mListener.onFragmentInteraction();
                        }
                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // if user logged in with Facebook, stop tracking their token
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
        }

        // if changing configurations, stop tracking firebase session.
        mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            mAuthProgressDialog.show();
            mFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new AuthResultHandler("facebook"));
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
            if (this.mAuthData != null && this.mAuthData.getProvider().equals("facebook")) {
                mFirebaseRef.unauth();
                setAuthenticatedUser(null);
            }
        }
    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            String email = ((String) authData.getProviderData().get("email")).replaceAll("\\.","@@");
            if(authData.getProvider().equals("facebook")){
                String name = (String) authData.getProviderData().get("displayName");
                saveuserDetailsinFirebase(email,name);
            }
            AppManager.setUseremail(email);
            mListener.onFragmentInteraction();
            this.mAuthData = authData;
        }
    }

    private void saveuserDetailsinFirebase(final String email, final String name){
        final Firebase favref = AppManager.getRef();
        Query queryRef = favref.orderByKey().equalTo(email);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    favref.child(email).child("Name").setValue(name);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    // Validate email address for new accounts.
    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            userNameET.setError(EMAIL_INVALID + email);
            return false;
        }
        return true;
    }

    // create a new user in Firebase
    public void createUser() {
        if(userNameET.getText() == null ||  !isEmailValid(userNameET.getText().toString())) {
            return;
        }
        mFirebaseRef.createUser(userNameET.getText().toString(), passwordET.getText().toString(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Snackbar snackbar = Snackbar.make(userNameET, USER_CREATION_SUCCESS, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        if(nameET.getText().length() > 0 && userNameET.getText().length() > 0 && passwordET.getText().length() > 0) {
                            saveuserDetailsinFirebase(userNameET.getText().toString().replaceAll("\\.","@@"),nameET.getText().toString());
                            mFirebaseRef.authWithPassword(userNameET.getText().toString().trim(), passwordET.getText().toString().trim(), new AuthResultHandler("password"));
                        }

                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Snackbar snackbar = Snackbar.make(userNameET, USER_CREATION_ERROR, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
    }
}
