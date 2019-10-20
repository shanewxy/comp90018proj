package com.unimelb.checkmein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final int CROP = 0;
    private static final int OPEN_GALLERY = 1;
    private static final int OPEN_CAMERA = 2;
    NavigationView navigationView;
    File file;

    View hView;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String email = auth.getCurrentUser().getEmail();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                auth.signOut();
                finish();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        nav_user.setText(email);
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//////                // 隐式意图打开系统界面 --要求回传
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                // 存到什么位置
////                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
////                startActivityForResult(intent, 1);
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 1);
//            }
//        });
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

