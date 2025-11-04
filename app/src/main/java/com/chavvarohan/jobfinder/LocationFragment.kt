package com.chavvarohan.jobfinder

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chavvarohan.jobfinder.databinding.FragmentLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class LocationFragment : Fragment() {

    private lateinit var binding: FragmentLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var city = ""

    private lateinit var jobList: ArrayList<JobApiItem>
    private lateinit var jobAdapter: JobAdapter

    private val BASE_URL = "https://script.google.com/macros/s/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLocation()
        }


        binding.swipeRefresh.setOnRefreshListener {
            getJobData()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobList = arrayListOf()
        jobAdapter = JobAdapter(requireContext(), jobList)


        jobAdapter.setOnItemClickListener(object : JobAdapter.onItemClickListener {
            override fun onItemClick(job: JobApiItem) {
                navigateToJobDetailsFragment(job)
            }
        })

        binding.recyclerView.adapter = jobAdapter

        getJobData()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                jobAdapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun getJobData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val call = retrofit.getJobs()
        call.enqueue(object : Callback<List<JobApiItem>> {
            override fun onResponse(
                call: Call<List<JobApiItem>>,
                response: Response<List<JobApiItem>>
            ) {
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }

                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    Log.d("LocationFragment", "Received jobs: $data")
                    binding.progressBar.visibility = View.GONE
                    if (data.isNotEmpty()) {

                        val filteredJobs = data.filter { job ->
                            job.Location.contains(city, ignoreCase = true)
                        }


                        val sortedJobs = filteredJobs.sortedBy { it.Location }
                        jobAdapter.updateJobs(sortedJobs)
                    } else {
                        Toast.makeText(context, "No jobs found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to retrieve jobs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<JobApiItem>>, t: Throwable) {
                Toast.makeText(context, "Failed to retrieve jobs: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToJobDetailsFragment(job: JobApiItem) {
        val fragment = JobDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("logoUrl", job.Logo)
                putString("jobTitle", job.Role)
                putString("companyName", job.Company_name)
                putString("location", job.Location)
                putString("skills", job.Skills)
                putString("applyUrl",job.Link)
                putString("date",job.Date)

            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("LocationFragment", "Location retrieved: Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                getCityName(location.latitude, location.longitude)
            } else {
                Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("LocationFragment", "Failed to get location", e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private fun getCityName(lat: Double, long: Double) {
        try {
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            val addressList = geoCoder.getFromLocation(lat, long, 1)

            if (addressList != null && addressList.isNotEmpty()) {
                city = addressList[0].locality ?: ""
                Log.d("LocationFragment", "City: $city")

                getJobData()
            } else {
                Toast.makeText(context, "No address found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading city: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("LocationFragment", "Error fetching city name", e)
        }
    }
}
