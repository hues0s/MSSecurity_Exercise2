package com.huesosco.mssecurityexercise2.utilities

import java.io.IOException
import java.security.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec


class AESDecryptor {

    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val ANDROID_KEY_STORE = "AndroidKeyStore"

    private var keyStore: KeyStore? = null

    init {
        initKeyStore()
    }

    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore!!.load(null)
    }


    fun decryptData(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String {

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)

        val ret = String(cipher.doFinal(encryptedData), charset("UTF-8"))

        return ret
    }

    private fun getSecretKey(alias: String): SecretKey {
        return (keyStore!!.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

}