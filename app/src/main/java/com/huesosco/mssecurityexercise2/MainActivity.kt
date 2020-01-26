package com.huesosco.mssecurityexercise2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.huesosco.mssecurityexercise2.utilities.CustomBiometricPrompt
import java.lang.Exception


class MainActivity: AppCompatActivity() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise2CollectionRef: CollectionReference = db.collection("exercise2")

    private lateinit var buttonAccessMessage: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAccessMessage = findViewById(R.id.main_button_access_message)
        progressBar = findViewById(R.id.main_progress_bar)

        resetDB(this)
    }

    private fun resetDB(fragmentActivity: FragmentActivity){
        //at start, we reset the data stored in the DB
        exercise2CollectionRef
            .get()
            .addOnSuccessListener(object: OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {

                    if (querySnapshot != null) {
                        exercise2CollectionRef.document("messageDoc").update("message", "")
                        buttonAccessMessage.setOnClickListener {
                            if (BiometricManager.from(applicationContext).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                                CustomBiometricPrompt(fragmentActivity, "ACCESS_MESSAGE").getBiometricPromptDialog()
                            }
                            else{
                                Toast.makeText(applicationContext, "You cannot use fingerprint in this device.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        progressBar.visibility = View.GONE
                        buttonAccessMessage.visibility = View.VISIBLE
                    }

                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}