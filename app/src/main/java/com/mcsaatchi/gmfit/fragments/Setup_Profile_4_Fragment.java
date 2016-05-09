package com.mcsaatchi.gmfit.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.PermissionsChecker;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Setup_Profile_4_Fragment extends Fragment {
    @Bind(R.id.enableNotifcationsLocationBTN)
    protected Button enableNotifcationsLocationBTN;

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_4, container, false);

        ButterKnife.bind(this, fragmentView);

        enableNotifcationsLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsChecker permissionsChecker = new PermissionsChecker(getActivity());

                String[] neededPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};

                if (permissionsChecker.lacksPermissions(neededPermissions)) {
                    Toast.makeText(getActivity(), "Permissions NOT granted, asking now", Toast.LENGTH_SHORT).show();
                    requestLocationPermissions(neededPermissions);
                } else {
                    Toast.makeText(getActivity(), "Permissions already granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return fragmentView;
    }

    private void requestLocationPermissions(final String[] permissions) {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA) ||
                !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSIONS);
        }

//        Snackbar.with(getApplicationContext())
//                .actionLabel(R.string.request_permission)
//                .dismissOnActionClicked(true)
//                .duration(8000)
//                .actionColor(getResources().getColor(R.color.yellow))
//                .text(R.string.permission_capture_rationale)
//                .actionListener(new ActionClickListener() {
//                    @Override
//                    public void onActionClicked(Snackbar snackbar) {
//                        ActivityCompat.requestPermissions(Snippets_Activity.this, permissions,
//                                REQUEST_LOCATION_PERMISSIONS);
//                    }
//                }).show(Snippets_Activity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0)
            return;

        boolean isAllPermissionsGranted;
        for (int result : grantResults) {
            isAllPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED);
            if (!isAllPermissionsGranted) {
                Toast.makeText(getActivity(), "PERMISSIONS NOT GRANTED?", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getActivity(), "PERMISSIONS GRANTE?", Toast.LENGTH_SHORT).show();
            }
        }

//        switch (requestCode) {
//            case REQUEST_LOCATION_PERMISSIONS:
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }
}
