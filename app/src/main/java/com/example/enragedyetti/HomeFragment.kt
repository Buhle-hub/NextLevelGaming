package com.example.enragedyetti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.enragedyetti.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Category Filter Pills -> Navigate directly to dedicated heading in Products Fragment
        binding.pillAll.setOnClickListener { navigateToProductsSection("") }
        binding.pillPackages.setOnClickListener { navigateToProductsSection("packages") }
        binding.pillExperiences.setOnClickListener { navigateToProductsSection("experiences") }
        binding.pillMerch.setOnClickListener { navigateToProductsSection("merch") }
        binding.pillEsports.setOnClickListener { navigateToProductsSection("packages") }

        // 2. "On the Horizon" Carousel Cards & Action Buttons
        binding.cardHeroPass.setOnClickListener { navigateToProductsSection("packages") }
        binding.btnHeroViewPass.setOnClickListener { navigateToProductsSection("packages") }

        binding.cardHeroArena.setOnClickListener { navigateToProductsSection("experiences") }
        binding.btnHeroViewArena.setOnClickListener { navigateToProductsSection("experiences") }

        // 3. Live Event Banner -> Navigates to Enquiry / Booking
        binding.cardLiveEvent.setOnClickListener {
            findNavController().navigate(R.id.EnquiryFragment)
        }

        // 4. Section Headers & List Items
        binding.btnSeeAllProducts.setOnClickListener { navigateToProductsSection("") }

        binding.itemRowKeyboard.setOnClickListener { navigateToProductsSection("merch") }
        binding.btnViewProducts.setOnClickListener {
            CartManager.addItem(CartItem("Pro RGB Mechanical Keyboard Set", 1299.0, R.drawable.keyboard_set))
            Toast.makeText(context, "Added Pro RGB Keyboard Set to Cart!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.CartFragment)
        }

        binding.itemRowPuffer.setOnClickListener { navigateToProductsSection("merch") }
        binding.btnLearnMore.setOnClickListener { navigateToProductsSection("merch") }
    }

    private fun navigateToProductsSection(sectionKey: String) {
        val bundle = bundleOf("targetSection" to sectionKey)
        findNavController().navigate(R.id.ProductsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}