package com.dingding.picbasic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int READ_REQUEST_CODE = 1;
    ImageView iv_f;
    RecyclerView rv;
    ArrayList<MyImage> images = new ArrayList<>();
    ImageAdapter adapter;
    GlobalApp dapp;
    int num_pic = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dapp = (GlobalApp) getApplicationContext();
        rv = findViewById(R.id.rv);

        num_pic = Integer.parseInt(dapp.pref.getString("num_pic", "20"));
        adapter = new ImageAdapter(images);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        String str_uri = dapp.pref.getString("folderUri","");
        if(str_uri!=""){
            showData(Uri.parse(str_uri));
            Toast.makeText(this, "載入上次資料夾", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "先指定資料夾", Toast.LENGTH_SHORT).show();
        }

    }
    public void showData(Uri treeUri){
        images.clear();

        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
        Toast.makeText(this, "數量：" + pickedDir.listFiles().length, Toast.LENGTH_SHORT).show();
        DocumentFile[] fs = pickedDir.listFiles();


        for(int i=0;i<fs.length;i++){
            if(i>=num_pic) break;
            images.add(new MyImage(fs[i].getUri(), fs[i].getName()));
        }

        adapter.notifyDataSetChanged();
    }
    private void pickRandom20() {
        images.clear();

        String str_uri = dapp.pref.getString("folderUri","");
        Uri treeUri = Uri.parse(str_uri);

        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
        DocumentFile[] fs = pickedDir.listFiles();

        Random rand = new Random();
        int random_idx = rand.nextInt(fs.length); // Obtain a number between [0 - 49].


        Toast.makeText(this, "編號：" + random_idx, Toast.LENGTH_SHORT).show();


        for(int i=random_idx;i<fs.length;i++){
            if(i - random_idx >= num_pic) break;
            images.add(new MyImage(fs[i].getUri(), fs[i].getName()));
        }

        adapter.notifyDataSetChanged();
        rv.scrollToPosition(0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == READ_REQUEST_CODE){
            Uri treeUri = data.getData();
            dapp.pref.edit().putString("folderUri", treeUri.toString()).commit();
            showData(treeUri);
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
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, READ_REQUEST_CODE);
            return true;
        }else if (item.getItemId() == R.id.random) {
            pickRandom20();
            return true;
        }else if (item.getItemId() == R.id.setting) {
            new LovelyTextInputDialog(this)
                    .setTitle("輸入顯示圖片數量")
                    .setInitialInput(dapp.pref.getString("num_pic", ""))
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
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
