package com.example.nexart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.nexart.Models.User
import com.example.nexart.Utils.USER_NODE
import com.example.nexart.Utils.USER_PROFILE_FOLDER
import com.example.nexart.Utils.uploadImage
import com.example.nexart.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class Signup : AppCompatActivity() {

    val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it == null) {
                } else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text =
            "<front color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login?</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()
        if (intent.hasExtra("MODE")) {
            if (intent.getIntExtra("MODE", -1) == 1) {
                binding.signUpBtn.text = "update Profile"
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                    .get().addOnSuccessListener {
                        user = it.toObject<User>()!!
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).into(binding.profileImage)
                        }

                        binding.textField.editText?.setText(user.name)
                        binding.textInputLayout.editText?.setText(user.email)
                        binding.textInputLayout3.editText?.setText(user.password)
                    }
            }
        }

            binding.signUpBtn.setOnClickListener {
                if (intent.hasExtra("MODE")) {
                    if (intent.getIntExtra("MODE", -1) == 1) {
                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                startActivity(Intent(this@Signup, Home::class.java))
                                finish()
                            }
                    }
                } else {
                    if (binding.textField.editText?.text.toString().equals("") or
                        binding.textInputLayout.editText?.text.toString().equals("") or
                        binding.textInputLayout3.editText?.text.toString().equals("")

                    ) {
                        Toast.makeText(
                            this@Signup,
                            "Please fill the all information",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(

                            binding.textInputLayout.editText?.text.toString(),
                            binding.textInputLayout3.editText?.text.toString()
                        ).addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                Toast.makeText(
                                    this@Signup,
                                    "Login Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                user.name = binding.textField.editText?.text.toString()
                                user.email = binding.textInputLayout.editText?.text.toString()
                                user.password = binding.textInputLayout3.editText?.text.toString()
                                Firebase.firestore.collection(USER_NODE)
                                    .document(Firebase.auth.currentUser!!.uid).set(user)
                                    .addOnSuccessListener {
                                        startActivity(Intent(this@Signup, Home::class.java))
                                        finish()
                                    }

                            } else {
                                Toast.makeText(
                                    this@Signup,
                                    result.exception?.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                }
                binding.plus.setOnClickListener {
                    launcher.launch("image/*")

                }
                binding.login.setOnClickListener {
                    startActivity(Intent(this@Signup, Login::class.java))
                    finish()
                }
            }
        }
    }
