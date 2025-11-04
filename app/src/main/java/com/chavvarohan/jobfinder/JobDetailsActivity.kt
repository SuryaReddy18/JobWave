package com.chavvarohan.jobfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.chavvarohan.jobfinder.R
import com.chavvarohan.jobfinder.databinding.ActivityJobDetailsBinding

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logoUrl = intent.getStringExtra("logoUrl")
        val jobTitle = intent.getStringExtra("jobTitle")
        val companyName = intent.getStringExtra("companyName")
        val location = intent.getStringExtra("location")
        val skills = intent.getStringExtra("skills")
        val applyUrl = intent.getStringExtra("applyUrl")

        binding.textViewTitle.text = jobTitle
        binding.textViewCompany.text = companyName
        binding.textViewLocation.text = location
        binding.textViewSkills.text = skills
        binding.buttonApply.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(applyUrl))
            startActivity(intent)
        }

        Glide.with(this)
            .load(logoUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imageViewLogo)
    }
}
