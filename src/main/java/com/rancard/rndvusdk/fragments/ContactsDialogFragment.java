package com.rancard.rndvusdk.fragments;
/**
 * Created by RSL-PROD-003 on 11/4/16.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rancard.rndvusdk.Config;
import com.rancard.rndvusdk.R;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.utils.Constants;
import com.rancard.rndvusdk.utils.MobileDevice;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by RSL-PROD-003 on 8/12/16.
 */
public class ContactsDialogFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks{


    private final int CONTACT_PERMISSION = 7;


    public static ContactsDialogFragment newInstance() {
        ContactsDialogFragment f = new ContactsDialogFragment();
        return f;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getDialog().setCanceledOnTouchOutside(false);
//        return this.getView();
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity())
                .title(Config.CONTACT_PERMISSION_DENIED_TITLE)
                .content(Config.CONTACT_PERMISSION_DENIED_DESCRIPTION)
                .iconRes(R.drawable.connected_users)
                .maxIconSize(300)
                .positiveText(getResources().getString(R.string.okay))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        System.out.println("okkkk");
                        getUserContacts();
                    }
                })
                .negativeText(getResources().getString(R.string.notnow))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getDialog().dismiss();
                    }
                });
        return dialog.build();
    }

//    @Override public void onStart() {
//        super.onStart();
//
//        Window window = getDialog().getWindow();
//        WindowManager.LayoutParams windowParams = window.getAttributes();
//        windowParams.dimAmount = 0f;
//        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(windowParams);
//    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NO_TITLE, 0);
//    }

    @AfterPermissionGranted(CONTACT_PERMISSION)
    private void getUserContactsForMarshmallow(){

        Log.d(Constants.TAG, "GET USER CONTACTS");
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_CONTACTS)) {
            Log.d(Constants.TAG, "in has GET USER CONTACTS permission for M");
            Rendezvous.retrieveContacts(getActivity());
        }else{
            // Request one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_contacts),
                    CONTACT_PERMISSION, Manifest.permission.READ_CONTACTS);
        }
    }

    private void getUserContacts(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getUserContactsForMarshmallow();
        }else{
            Log.d(Constants.TAG, "in has GET USER CONTACTS permission");
            Rendezvous.retrieveContacts(getActivity());
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        getUserContacts();
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        MobileDevice.PerformCommonActions.showYesNoDialog(getActivity(), Config.CONTACT_PERMISSION_DENIED_TITLE, Config.CONTACT_PERMISSION_DENIED_DESCRIPTION, new MobileDevice.PerformCommonActions.YesNoDialogListener() {
            @Override
            public void onYesClicked(DialogInterface dialog, int which) {
                getUserContactsForMarshmallow();
            }

            @Override
            public void onNoClicked(DialogInterface dialog, int which) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(Constants.TAG, "destroyed Contacts Fragment");
    }

    public static void show(AppCompatActivity context) {
        ContactsDialogFragment dialog = new ContactsDialogFragment();
        dialog.show(context.getSupportFragmentManager(), "[CONTACTS_DIALOG]");
    }
}
