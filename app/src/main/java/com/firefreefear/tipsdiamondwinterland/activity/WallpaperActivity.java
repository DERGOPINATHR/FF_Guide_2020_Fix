package com.firefreefear.tipsdiamondwinterland.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firefreefear.tipsdiamondwinterland.R;
import com.firefreefear.tipsdiamondwinterland.adapter.WallpaperAdapter;
import com.firefreefear.tipsdiamondwinterland.model.WallpaperModel;
import com.firefreefear.tipsdiamondwinterland.viewmodel.WallpaperViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;

public class WallpaperActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WallpaperAdapter wallpaperAdapter;
    private WallpaperViewModel wallpaperViewModel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private AdView adView;
    private RelativeLayout banner_layout;
    private InterstitialAd interstitialAd;
    public static int nbShowInterstitial = 3;
    public static int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        banner_layout = findViewById(R.id.layout_banner);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                super.onAdLoaded();
                WallpaperActivity.this.banner_layout.setVisibility(View.VISIBLE);
            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitialAds));
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        recyclerView = findViewById(R.id.rv_wallpaper);
        wallpaperAdapter = new WallpaperAdapter();
        wallpaperAdapter.notifyDataSetChanged();

        wallpaperAdapter.setOnItemClickCallback(new WallpaperAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(WallpaperModel data) {
                try {
                    Intent intent = new Intent(WallpaperActivity.this, DetailWallpaperActivity.class);
                    intent.putExtra(DetailWallpaperActivity.EXTRA_WALLPAPER, data);
                    startActivity(intent);
                    if (mCount == nbShowInterstitial) {
                        if (interstitialAd.isLoaded())
                            interstitialAd.show();
                        mCount = 0;
                    }
                    ++ mCount;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wallpaperAdapter);

        wallpaperViewModel = ViewModelProviders.of(this).get(WallpaperViewModel.class);
        wallpaperViewModel.getWallpaper().observe(this, getWallpaper);
        wallpaperViewModel.setWallpaper(this);

    }

    private Observer<ArrayList<WallpaperModel>> getWallpaper = new Observer<ArrayList<WallpaperModel>>() {
        @Override
        public void onChanged(ArrayList<WallpaperModel> wallpaper) {
            if (wallpaper != null) {
                wallpaperAdapter.setWallpaper(wallpaper);
            }
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

}
