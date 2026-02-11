package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
    }

    // הוספתי פרמטר onSuccess שמקבל פונקציית ניווט
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.login), Toast.LENGTH_SHORT).show()
                    onSuccess() // כאן אנחנו מפעילים את הניווט שיוגדר בפרגמנט
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
    }
}