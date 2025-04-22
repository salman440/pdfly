package com.systemnox.pdfly.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowMetrics
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.systemnox.pdfly.R
import com.systemnox.pdfly.adapter.FragmentAdapter
import com.systemnox.pdfly.databinding.ActivityHomeBinding
import com.systemnox.pdfly.fragments.FolderFragment
import com.systemnox.pdfly.vms.PdfListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback


class Home : AppCompatActivity() {

    private var adView: AdView? = null

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }
    private val pdfListViewModel: PdfListViewModel by viewModel()
    private var appOpenAd: AppOpenAd? = null

    private var isAdDisplayed: Boolean = false
    private val appOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
        override fun onAdLoaded(ad: AppOpenAd) {
            appOpenAd = ad // Initialize the appOpenAd property here
            appOpenAd!!.show(this@Home)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            // Handle ad loading failure
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        MobileAds.initialize(this@Home) {}

        setupBottomNav()

        loadBanner()
        loadAppOpenAd()

        onBackPressedDispatcher.addCallback(this) {
            val currentFragment = getCurrentFragment()
            if (currentFragment is FolderFragment) {
                if (currentFragment.handleFragmentBackPressed()) {
                    return@addCallback
                }
            }
            if (binding.viewPager.currentItem != 0) {
                binding.viewPager.setCurrentItem(0, false)
                return@addCallback
            } else {
                finish()
            }
        }


    }

    //    fun refreshMediaStore(context: Context) {
//        // Use MediaScannerConnection to scan the entire external storage directory
//        MediaScannerConnection.scanFile(
//            context,
//            arrayOf(Environment.getExternalStorageDirectory().toString()),
//            null
//        ) { _, _ ->
//        }
//    }
    private fun loadAppOpenAd() {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            this,
            getString(R.string.appopenad),
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            appOpenAdLoadCallback
        )
    }

    override fun onResume() {
        super.onResume()
        if (isAdDisplayed) {
            // Do not show the ad if it's already displayed
            return
        }
        appOpenAd?.let {
            it.show(this)
        } ?: run {
            // If the ad is null, load it again
            loadAppOpenAd()
        }
    }

    private fun loadBanner() {
        // [START create_ad_view]
        // Create a new ad view.
        val adView = AdView(this)
        adView.adUnitId = getString(R.string.bannerad)
        adView.setAdSize(adSize)
        this.adView = adView

        // Replace ad container with new ad view.
        binding.bannerAdView.removeAllViews()
        binding.bannerAdView.addView(adView)
        // [END create_ad_view]

        // [START load_ad]
        // Start loading the ad in the background.
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // [END load_ad]
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
    }


    private fun setupBottomNav() {

        binding.viewPager.adapter = FragmentAdapter(supportFragmentManager, lifecycle)

        binding.viewPager.isUserInputEnabled = false

        val itemToPageMap = mapOf(
            R.id.nav_home to 0, R.id.nav_files to 1, R.id.nav_folder to 2
        )

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    val intent = Intent(this@Home, SearchPDF::class.java)
                    startActivity(intent)
                    false
                }

                else -> {
                    itemToPageMap[item.itemId]?.let {
                        binding.viewPager.setCurrentItem(it, false)
                        true
                    } ?: false
                }
            }

        }


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavView.menu.getItem(position).isChecked = true
            }
        })


    }


}