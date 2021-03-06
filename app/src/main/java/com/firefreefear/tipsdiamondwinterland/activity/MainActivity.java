package com.firefreefear.tipsdiamondwinterland.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firefreefear.tipsdiamondwinterland.R;
import com.firefreefear.tipsdiamondwinterland.adapter.MenuAdapter;
import com.firefreefear.tipsdiamondwinterland.model.MenuModel;
import com.firefreefear.tipsdiamondwinterland.viewmodel.MenuViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private MenuViewModel viewModel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private AdView adView;
    private RelativeLayout banner_layout;
    private InterstitialAd interstitialAd;
    public static int nbShowInterstitial = 4;
    public static int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

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
                MainActivity.this.banner_layout.setVisibility(View.VISIBLE);
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

        shimmerFrameLayout = findViewById(R.id.shimmer_effect);

        menuAdapter = new MenuAdapter();
        menuAdapter.notifyDataSetChanged();

        recyclerView = findViewById(R.id.rv_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(menuAdapter);

        menuAdapter.setOnItemClickCallback(new MenuAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(MenuModel data) {
                try {
                    switch (data.getId()) {
                        case 1:
                            Intent intent = new Intent(MainActivity.this, TipsActivity.class);
                            startActivity(intent);
                            showInterstitialAds();
                            break;
                        case 2:
                            Intent intentWaepon = new Intent(MainActivity.this, WaeponActivity.class);
                            startActivity(intentWaepon);
                            showInterstitialAds();
                            break;
                        case 3:
                            Intent intentCharacter = new Intent(MainActivity.this, CharacterActivity.class);
                            startActivity(intentCharacter);
                            showInterstitialAds();
                            break;
                        case 4:
                            Intent intentVehicles = new Intent(MainActivity.this, VehiclesActivity.class);
                            startActivity(intentVehicles);
                            showInterstitialAds();
                            break;
                        case 5:
                            Intent intentDiamond = new Intent(MainActivity.this, DiamondActivity.class);
                            startActivity(intentDiamond);
                            showInterstitialAds();
                            break;
                        case 6:
                            Intent intentWallpaper = new Intent(MainActivity.this, WallpaperActivity.class);
                            startActivity(intentWallpaper);
                            showInterstitialAds();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel = ViewModelProviders.of(this).get(MenuViewModel.class);
        viewModel.getMenu().observe(this, getMenu);
        viewModel.setMenu(this);
    }

    private void showInterstitialAds() {
        if (mCount == nbShowInterstitial) {
            if (interstitialAd.isLoaded())
                interstitialAd.show();
            mCount = 0;
        }
        ++ mCount;
    }

    private Observer<ArrayList<MenuModel>> getMenu = new Observer<ArrayList<MenuModel>>() {
        @Override
        public void onChanged(ArrayList<MenuModel> menus) {
            if (menus != null) {
                menuAdapter.setData(menus);
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

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rate:
//                Toast.makeText(this, "Anda Memilih Rate", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try { startActivity(goToMarket); }
                catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.privacy:
//                Toast.makeText(this, "Anda Memilih Privacy", Toast.LENGTH_SHORT).show();
                String url = "https://www.yumiekids.blogspot.com/2020/01/privacy-tos.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
