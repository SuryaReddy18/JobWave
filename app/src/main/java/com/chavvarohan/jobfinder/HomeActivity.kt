package com.chavvarohan.jobfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.MenuItem
import androidx.fragment.app.commit

import com.chavvarohan.jobfinder.databinding.ActivityHomeBinding
import com.chavvarohan.jobfinder.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener(this)

    }

    private fun onHomeClicked(): Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_layout, (HomeFragment()))
        }
        return true
    }

    private fun onLocationClicked(): Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_layout, (LocationFragment()))
        }
        return true
    }

    private fun onProfileClicked(): Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_layout, (ProfileFragment()))
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.home -> {
            onHomeClicked()
            true
        }
        R.id.location -> {
            onLocationClicked()
            true
        }
        R.id.profile -> {
            onProfileClicked()
            true
        }
        else -> false

    }
}