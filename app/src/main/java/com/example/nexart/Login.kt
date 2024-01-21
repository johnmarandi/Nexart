package com.example.nexart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.nexart.Models.User
import com.example.nexart.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.loginBtn.setOnClickListener {
            if (binding.textField.editText?.text.toString().equals("") or
                binding.textInputLayout.editText?.text.toString().equals("")
            ) {
                Toast.makeText(this@Login, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                var user = User(
                    binding.textField.editText?.text.toString(),
                    binding.textInputLayout.editText?.text.toString()
                )

                Firebase.auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this@Login, Home::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@Login,
                                it.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }

            }

            }
        binding.newUser.setOnClickListener{
            startActivity(Intent(this@Login,Signup::class.java))
            }
        }
    }
