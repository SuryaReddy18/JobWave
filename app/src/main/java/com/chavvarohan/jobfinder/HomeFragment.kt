package com.chavvarohan.jobfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chavvarohan.jobfinder.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var jobList: ArrayList<JobApiItem>
    private lateinit var jobAdapter: JobAdapter

    private val BASE_URL = "https://script.google.com/macros/s/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
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
                    binding.progressBar.visibility = View.GONE
                    if (data.isNotEmpty()) {
                        jobAdapter.updateJobs(data)
                    } else {
                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<JobApiItem>>, t: Throwable) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
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
}
