package com.example.enragedyetti

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.enragedyetti.databinding.FragmentProductsBinding
import com.google.android.material.button.MaterialButton

class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Scroll to target section heading if passed from Home screen
        val targetSection = arguments?.getString("targetSection") ?: ""
        if (targetSection.isNotEmpty()) {
            binding.root.post {
                when (targetSection) {
                    "packages" -> binding.root.smoothScrollTo(0, binding.headerPackages.top)
                    "experiences" -> binding.root.smoothScrollTo(0, binding.headerExperiences.top)
                    "merch" -> binding.root.smoothScrollTo(0, binding.headerMerch.top)
                }
            }
        }

        // Gaming Packages
        binding.btnAddUltimate.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Ultimate Gamer Pass", 499.0, R.drawable.memberships_pic))
        }
        binding.btnAddVip.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("VIP Gaming Experience", 799.0, R.drawable.memberships_pic))
        }
        binding.btnAddEsports.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Esports Training Package", 599.0, R.drawable.memberships_pic))
        }
        binding.btnAddBirthday.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Birthday Party Package", 1299.0, R.drawable.memberships_pic))
        }

        // Individual Experiences
        binding.btnAddVr.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Virtual Reality Experience", 149.0, R.drawable.arena))
        }
        binding.btnAddRacing.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Racing Simulator Challenge", 179.0, R.drawable.arena))
        }
        binding.btnAddEscape.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Escape Room Challenge", 199.0, R.drawable.arena))
        }

        // Official Merchandise
        binding.btnAddHoodie.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("NextLvl Signature Hoodie", 599.0, R.drawable.hoodie_nxtlvl))
        }
        binding.btnAddShirts.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("NextLvl Merch Shirts", 249.0, R.drawable.nextlvl_merch_shirts))
        }
        binding.btnAddKeyboard.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Pro RGB Mechanical Keyboard Set", 1299.0, R.drawable.keyboard_set))
        }
        binding.btnAddPuffer.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Enraged PufferJacket", 1799.0, R.drawable.enraged_pufferjacket))
        }
        binding.btnAddPoster.setOnClickListener {
            handleAddToCart(it as MaterialButton, CartItem("Limited Edition Arena Poster", 299.0, R.drawable.rivalry_poster))
        }
    }

    private fun handleAddToCart(button: MaterialButton, item: CartItem) {
        // Quick scale pulse animation
        button.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
            button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
        }.start()

        // Add to CartManager
        CartManager.addItem(item)

        // Updated button feedback: Change text to "Added to Cart" for 1.2s
        val originalText = button.text
        button.text = "Added to Cart"
        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                button.text = originalText
            }
        }, 1200)

        Toast.makeText(context, "Added ${item.name} to Cart!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}