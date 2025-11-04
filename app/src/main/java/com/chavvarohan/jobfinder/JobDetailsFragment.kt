package com.chavvarohan.jobfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.chavvarohan.jobfinder.databinding.FragmentJobDetailsBinding

class JobDetailsFragment : Fragment() {

    private lateinit var binding: FragmentJobDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoUrl = arguments?.getString("logoUrl")
        val jobTitle = arguments?.getString("jobTitle")
        val companyName = arguments?.getString("companyName")
        val location = arguments?.getString("location")
        val skills = arguments?.getString("skills")
        val applyUrl = arguments?.getString("applyUrl")
        val date = arguments?.getString("date")

        binding.textViewTitle.text = jobTitle
        binding.textViewCompany.text = companyName
        binding.textViewLocation.text = location
        binding.textViewSkills.text = skills
        binding.textViewDate.text = date
        binding.buttonApply.setOnClickListener {
            try {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(applyUrl)
                startActivity(openURL)
            }catch (Exception: Exception) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        Glide.with(this)
            .load(logoUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imageViewLogo)
    }
}
