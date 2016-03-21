package com.mac.isaac.socialmediaisaac;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements MainFragment.GetAccountsPermission, OnPermissionCallback {

    static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    private static Context context;
    private static ProgressDialog pd;
    final int PERMISSIONS_REQUEST_CODE = 1234;
    final String PERMISSION = Manifest.permission.GET_ACCOUNTS;
    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        printHashKey();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        permissionHelper = PermissionHelper.getInstance(this);
        requestPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected static void showProgress(String message) {
        pd.setMessage(message);
        pd.show();
    }

    protected static void hideProgress() {
        pd.dismiss();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.mac.isaac.socialmediaisaac",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HASH KEY:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("HASH KEY:", "NameNotFoundException "+e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e("HASH KEY:", "NoSuchAlgorithmException "+e.getMessage());
        }
    }

    @Override
    public void requestPermission() {
        Log.i("MYTAG", "requestPermission()");
        if (!hasPermission()) {
            Log.i("MYTAG", "setForceAccepting()");
            permissionHelper.setForceAccepting(false).request(PERMISSION);
        }
    }

    @Override
    public boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("MYTAG", "onRequestPermissionsResult()");
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(String[] permissionName) {
        Log.i("MYTAG", "onPermissionGranted()");
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onPermissionDeclined(String[] permissionName) {
        Log.i("MYTAG", "onPermissionDeclined()");
    }

    @Override
    public void onPermissionPreGranted(String permissionsName) {
        Log.i("MYTAG", "onPermissionPreGranted()");
    }

    @Override
    public void onPermissionNeedExplanation(String permissionName) {
        Log.i("MYTAG", "onPermissionNeedExplanation()");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Accept me")
                .setMessage(permissionName)
                .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionHelper.requestAfterExplanation(PERMISSION);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onPermissionReallyDeclined(String permissionName) {
        Log.i("MYTAG", "onPermissionReallyDeclined()");
    }

    @Override
    public void onNoPermissionNeeded() {
        Log.i("MYTAG", "onNoPermissionNeeded()");
    }
}
