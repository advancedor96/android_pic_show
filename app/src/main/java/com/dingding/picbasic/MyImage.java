package com.dingding.picbasic;

import android.net.Uri;

public class MyImage {
    private Uri uri;
    private String name;

    public MyImage(Uri uri, String name){
        this.uri = uri;
        this.name = name;
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getName() {
        return this.name;
    }
}
