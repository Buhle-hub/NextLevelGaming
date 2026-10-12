package com.example.enragedyetti

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.enragedyetti.databinding.FragmentPaymentsuccessfulBinding
import java.util.Locale

class PaymentSuccessfulFragment : Fragment() {

    private var _binding: FragmentPaymentsuccessfulBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsuccessfulBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get active username & last completed order
        val username = UserSessionManager.getCurrentUsername().ifEmpty { "Yetti Member" }
        val order = UserSessionManager.lastCompletedOrder ?: UserSessionManager.currentUser?.orders?.firstOrNull()

        binding.tvPaymentSuccessDetails.text = "Thank you, $username!\nYour order was processed successfully."

        if (order != null) {
            binding.tvOrderNumber.text = "Order Number: ${order.id} • ${order.timestamp}"
            binding.tvGrandTotalPaid.text = String.format(Locale.US, "R%.2f", order.total)

            // Render purchased cart items
            val container = binding.purchasedItemsContainer
            container.removeAllViews()

            for (item in order.items) {
                val rowView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 10, 0, 10)
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }

                val iv = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(48, 48)
                    setImageResource(item.imageRes)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }

                val tvName = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                        setMargins(14, 0, 0, 0)
                    }
                    text = item.name
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                val tvPrice = TextView(context).apply {
                    text = String.format(Locale.US, "R%.2f", item.price)
                    setTextColor(Color.parseColor("#00FF66"))
                    textSize = 14f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                rowView.addView(iv)
                rowView.addView(tvName)
                rowView.addView(tvPrice)

                container.addView(rowView)
            }
        }

        binding.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.HomeFragment)
        }

        binding.btnShopMore.setOnClickListener {
            findNavController().navigate(R.id.ProductsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}