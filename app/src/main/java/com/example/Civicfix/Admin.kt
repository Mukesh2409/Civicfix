package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.Civicfix.databinding.ActivityAdminBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class Admin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAdminBinding
    private lateinit var database: FirebaseDatabase
    private val emailPattern = "[A-Za-z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.servicebutton.setOnClickListener {
            val email = binding.adminemailEt.text.toString()
            val password = binding.adminpass.text.toString()
            val cpassword = binding.adminconfirmPass.text.toString()
            val name = binding.name.text.toString()
            val number = binding.phone.text.toString()
            val organizationname = binding.organisationname.text.toString()

            if (email.isEmpty() || password.isEmpty() || cpassword.isEmpty() || name.isEmpty() || number.isEmpty() || organizationname.isEmpty()) {
                if (email.isEmpty()) {
                    binding.adminemailEt.error = "Enter your Email-id"
                }
                if (name.isEmpty()) {
                    binding.name.error = "Enter your Name"
                }
                if (number.isEmpty()) {
                    binding.phone.error = "Enter your number"
                }
                if (organizationname.isEmpty()) {
                    binding.organisationname.error = "Enter your Organization Name"
                }
                if (password.isEmpty()) {
                    binding.adminpass.error = "Enter your Password"
                }
                if (cpassword.isEmpty()) {
                    binding.adminconfirmPass.error = "Re Enter your Password"
                }
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())) {
                binding.adminemailEt.error = "Enter valid Email Address"
                Toast.makeText(this, "Enter valid Email Address", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                binding.adminpass.error = "Choose password of at least 6 characters"
                Toast.makeText(this, "Choose password of at least 6 characters", Toast.LENGTH_SHORT).show()
            } else if (number.length != 10) {
                binding.phone.error = "Enter a valid 10-digit number"
                Toast.makeText(this, "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
            } else if (password != cpassword) {
                binding.adminconfirmPass.error = "Password not matched, try again"
                Toast.makeText(this, "Password not matched, try again", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                        val adminData = AdminData(
                            email = email,
                            name = name,
                            number = number,
                            organizationname = organizationname
                        )

                        val verification = Verification(
                            type = "Admin",
                            phone = number,
                            verified = "No"
                        )

                        val typeRef = database.reference.child("Verification").child(auth.currentUser!!.uid)
                        val databaseRef = database.reference.child("Admin").child(auth.currentUser!!.uid)

                        databaseRef.setValue(adminData).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                typeRef.setValue(verification).addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        // Start phone verification
                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            "+91$number",
                                            60, // Timeout duration
                                            TimeUnit.SECONDS,
                                            this@Admin,
                                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                                    Log.d("Admin", "Verification completed")
                                                    signInWithPhoneAuthCredential(credential)
                                                }

                                                override fun onVerificationFailed(e: FirebaseException) {
                                                    Log.e("Admin", "Verification failed: ${e.message}")
                                                    Toast.makeText(this@Admin, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }

                                                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                                    Log.d("Admin", "onCodeSent triggered with verificationId: $verificationId")
                                                    val intent = Intent(this@Admin, OtpVerification::class.java)
                                                    intent.putExtra("userId", auth.currentUser!!.uid)
                                                    intent.putExtra("Token", verificationId)
                                                    intent.putExtra("Phone", "+91$number")
                                                    intent.putExtra("Type", "Admin")
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                        )
                                    } else {
                                        Toast.makeText(this, "Failed to save verification data", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Failed to save admin data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Account creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Admin, "Verification successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Admin, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}