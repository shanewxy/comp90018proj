package com.unimelb.checkmein;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final int CROP = 0;
    private static final int OPEN_GALLERY = 1;
    private static final int OPEN_CAMERA = 2;
    NavigationView navigationView;
    File file;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;

    private static final String IMAGE_FILE_NAME = "header.jpg";

    View hView;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String email = auth.getCurrentUser().getEmail();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        file = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");
        hView = navigationView.getHeaderView(0);
        image = hView.findViewById(R.id.imageView);
        // Passing each menu ID as a set of Ids because each
        TextView nav_user = hView.findViewById(R.id.email_textView);
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_allsubjects, R.id.nav_mysubject,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        nav_user.setText(email);
        image.setOnClickListener(this);
        findViewById(R.id.fab).setOnClickListener(this);
//        findViewById(R.id.nav_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.nav_send) {
            auth.signOut();
            finish();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (i == R.id.imageView) {
            //                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                // 存到什么位置
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//                startActivityForResult(intent, 1);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
//            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
//            galleryIntent.setType("image/*");
//            startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);

        } else if (i == R.id.fab) {
            auth.signOut();
            finish();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_send){
            Log.d("dff", "onOptionsItemSelected: ");
            auth.signOut();
            finish();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void crop(Uri uri) {
        // 定义图片裁剪意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));


        intent.setDataAndType(uri, "image/*");
        // 设置是否裁剪
        intent.putExtra("crop", "true");
        // 裁剪框的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // 设置输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        // 设置图片格式
        intent.putExtra("outputFormat", "JPEG");
        //是否返回数据
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP);

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK) {
//            return;
//        } else {
//            switch (requestCode) {
//                case IMAGE_REQUEST_CODE:
//                    resizeImage(data.getData());
//                    break;
//                case CAMERA_REQUEST_CODE:
//                    if (isSdcardExisting()) {
//                        resizeImage(getImageUri());
//                    } else {
//                        Toast.makeText(MainActivity.this, "未找到存储卡，无法存储照片！",
//                                Toast.LENGTH_LONG).show();
//                    }
//                    break;
//
//                case RESIZE_REQUEST_CODE:
//                    if (data != null) {
//                        showResizeImage(data);
//                    }
//                    break;
//            }
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            image.setImageDrawable(drawable);
        }
    }

    private Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA) {
            // 图片怎么取出
//          imageView.setImageURI(Uri.fromFile(file));
            crop(Uri.fromFile(file));
        } else if (requestCode == OPEN_GALLERY) {
            // 相册应用通过putData设置的图片的uri，所以我们这样拿
            Uri uri = data.getData();
//          imageView.setImageURI(uri);
            crop(uri);
        } else if (requestCode == CROP) {
            //直接拿到一张图片
//            Bitmap bitmap = data.getParcelableExtra("data");
            // 直接拿到一张图片
            final Bitmap bitmap = data.getParcelableExtra("data");
            File picFile = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".png");
            // 把bitmap放置到文件中
            // format 格式
            // quality 质量
            Drawable drawable = new BitmapDrawable(bitmap);
            image.setImageDrawable(drawable);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(
                        picFile));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(bitmap);

        }
    }
}

