package com.example.b07proj.model

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Object to manage PIN-based login for the app.
object PinManager {
    // Constants for keystore and transformation
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    // Use a single, fixed alias for the key. This is necessary for PIN-only login.
    private const val KEY_ALIAS = "com_example_b07proj_local_pin_key"
    private const val IV_SEPARATOR = "|"

    // Initialize the KeyStore
    private val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }


    //Encrypts a plaintext string using the app's fixed local data key.
    fun encrypt(dataToEncrypt: String): String? {
        // Now uses the KEY_ALIAS internally
        return try {
            // Initialize the cipher with the KEY_ALIAS and transformation and encrypt the data
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val encryptedBytes = cipher.doFinal(dataToEncrypt.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv
            val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            "$ivString$IV_SEPARATOR$encryptedString"
        } catch (e: Exception) {
            null
        }
    }


    //Decrypts an encrypted data string using the app's fixed local data key.
    fun decrypt(encryptedData: String): String? {
        // Now uses the fixed KEY_ALIAS internally
        return try {
            // Split the IV and encrypted data then initialize the cipher with the KEY_ALIAS and transformation and decrypt the data
            val parts = encryptedData.split(IV_SEPARATOR)
            if (parts.size != 2) return null
            val iv = Base64.decode(parts[0], Base64.DEFAULT)
            val encryptedBytes = Base64.decode(parts[1], Base64.DEFAULT)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
    // Get the UUID and PIN from the encrypted data.
    fun getUuid(encryptedData: String): String? {
        val decryptedJson = decrypt(encryptedData)
        // checks if the decrypted json is null or not
        return decryptedJson?.let { try { JSONObject(it).getString("uuid") } catch (e: Exception) { null } }
    }
    // Get the PIN from the encrypted data.
    fun getPin(encryptedData: String): String? {
        val decryptedJson = decrypt(encryptedData)
        return decryptedJson?.let { try { JSONObject(it).getString("pin") } catch (e: Exception) { null } }
    }

    // function to get or create the secret key
    private fun getOrCreateSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateSecretKey()
    }

    // function to generate the secret key
    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val keyGenSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            // Set the encryption mode to GCM
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(keyGenSpec)
        return keyGenerator.generateKey()
    }
}