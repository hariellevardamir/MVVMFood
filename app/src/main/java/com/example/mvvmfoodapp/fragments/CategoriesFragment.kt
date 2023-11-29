package com.example.mvvmfoodapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mvvmfoodapp.activities.CategoryMealsActivity
import com.example.mvvmfoodapp.activities.MainActivity
import com.example.mvvmfoodapp.activities.MealActivity
import com.example.mvvmfoodapp.adapter.CategoriesAdapter
import com.example.mvvmfoodapp.databinding.FragmentCategoriesBinding
import com.example.mvvmfoodapp.pojo.Category
import com.example.mvvmfoodapp.viewModel.HomeViewModel

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var categoryAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = (FragmentCategoriesBinding.inflate(inflater))
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        observeCategories()
        onCategoryClick()

    }

    private fun prepareRecyclerView() {
        categoryAdapter = CategoriesAdapter()
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoryAdapter
        }
    }

    private fun observeCategories() {
        viewModel.observeCategoriesLiveData()
            .observe(viewLifecycleOwner, object : Observer<List<Category>> {
                override fun onChanged(t: List<Category>?) {
                    val category = t
                    categoryAdapter.setCategoryList(categoryList = category as ArrayList<Category>)
                }
            })
    }

    private fun onCategoryClick() {
        categoryAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(HomeFragment.CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

}