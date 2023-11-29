package com.example.mvvmfoodapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mvvmfoodapp.R
import com.example.mvvmfoodapp.activities.MainActivity
import com.example.mvvmfoodapp.activities.MealActivity
import com.example.mvvmfoodapp.adapter.MealsAdapter
import com.example.mvvmfoodapp.databinding.FragmentSearchBinding
import com.example.mvvmfoodapp.pojo.Meal
import com.example.mvvmfoodapp.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var searchRecyclerViewAdapter: MealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        binding.imgSearchArrow.setOnClickListener {
            searchMeals()
        }
        observeSearchMealsLiveData()
        onSearchMealsClick()

        var searchJobs: Job? = null
        binding.etSearchBox.addTextChangedListener { searchQuery ->
            searchJobs?.cancel()
            searchJobs = lifecycleScope.launch {
                delay(500)
                viewModel.searchMeals(searchQuery = searchQuery.toString())
            }
        }
    }

    private fun prepareRecyclerView() {
        searchRecyclerViewAdapter = MealsAdapter()
        searchRecyclerViewAdapter.apply {
            binding.rvSearchedMeals.apply {
                layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
                adapter = searchRecyclerViewAdapter
            }
        }
    }

    private fun searchMeals() {
        val searchQuery = binding.etSearchBox.text.toString()
        if (searchQuery.isNotEmpty()) {
            viewModel.searchMeals(searchQuery)
        }
    }

    private fun observeSearchMealsLiveData() {
        viewModel.observeSearchMeals().observe(viewLifecycleOwner, object : Observer<List<Meal>> {
            override fun onChanged(t: List<Meal>?) {
                val mealsList = t
                searchRecyclerViewAdapter.differ.submitList(mealsList)
                if (mealsList != null) {
                    binding.tvCategoriesSize.text = mealsList!!.size.toString()
                } else {
                    binding.tvCategoriesSize.text = "0"
                }
            }
        })
    }

    private fun onSearchMealsClick() {
        searchRecyclerViewAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, meal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

}