package com.example.enragedyetti

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        // 1. Category Filter Pills -> Navigate to Products Catalog
        val pills = listOf(binding.pillAll, binding.pillPackages, binding.pillExperiences, binding.pillMerch, binding.pillEsports)
        pills.forEach { pill ->
            pill.setOnClickListener {
                pills.forEach { p ->
                    p.isSelected = false
                    p.setTextColor(Color.WHITE)
                }
                pill.isSelected = true
                pill.setTextColor(Color.BLACK)

                // Navigate directly to Products catalog on pill selection
                findNavController().navigate(R.id.ProductsFragment)
            }
        }

        // 2. "On the Horizon" Carousel Cards & Action Buttons
        binding.cardHeroPass.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }
        binding.btnHeroViewPass.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }

        binding.cardHeroArena.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }
        binding.btnHeroViewArena.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }

        // 3. Live Event Banner -> Navigates to Enquiry / Booking
        binding.cardLiveEvent.setOnClickListener {
            findNavController().navigate(R.id.EnquiryFragment)
        }

        // 4. Section Headers & List Items
        binding.btnSeeAllProducts.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }

        binding.itemRowKeyboard.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }
        binding.btnViewProducts.setOnClickListener {
            CartManager.addItem(CartItem("Pro RGB Mechanical Keyboard Set", 1299.0, R.drawable.keyboard_set))
            Toast.makeText(context, "Added Pro RGB Keyboard Set to Cart!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.CartFragment)
        }

        binding.itemRowPuffer.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }
        binding.btnLearnMore.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}