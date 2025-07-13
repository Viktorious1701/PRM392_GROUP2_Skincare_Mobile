package com.example.prm392_group2_skincare_mobile.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.CosmeticApiService
import com.example.prm392_group2_skincare_mobile.data.repository.CosmeticRepository
import com.example.prm392_group2_skincare_mobile.databinding.FragmentHomeBinding
import com.example.prm392_group2_skincare_mobile.ui.product_detail.ProductDetailActivity
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val cosmeticService = RetrofitClient.create(CosmeticApiService::class.java)
                val repository = CosmeticRepository(cosmeticService)
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
        }
    }

    private lateinit var bannerAdapter: PromotionalBannerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var featuredProductAdapter: FeaturedProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private fun setupUI() {
        // Setup Featured Products RecyclerView
        featuredProductAdapter = FeaturedProductAdapter { product ->
            val intent = Intent(activity, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_ID", product.id)
            }
            startActivity(intent)
        }
        binding.rvFeaturedProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvFeaturedProducts.adapter = featuredProductAdapter

        // Setup Categories RecyclerView
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun observeViewModel() {
        homeViewModel.banners.observe(viewLifecycleOwner) { banners ->
            bannerAdapter = PromotionalBannerAdapter(banners)
            binding.viewPagerBanners.adapter = bannerAdapter
            // Attach TabLayout to ViewPager2
            TabLayoutMediator(binding.tabLayoutDots, binding.viewPagerBanners) { _, _ -> }.attach()
        }

        homeViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter = CategoryAdapter(categories)
            binding.rvCategories.adapter = categoryAdapter
        }

        homeViewModel.featuredProducts.observe(viewLifecycleOwner) { result ->
            result.onSuccess { products ->
                featuredProductAdapter.submitList(products)
            }.onFailure { error ->
                Toast.makeText(context, "Error loading products: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}