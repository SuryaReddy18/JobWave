package com.chavvarohan.jobfinder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chavvarohan.jobfinder.databinding.DesignListBinding

class JobAdapter(
    private val context: Context,
    private var jobList: List<JobApiItem>
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    private var filteredJobList: List<JobApiItem> = jobList

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(job: JobApiItem)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = DesignListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        if (position >= 0 && position < filteredJobList.size) {
            val currentJob = filteredJobList[position]
            holder.bind(currentJob)
        }
    }

    override fun getItemCount(): Int = filteredJobList.size

    fun updateJobs(newJobList: List<JobApiItem>) {
        jobList = newJobList
        filteredJobList = newJobList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredJobList = if (query.isEmpty()) {
            jobList
        } else {
            jobList.filter {
                it.Role.contains(query, ignoreCase = true) ||
                        it.Company_name.contains(query, ignoreCase = true) ||
                        it.Location.contains(query, ignoreCase = true) ||
                        it.Skills.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    inner class JobViewHolder(
        private val binding: DesignListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(filteredJobList[position])
                }
            }
        }

        fun bind(job: JobApiItem) {
            binding.textViewTitle.text = job.Role
            binding.textViewCompany.text = job.Company_name
            binding.textViewLocation.text = job.Location
            binding.textViewSkill.text = job.Skills

            Glide.with(context)
                .load(job.Logo)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imageViewImage)
        }
    }
}
