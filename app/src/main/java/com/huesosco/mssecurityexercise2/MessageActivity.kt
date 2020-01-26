package com.huesosco.mssecurityexercise2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.huesosco.mssecurityexercise2.utilities.AESDecryptor
import com.huesosco.mssecurityexercise2.utilities.AESEncryptor

import java.lang.Exception


class MessageActivity : AppCompatActivity() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise2CollectionRef: CollectionReference = db.collection("exercise2")

    private lateinit var textView: TextView
    private lateinit var editText: EditText
    private lateinit var button: Button

    private lateinit var encryptor: AESEncryptor
    private lateinit var decryptor: AESDecryptor
    private lateinit var ivGeneral: ByteArray

    private val SAMPLE_ALIAS = "MYALIAS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        loadXmlReferences()

        encryptor = AESEncryptor()
        decryptor = AESDecryptor()

        loadMessage()
        setUpButton()
    }

    private fun loadXmlReferences(){
        textView = findViewById(R.id.activity_message_text_view)
        editText = findViewById(R.id.activity_message_edit_text)
        button = findViewById(R.id.activity_message_button)
    }

    private fun loadMessage(){
        exercise2CollectionRef
            .get()
            .addOnSuccessListener(object: OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {

                    if (querySnapshot != null) {

                        var messageEncrypted = String()

                        for(doc in querySnapshot.documents){
                            //we search for the message Document in the database, and the AES password, which
                            //is the one set before by the user
                            if(doc.id == "messageDoc") messageEncrypted = doc.data!!["message"].toString()
                            if(doc.id == "ivDoc" && doc.data!!["ivGeneral"].toString() != "")
                                ivGeneral = Base64.decode(doc.data!!["ivGeneral"].toString(), Base64.DEFAULT)
                        }

                        //once we have the message encrypted and its pass, we decrypt it
                        textView.text = if(messageEncrypted.isNotEmpty())
                            decryptText(stringToByteArray(messageEncrypted))
                        else ""
                        Toast.makeText(applicationContext, "Message successfully loaded.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setUpButton() {
        button.setOnClickListener {
            val e1 = editText.text.toString()
            if (e1.isEmpty())
                Toast.makeText(applicationContext, "You must write a message.", Toast.LENGTH_SHORT).show()
            else{

                val encryptedDataByteArray = encryptText(e1)


                exercise2CollectionRef
                    .document("messageDoc").update("message", byteArrayToString(encryptedDataByteArray!!))
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Message successfully saved.", Toast.LENGTH_SHORT).show()
                        textView.text = e1
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "ERROR: Message could not be saved.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }


    private fun decryptText(encryptedData: ByteArray): String? {
        return decryptor.decryptData(SAMPLE_ALIAS, encryptedData, ivGeneral)
    }

    private fun encryptText(toEncrypt: String): ByteArray? {
        var encrypted: ByteArray? = null
        encrypted = encryptor.encryptText(SAMPLE_ALIAS, toEncrypt)
        ivGeneral = encryptor.getIv()!!
        exercise2CollectionRef
            .document("ivDoc")
            .update("ivGeneral", Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT))

        return encrypted
    }


    //this two functions are useful because it is not possible to store bytearrays in firebase
    private fun byteArrayToString(array: ByteArray): String {
        var ret = String()
        for(b in array){
            ret += b.toInt()
            ret += "$"
        }
        return ret
    }

    private fun stringToByteArray(s: String): ByteArray {
        val stringSplitArray = s.split("$")
        val byteArray = ByteArray(stringSplitArray.size - 1)
        for(i in 0 until byteArray.size){
            byteArray[i] = stringSplitArray[i].toByte()
        }
        return byteArray
    }

}
