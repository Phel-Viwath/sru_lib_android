/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class KeyStoreManager @Inject constructor() {

    private val keyStore = KeyStore.getInstance(KEYSTORE_NAME).apply {
        load(null)
    }

    private fun getKey() : SecretKey {
        val existingKey = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false) // Set to true for biometric authentication
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        return iv + encrypted
    }

    fun decrypt(encryptedBytes: ByteArray): ByteArray{
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = encryptedBytes.copyOfRange(0, cipher.blockSize)
        val data = encryptedBytes.copyOfRange(cipher.blockSize, encryptedBytes.size)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }


    companion object{
        private const val KEYSTORE_NAME = "AndroidKeyStore"
        private const val KEYSTORE_ALIAS = "secret"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }


}