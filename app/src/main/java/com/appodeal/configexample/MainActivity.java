package com.appodeal.configexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.utils.Log.LogLevel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;

import java.util.Set;
import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String APPODEAL = "appodeal";
    private static final String MOPUB = "mopub";

    private static final String CONFIG_KEY = "mediation_partner";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String mNetworkName;

    private boolean interstitialMoPubStartLoad = false;
    private boolean rewardedVideoMoPubStartLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetworkName = "";

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchRemoteConfig();
     }

    private void fetchRemoteConfig() {
        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "fetch remote config: Succeeded");
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.d(TAG, "fetch remote config: Failed");
                        }
                        displayRemoteConfig();
                    }
                });
    }

    private void displayRemoteConfig() {
        mNetworkName = mFirebaseRemoteConfig.getString(CONFIG_KEY);

        TextView networkNameTextView = findViewById(R.id.networkNameTextView);
        networkNameTextView.setText(mNetworkName);
        if (mNetworkName.equalsIgnoreCase(APPODEAL)){
            initAppodeal();
        }
    }

    public void onClickShowInterstitial(View view) {
        if (mNetworkName.equalsIgnoreCase(APPODEAL)) {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        } else if (mNetworkName.equalsIgnoreCase(MOPUB)) {
            showMoPub("interstitial");
        }
    }

    public void onClickShowRewardedVideo(View view) {
        if (mNetworkName.equalsIgnoreCase(APPODEAL)) {
            Appodeal.show(this, Appodeal.REWARDED_VIDEO);
        } else if (mNetworkName.equalsIgnoreCase(MOPUB)) {
            showMoPub("rewardedvideo");
        }
    }

    public void onClickShowBanner(View view) {
        if (mNetworkName.equalsIgnoreCase(APPODEAL)) {
            Appodeal.show(this, Appodeal.BANNER);
        } else if (mNetworkName.equalsIgnoreCase(MOPUB)) {
            showMoPub("banner");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Appodeal.isInitialized(Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO | Appodeal.BANNER)) {
            Appodeal.onResume(this, Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO | Appodeal.BANNER);
        }
        if (MoPub.isSdkInitialized()) {
            MoPub.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MoPub.isSdkInitialized()) {
            MoPub.onPause(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MoPub.isSdkInitialized()) {
            MoPub.onStop(this);
        }
    }

    private void initAppodeal() {
        Appodeal.setLogLevel(LogLevel.debug);
        Appodeal.setTesting(true);
        Appodeal.initialize(this, "e1168fcc9e57397e7e627959cd88572cc8cd1dcc96a737e0", Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO | Appodeal.BANNER);

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                Log.d(TAG, "Appodeal: onInterstitialLoaded");
            }
            @Override
            public void onInterstitialFailedToLoad() {
                Log.d(TAG, "Appodeal: onInterstitialFailedToLoad");
            }
            @Override
            public void onInterstitialShown() {
                Log.d(TAG, "Appodeal: onInterstitialShown");
            }
            @Override
            public void onInterstitialClicked() {
                Log.d(TAG, "Appodeal: onInterstitialClicked");
            }
            @Override
            public void onInterstitialClosed() {
                Log.d(TAG, "Appodeal: onInterstitialClosed");
            }
        });

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {
                Log.d(TAG, "Appodeal: onRewardedVideoLoaded");
            }
            @Override
            public void onRewardedVideoFailedToLoad() {
                Log.d(TAG, "Appodeal: onRewardedVideoFailedToLoad");
            }
            @Override
            public void onRewardedVideoShown() {
                Log.d(TAG, "Appodeal: onRewardedVideoShown");
            }
            @Override
            public void onRewardedVideoFinished(double amount, String name) {
                Log.d(TAG, "Appodeal: onRewardedVideoFinished");
            }
            @Override
            public void onRewardedVideoClosed(boolean finished) {
                Log.d(TAG, "Appodeal: onRewardedVideoClosed");
            }
        });

        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {
                Log.d(TAG, "Appodeal: onBannerLoaded");
            }
            @Override
            public void onBannerFailedToLoad() {
                Log.d(TAG, "Appodeal: onBannerFailedToLoad");
            }
            @Override
            public void onBannerShown() {
                Log.d(TAG, "Appodeal: onBannerShown");
            }
            @Override
            public void onBannerClicked() {
                Log.d(TAG, "Appodeal: onBannerClicked");
            }
        });
    }

    private void showMoPub(final String type){
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder("b195f8dd8ded45fe847ad89ed1d016da").build();
        MoPubLog.setSdkHandlerLevel(Level.ALL);

        if (!MoPub.isSdkInitialized()) {
            MoPub.initializeSdk(this, sdkConfiguration, new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    showMoPubTypeAd(type);
                }
            });
        } else {
            showMoPubTypeAd(type);
        }
    }

    private void showMoPubTypeAd(String type) {
        if (type.equalsIgnoreCase("banner")) {
            showBannerMoPub();
        } else if (type.equalsIgnoreCase("interstitial")) {
            showInterstitialMoPub();
        } else if (type.equalsIgnoreCase("rewardedvideo")) {
            showRewardedVideoMoPub();
        }
    }

    private void showBannerMoPub() {
        MoPubView mBannerView = findViewById(R.id.adview);
        mBannerView.setAdUnitId("b195f8dd8ded45fe847ad89ed1d016da");

        MoPubView.BannerAdListener bannerAdListener = new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView moPubView) {
                Log.d(TAG, "MoPub: onBannerLoaded");
            }

            @Override
            public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
                Log.d(TAG, "MoPub: onBannerFailed");
            }

            @Override
            public void onBannerClicked(MoPubView moPubView) {
                Log.d(TAG, "MoPub: onBannerClicked");
            }

            @Override
            public void onBannerExpanded(MoPubView moPubView) {
                Log.d(TAG, "MoPub: onBannerExpanded");
            }

            @Override
            public void onBannerCollapsed(MoPubView moPubView) {
                Log.d(TAG, "MoPub: onBannerCollapsed");
            }
        };

        mBannerView.setBannerAdListener(bannerAdListener);
        mBannerView.loadAd();
    }

    private void showInterstitialMoPub() {
        if (interstitialMoPubStartLoad) {
            return;
        }
        interstitialMoPubStartLoad = true;

        final MoPubInterstitial mMoPubInterstitial = new MoPubInterstitial(this, "24534e1901884e398f1253216226017e");
        MoPubInterstitial.InterstitialAdListener interstitialAdListener = new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
                Log.d(TAG, "MoPub: onInterstitialLoaded");
                mMoPubInterstitial.show();
                interstitialMoPubStartLoad = false;
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
                Log.d(TAG, "MoPub: onInterstitialFailed");
                interstitialMoPubStartLoad = false;
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
                Log.d(TAG, "MoPub: onInterstitialShown");
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
                Log.d(TAG, "MoPub: onInterstitialClicked");
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
                Log.d(TAG, "MoPub: onInterstitialDismissed");
            }
        };
        mMoPubInterstitial.setInterstitialAdListener(interstitialAdListener);
        mMoPubInterstitial.load();
    }

    private void showRewardedVideoMoPub() {
        if (rewardedVideoMoPubStartLoad) {
            return;
        }
        rewardedVideoMoPubStartLoad = true;

        final String adUnitId = "920b6145fb1546cf8b5cf2ac34638bb7";
        MoPubRewardedVideoListener moPubRewardedVideoListener = new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(@NonNull String s) {
                Log.d(TAG, "MoPub: onRewardedVideoLoadSuccess");
                rewardedVideoMoPubStartLoad = false;
                if (MoPubRewardedVideos.hasRewardedVideo(adUnitId)) {
                    MoPubRewardedVideos.showRewardedVideo(adUnitId);
                }
            }

            @Override
            public void onRewardedVideoLoadFailure(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
                Log.d(TAG, "MoPub: onRewardedVideoLoadFailure");
                rewardedVideoMoPubStartLoad = false;
            }

            @Override
            public void onRewardedVideoStarted(@NonNull String s) {
                Log.d(TAG, "MoPub: onRewardedVideoStarted");
            }

            @Override
            public void onRewardedVideoPlaybackError(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
                Log.d(TAG, "MoPub: onRewardedVideoPlaybackError");
            }

            @Override
            public void onRewardedVideoClicked(@NonNull String s) {
                Log.d(TAG, "MoPub: onRewardedVideoClicked");
            }

            @Override
            public void onRewardedVideoClosed(@NonNull String s) {
                Log.d(TAG, "MoPub: onRewardedVideoClosed");
            }

            @Override
            public void onRewardedVideoCompleted(@NonNull Set<String> set, @NonNull MoPubReward moPubReward) {
                Log.d(TAG, "MoPub: onRewardedVideoCompleted");
            }
        };

        MoPubRewardedVideoManager.RequestParameters requestParameters = new MoPubRewardedVideoManager.RequestParameters("", null, null, null);
        MoPubRewardedVideos.setRewardedVideoListener(moPubRewardedVideoListener);
        MoPubRewardedVideos.loadRewardedVideo(adUnitId, requestParameters);
    }
}
