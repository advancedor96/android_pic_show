package com.dingding.picbasic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.BoardiesITSolutions.FileDirectoryPicker.DirectoryPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DIRECTORY_PICKER = 2;
    RecyclerView rv;
    ArrayList<MyImage> images = new ArrayList<>();
    ImageAdapter adapter;
    GlobalApp dapp;
    int num_pic = 5;
    int now_idx = 0;
    File[] allfiles;
    ImageButton ib_goNext, ib_goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ib_goNext = findViewById(R.id.ib_goNext);
        ib_goBack = findViewById(R.id.ib_goBack);
        GestureDetector gdDown = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                showNext(now_idx, true);
                return true;
            }
        });
        GestureDetector gdUp = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                now_idx = now_idx - num_pic*2;
                showNext(now_idx, false);
                return true;
            }
        });


        ib_goNext.setOnTouchListener((v, event) -> {
            return gdDown.onTouchEvent(event);
        });
        ib_goBack.setOnTouchListener((v, event) -> {
            return gdUp.onTouchEvent(event);
        });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dapp = (GlobalApp) getApplicationContext();
        rv = findViewById(R.id.rv);

        num_pic = Integer.parseInt(dapp.pref.getString("num_pic", "20"));
        adapter = new ImageAdapter(images);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(v -> showNext(now_idx));


        String path = dapp.pref.getString("path","");
        if(path!=""){
            now_idx = new Random().nextInt((new File(path)).listFiles().length);
            Logger.d("設為"+now_idx);
            showData(path);
            Toast.makeText(this, String.format("載入%d張，從%s", num_pic, path), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "先指定資料夾", Toast.LENGTH_SHORT).show();
        }

    }
    public void showNext(int idx, boolean goTop){
        images.clear();
        Toast.makeText(MainActivity.this, "打開" + idx, Toast.LENGTH_LONG).show();
        for(int i=idx;i<allfiles.length;i++){
            if(i - idx >=num_pic) {
                now_idx = i;
                break;
            }
            if(allfiles[i].isDirectory()) continue;

            String ext = allfiles[i].getName().substring(allfiles[i].getName().lastIndexOf("."));
            if(ext.toLowerCase().equals(".png") || ext.toLowerCase().equals(".jpg")){
                images.add(new MyImage(Uri.fromFile(allfiles[i]), i + "-" + allfiles[i].getName()));
            }else{
                Logger.d("跳過的副檔名:"+ext.toLowerCase());
            }
        }
        adapter.notifyDataSetChanged();
        if(goTop){
            rv.scrollToPosition(0);
        }else{
            rv.scrollToPosition(adapter.getItemCount()-1);
        }
    }
    public void showData(String path){
        images.clear();
        File pickedDir = new File(path);
        Toast.makeText(this, "總數：" + pickedDir.listFiles().length, Toast.LENGTH_SHORT).show();
        allfiles = pickedDir.listFiles();
        Logger.d("排序開始");
        Arrays.sort(allfiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }
            public boolean equals(Object obj) {
                return true;
            }

        });
        showNext(now_idx, true);

    }

    public void pickFolder(){
        Intent intent = new Intent(this, DirectoryPicker.class);
        startActivityForResult(intent, REQUEST_DIRECTORY_PICKER);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIRECTORY_PICKER){
            if(resultCode == Activity.RESULT_OK){
                String currentPath = data.getStringExtra(DirectoryPicker.BUNDLE_CHOSEN_DIRECTORY);
                dapp.pref.edit().putString("path", currentPath).commit();
                showData(currentPath);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.select_folder) {
            pickFolder();
            return true;
        }else if (item.getItemId() == R.id.random) {
            int random_idx = new Random().nextInt(allfiles.length);
            showNext(random_idx, true);
            return true;
        }else if (item.getItemId() == R.id.setting) {
            new LovelyTextInputDialog(this)
                    .setTitle("輸入顯示圖片數量")
                    .setInitialInput(dapp.pref.getString("num_pic", ""))
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .setInputFilter("請輸入數字", new LovelyTextInputDialog.TextFilter() {
                        @Override
                        public boolean check(String text) {
                            return text.matches("^[0-9]*$");
                        }
                    })
                    .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                            dapp.pref.edit().putString("num_pic", text).commit();
                            num_pic = Integer.parseInt(dapp.pref.getString("num_pic", ""));
                            showNext(now_idx, true);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void yo(View view) {
        Logger.d("yo");
    }
}
