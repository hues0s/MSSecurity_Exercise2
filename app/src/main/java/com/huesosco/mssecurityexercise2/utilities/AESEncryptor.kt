package com.huesosco.mssecurityexercise2.utilities

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class AESEncryptor {

    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val ANDROID_KEY_STORE = "AndroidKeyStore"

    private var encryption: ByteArray? = null
    private var iv: ByteArray? = null


    fun encryptText(alias: String, textToEncrypt: String): ByteArray? {

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))

        iv = cipher.iv

        encryption = cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))
        return encryption
    }


    private fun getSecretKey(alias: String): SecretKey {

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGenerator.init(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(3600)
                    .build()
            )
        }

        return keyGenerator.generateKey()
    }

    fun getEncryption(): ByteArray? {
        return encryption
    }

    fun getIv(): ByteArray? {
        return iv
    }

}