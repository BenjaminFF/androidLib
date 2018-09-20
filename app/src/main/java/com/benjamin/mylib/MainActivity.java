package com.benjamin.mylib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyBanner myBanner=findViewById(R.id.mybanner);
        ArrayList<String> bannerItems=new ArrayList<>();
        bannerItems.add("banner1");
        bannerItems.add("banner2");
        bannerItems.add("banner3");
        bannerItems.add("banner4");
        bannerItems.add("banner5");
        MyBannerAdapter myBannerAdapter=new MyBannerAdapter(bannerItems);
        myBanner.setAdapter(myBannerAdapter);
    }
}
