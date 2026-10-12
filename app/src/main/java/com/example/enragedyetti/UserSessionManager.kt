package com.example.enragedyetti

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class UserAccount(
    var username: String = "",
    var email: String = "",
    var phone: String = "",
    var password: String = "",
    val orders: MutableList<OrderHistory> = mutableListOf(),
    val tickets: MutableList<SupportTicket> = mutableListOf(),
    val cards: MutableList<SavedCard> = mutableListOf()
)

data class OrderHistory(
    val id: String,
    val total: Double,
    val items: List<CartItem>,
    val timestamp: String
)

data class SupportTicket(
    val subject: String,
    val body: String,
    val timestamp: String
)

data class SavedCard(
    val number: String,
    val name: String
)

object UserSessionManager {
    private const val PREFS_NAME = "nextlvl_session_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USERNAME = "saved_username"
    private const val KEY_EMAIL = "saved_email"

    var currentUser: UserAccount? = null
    var lastCompletedOrder: OrderHistory? = null
    val registeredAccounts = mutableListOf<UserAccount>()
    var tempSignUpContact: String = ""
    var tempSignUpMethod: String = "email"

    fun initSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (isLoggedIn) {
            val username = prefs.getString(KEY_USERNAME, "") ?: ""
            val email = prefs.getString(KEY_EMAIL, "") ?: ""
            if (username.isNotEmpty()) {
                val user = registeredAccounts.find { it.username.equals(username, ignoreCase = true) }
                    ?: UserAccount(username = username, email = email).also { registeredAccounts.add(it) }
                currentUser = user
            }
        }
    }

    fun saveSession(context: Context, username: String, email: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun clearSession(context: Context) {
        currentUser = null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = currentUser != null

    fun getCurrentUsername(): String = currentUser?.username ?: ""

    fun registerUser(context: Context, username: String, pass: String): UserAccount {
        val user = UserAccount(
            username = username,
            password = pass,
            email = if (tempSignUpMethod == "email") tempSignUpContact else "",
            phone = if (tempSignUpMethod == "phone") tempSignUpContact else ""
        )
        registeredAccounts.add(user)
        currentUser = user
        saveSession(context, username, user.email)
        return user
    }

    fun loginUser(context: Context, identifier: String, pass: String): Pair<Boolean, String> {
        val match = registeredAccounts.find {
            (it.email.isNotEmpty() && it.email.equals(identifier, ignoreCase = true)) ||
            (it.phone.isNotEmpty() && it.phone.equals(identifier, ignoreCase = true)) ||
            (it.username.isNotEmpty() && it.username.equals(identifier, ignoreCase = true))
        }

        return if (match != null) {
            if (match.password == pass) {
                currentUser = match
                saveSession(context, match.username, match.email)
                Pair(true, "Account found — redirecting...")
            } else {
                Pair(false, "Incorrect password.")
            }
        } else {
            Pair(false, "Account not found with $identifier. Please sign up.")
        }
    }

    fun logout(context: Context) {
        clearSession(context)
    }

    fun generateOrderId(): String {
        val randomNum = Random.nextInt(100000, 999999)
        return "ORD-2026-$randomNum"
    }

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.US)
        return sdf.format(Date())
    }

    fun addOrder(order: OrderHistory) {
        lastCompletedOrder = order
        val user = currentUser ?: UserAccount(username = "Guest").also {
            registeredAccounts.add(it)
            currentUser = it
        }
        user.orders.add(0, order)
    }

    fun addTicket(subject: String, body: String): SupportTicket {
        val ticket = SupportTicket(subject, body, getCurrentTimestamp())
        val user = currentUser ?: UserAccount(username = "Guest").also {
            registeredAccounts.add(it)
            currentUser = it
        }
        user.tickets.add(0, ticket)
        return ticket
    }

    fun addCard(number: String, name: String): SavedCard {
        val card = SavedCard(number, name)
        val user = currentUser ?: UserAccount(username = "Guest").also {
            registeredAccounts.add(it)
            currentUser = it
        }
        user.cards.add(card)
        return card
    }

    fun changePassword(oldPass: String, newPass: String): Pair<Boolean, String> {
        val user = currentUser ?: return Pair(false, "No active user session")
        if (user.password.isNotEmpty() && user.password != oldPass) {
            return Pair(false, "Current password does not match")
        }
        user.password = newPass
        return Pair(true, "Password changed successfully")
    }

    fun changeUsername(context: Context, newUsername: String): Pair<Boolean, String> {
        val user = currentUser ?: return Pair(false, "No active user session")
        user.username = newUsername
        saveSession(context, newUsername, user.email)
        return Pair(true, "Username updated to $newUsername")
    }

    fun sendWelcomeEmailIntent(context: Context, recipientEmail: String, username: String) {
        val targetEmail = if (recipientEmail.isEmpty()) "dior.dynasty54@gmail.com" else recipientEmail
        val subject = "Welcome to NextLVL Gaming - Fellow Yetti!"
        val body = "Hi $username,\n\nWelcome to NextLVL Gaming! We are so glad to have a fellow Yetti join our tribe!\n\nFrom: dior.dynasty54@gmail.com (NextLVL Gaming Team)"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$targetEmail")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(targetEmail, "dior.dynasty54@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Send Interactive Welcome Email"))
        } catch (e: Exception) {
            Toast.makeText(context, "Welcome email triggered from dior.dynasty54@gmail.com", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSystemOrderSummaryEmail(context: Context, order: OrderHistory) {
        val username = getCurrentUsername().ifEmpty { "Yetti Member" }
        val subject = "Order summary - ${order.id}"

        val itemsBody = StringBuilder()
        order.items.forEach { item ->
            itemsBody.append("• ${item.name} - R${String.format(Locale.US, "%.2f", item.price)}\n")
        }

        val body = """
            NextLVL Order Summary:
            ------------------------------
            Order ID: ${order.id}
            Date & Time: ${order.timestamp}
            Account Holder Username: $username
            
            Order Contents:
            $itemsBody
            Grand Total: R${String.format(Locale.US, "%.2f", order.total)}
            ------------------------------
            Sent automatically by NextLVL Gaming System to dior.dynasty54@gmail.com.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:dior.dynasty54@gmail.com")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("dior.dynasty54@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "System Order Summary for dior.dynasty54@gmail.com"))
        } catch (e: Exception) {
            Toast.makeText(context, "Order summary sent to dior.dynasty54@gmail.com", Toast.LENGTH_SHORT).show()
        }
    }
}