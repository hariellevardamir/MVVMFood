package com.example.mvvmfoodapp.fragments.bottomSheet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mvvmfoodapp.R
import com.example.mvvmfoodapp.activities.MainActivity
import com.example.mvvmfoodapp.activities.MealActivity
import com.example.mvvmfoodapp.databinding.FragmentMealBottomSheetBinding
import com.example.mvvmfoodapp.fragments.HomeFragment
import com.example.mvvmfoodapp.pojo.Meal
import com.example.mvvmfoodapp.viewModel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val MEAL_ID = "param1"

class MealBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMealBottomSheetBinding
    private lateinit var viewModel: HomeViewModel
    private var mealId: String? = null
    private var mealName: String? = null
    private var mealThumb: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mealId = it.getString(MEAL_ID)
        }

        viewModel = (activity as MainActivity).viewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMealBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mealId != null) {
            viewModel.getMealById(mealId!!)
        }
        observeBottomSheetMeal()
        onBottomSheetDialogClick()

    }

    private fun observeBottomSheetMeal() {
        if (mealId != null) {
            viewModel.observeBottomSheetMealLiveData()
                .observe(viewLifecycleOwner, object : Observer<Meal> {
                    override fun onChanged(t: Meal?) {
                        val meal = t
                        if (meal != null) {
                            Glide.with(this@MealBottomSheetFragment)
                                .load(meal.strMealThumb)
                                .into(binding.imgBottomSheet)
                            binding.tvBottomSheetMealName.text = meal.strMeal
                            binding.tvBottomSheetArea.text = meal.strArea
                            binding.tvBottomSheetCategory.text = meal.strCategory

                            mealName = meal.strMeal
                            mealThumb = meal.strMealThumb
                        }
                    }
                })
        }
    }

    private fun onBottomSheetDialogClick() {
        binding.bottomSheet.setOnClickListener {
            if (mealName != null && mealThumb != null) {
                val intent = Intent(activity, MealActivity::class.java)
                intent.apply {
                    putExtra(HomeFragment.MEAL_ID, mealId)
                    putExtra(HomeFragment.MEAL_NAME, mealName)
                    putExtra(HomeFragment.MEAL_THUMB, mealThumb)
                }
                startActivity(intent)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) = MealBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString(MEAL_ID, param1)
            }
        }
    }

}