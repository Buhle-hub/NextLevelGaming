package com.example.enragedyetti

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.enragedyetti.databinding.FragmentProfileBinding
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set active user details
        val user = UserSessionManager.currentUser
        if (user != null) {
            binding.tvProfileName.text = user.username.ifEmpty { "Yetti Member" }
            binding.tvProfileEmail.text = user.email.ifEmpty { user.phone.ifEmpty { "member@nextlvl.com" } }
            binding.tvCurrentUsernameLabel.text = "Current: ${user.username}"
        } else {
            binding.tvProfileName.text = "Fresh Yetti Member"
            binding.tvProfileEmail.text = "Sign in or create account"
            binding.tvCurrentUsernameLabel.text = "Current: None"
        }

        // Render dynamic orders list
        renderUserOrders()

        // Navigation / Tab Switching between sub-cards
        binding.btnMenuOrder.setOnClickListener {
            showCard(binding.cardOrders)
        }

        binding.btnMenuChangeUsername.setOnClickListener {
            showCard(binding.cardChangeUsername)
        }

        binding.btnMenuChangePassword.setOnClickListener {
            showCard(binding.cardChangePassword)
        }

        binding.btnMenuLogTicket.setOnClickListener {
            showCard(binding.cardLogTicket)
        }

        binding.btnMenuLogOut.setOnClickListener {
            context?.let { ctx ->
                UserSessionManager.logout(ctx)
            }
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.SignInFragment)
        }

        // Action Handlers
        binding.btnSaveUsername.setOnClickListener {
            val newUsername = binding.etNewUsername.text.toString().trim()
            if (newUsername.isEmpty()) {
                binding.etNewUsername.error = "Enter a username"
                return@setOnClickListener
            }
            if (newUsername.contains(" ")) {
                binding.etNewUsername.error = "Username cannot contain spaces"
                return@setOnClickListener
            }

            binding.etNewUsername.error = null
            context?.let { ctx ->
                val (success, message) = UserSessionManager.changeUsername(ctx, newUsername)
                if (success) {
                    binding.tvCurrentUsernameLabel.text = "Current: $newUsername"
                    binding.tvProfileName.text = newUsername
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    binding.etNewUsername.text?.clear()
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnSavePassword.setOnClickListener {
            val oldPass = binding.etCurrentPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()

            var isValid = true

            if (oldPass.isEmpty()) {
                binding.etCurrentPassword.error = "Current password required"
                isValid = false
            } else {
                binding.etCurrentPassword.error = null
            }

            if (newPass.isEmpty()) {
                binding.etNewPassword.error = "New password required"
                isValid = false
            } else if (newPass.length < 6) {
                binding.etNewPassword.error = "New password must be at least 6 characters"
                isValid = false
            } else {
                binding.etNewPassword.error = null
            }

            if (isValid) {
                val (success, message) = UserSessionManager.changePassword(oldPass, newPass)
                if (success) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    binding.etCurrentPassword.text?.clear()
                    binding.etNewPassword.text?.clear()
                } else {
                    binding.etCurrentPassword.error = message
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnSubmitTicket.setOnClickListener {
            val subject = binding.etTicketSubject.text.toString().trim()
            val details = binding.etTicketDetails.text.toString().trim()

            var isValid = true

            if (subject.isEmpty()) {
                binding.etTicketSubject.error = "Subject is required"
                isValid = false
            } else {
                binding.etTicketSubject.error = null
            }

            if (details.isEmpty()) {
                binding.etTicketDetails.error = "Ticket details required"
                isValid = false
            } else {
                binding.etTicketDetails.error = null
            }

            if (isValid) {
                UserSessionManager.addTicket(subject, details)
                Toast.makeText(context, "Ticket logged successfully!", Toast.LENGTH_SHORT).show()
                binding.etTicketSubject.text?.clear()
                binding.etTicketDetails.text?.clear()
            }
        }
    }

    private fun renderUserOrders() {
        val container = binding.ordersContainer ?: return
        container.removeAllViews()

        val userOrders = UserSessionManager.currentUser?.orders ?: emptyList()

        if (userOrders.isEmpty()) {
            val emptyTv = TextView(context).apply {
                text = "No orders placed yet. Explore Products to place your first order!"
                setTextColor(Color.parseColor("#A0A0B0"))
                textSize = 14f
                setPadding(0, 12, 0, 12)
            }
            container.addView(emptyTv)
            return
        }

        for (order in userOrders) {
            val orderCard = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundResource(R.drawable.behance_card_bg)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 12)
                layoutParams = params
            }

            val tvHeader = TextView(context).apply {
                text = "${order.id} — Total: R${String.format(Locale.US, "%.2f", order.total)}"
                setTextColor(Color.parseColor("#00FF66"))
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvDate = TextView(context).apply {
                text = "Date: ${order.timestamp}"
                setTextColor(Color.parseColor("#80FFFFFF"))
                textSize = 12f
                setPadding(0, 2, 0, 6)
            }

            orderCard.addView(tvHeader)
            orderCard.addView(tvDate)

            for (item in order.items) {
                val itemTv = TextView(context).apply {
                    text = "• ${item.name} (R${String.format(Locale.US, "%.2f", item.price)})"
                    setTextColor(Color.WHITE)
                    textSize = 13f
                    setPadding(8, 2, 0, 2)
                }
                orderCard.addView(itemTv)
            }

            container.addView(orderCard)
        }
    }

    private fun showCard(activeCard: View) {
        binding.cardOrders.visibility = View.GONE
        binding.cardChangeUsername.visibility = View.GONE
        binding.cardChangePassword.visibility = View.GONE
        binding.cardLogTicket.visibility = View.GONE

        activeCard.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}