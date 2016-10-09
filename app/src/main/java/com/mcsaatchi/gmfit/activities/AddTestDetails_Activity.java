package com.mcsaatchi.gmfit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.TestMetricsRecycler_Adapter;
import com.mcsaatchi.gmfit.adapters.UserTestsRecycler_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponseDatum;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddTestDetails_Activity extends Base_Activity {

    @Bind(R.id.testPhotosLayout)
    LinearLayout testPhotosLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.addNewTestPhotoBTN)
    TextView addNewTestPhotoBTN;
    @Bind(R.id.testMetricsListView)
    RecyclerView testMetricsListView;

    private static final int SELECT_PICTURE = 1;
    private static final int RC_HANDLE_STORAGE_PERM = 2;

    private List<String> selectedImagePaths = new ArrayList<>();

    private ArrayList<MedicalTestsResponseDatum> testMetrics = new ArrayList<>();
    private String testName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.app_name, true));

        setContentView(R.layout.activity_add_health_test_details);

        ButterKnife.bind(this);

        addNewTestPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean hasPermission = (ContextCompat.checkSelfPermission(AddTestDetails_Activity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

                if (!hasPermission) {
                    ActivityCompat.requestPermissions(AddTestDetails_Activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RC_HANDLE_STORAGE_PERM);
                } else {
                    openPictureChooser();
                }
            }
        });

        if (getIntent().getExtras() != null) {
            testName = getIntent().getExtras().getString(Constants.EXTRAS_TEST_TITLE);
            testMetrics = getIntent().getExtras().getParcelableArrayList(Constants.EXTRAS_TEST_OBJET_DETAILS);
            setupMetricsListView(testMetrics);
        }

        setupToolbar(toolbar, testName, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                if (intent.getClipData() != null) {
                    for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                        selectedImagePaths.add(getPath(AddTestDetails_Activity.this, intent.getClipData().getItemAt(i).getUri()));
                        testPhotosLayout.addView(createNewImageViewLayout(intent.getClipData().getItemAt(i).getUri()));
                    }
                } else {
                    testPhotosLayout.addView(createNewImageViewLayout(intent.getData()));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openPictureChooser();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_test_photos_bar_title)
                .setMessage(R.string.no_storage_permission)
                .setPositiveButton(R.string.OK, listener)
                .show();
    }

    private void openPictureChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    private void setupMetricsListView(ArrayList<MedicalTestsResponseDatum> testMetrics){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        TestMetricsRecycler_Adapter userTestsRecyclerAdapter = new TestMetricsRecycler_Adapter(this, testMetrics);

        testMetricsListView.setLayoutManager(mLayoutManager);
        testMetricsListView.setAdapter(userTestsRecyclerAdapter);
        testMetricsListView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    public View createNewImageViewLayout(Uri imageURI) {
        final View singlePhotoLayout = getLayoutInflater().inflate(R.layout.view_test_photo_item, null);

        ImageView testPhotoIMG = (ImageView) singlePhotoLayout.findViewById(R.id.testPhotoIMG);
        Button deleteTestPhotoBTN = (Button) singlePhotoLayout.findViewById(R.id.deleteTestPhotoBTN);

        Picasso.with(AddTestDetails_Activity.this).load(new File(getPath(AddTestDetails_Activity.this, imageURI))).fit().into
                (testPhotoIMG);

        deleteTestPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testPhotosLayout.removeView(singlePhotoLayout);
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.test_photos_width),
                getResources().getDimensionPixelSize(R.dimen.test_photos_height));
        singlePhotoLayout.setPadding(20, 0, 20, 0);
        singlePhotoLayout.setLayoutParams(layoutParams);

        return singlePhotoLayout;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
