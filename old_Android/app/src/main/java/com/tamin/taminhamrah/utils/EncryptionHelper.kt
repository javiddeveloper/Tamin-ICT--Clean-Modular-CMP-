import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

object EncryptionHelper {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val ALIAS = "MySecureTokenKey"
    private const val PREFS_NAME = "secure_key_prefs"
    private const val KEY_ENCRYPTED_AES = "encrypted_aes_key"


    fun encrypt(context: Context, data: String): String {
        try {
            if (data.isEmpty()) return ""

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(context))

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            val ivString = Base64.encodeToString(iv, Base64.DEFAULT).trim()
            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim()

            return "$ivString:]:$encryptedString"
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed")
            return ""
        }
    }

    fun decrypt(context: Context, encryptedData: String): String {
        try {
            if (encryptedData.isEmpty()) return ""

            val parts = encryptedData.split(":]:")
            if (parts.size != 2) return ""

            val iv = Base64.decode(parts[0], Base64.DEFAULT)
            val encryptedBytes = Base64.decode(parts[1], Base64.DEFAULT)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(context), spec)

            val decodedBytes = cipher.doFinal(encryptedBytes)
            return String(decodedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed")
            return ""
        }
    }

    private fun getSecretKey(context: Context): SecretKey {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSecretKeyApi23()
        } else {
            getSecretKeyLegacy(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getSecretKeyApi23(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        keyStore.getKey(ALIAS, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val spec = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun getSecretKeyLegacy(context: Context): SecretKey {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedKeyBase64 = prefs.getString(KEY_ENCRYPTED_AES, null)

        return if (encryptedKeyBase64 != null) {
            unwrapKey(encryptedKeyBase64)
        } else {
            val newKey = ByteArray(32) // 256-bit key
            SecureRandom().nextBytes(newKey)
            val secretKey = SecretKeySpec(newKey, "AES")

            val wrappedKey = wrapKey(context, secretKey)
            prefs.edit().putString(KEY_ENCRYPTED_AES, wrappedKey).apply()
            secretKey
        }
    }


    private fun wrapKey(context: Context, key: SecretKey): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        if (!keyStore.containsAlias(ALIAS)) {
            generateRsaKeyPair(context)
        }

        val publicKey = keyStore.getCertificate(ALIAS).publicKey
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedKey = cipher.doFinal(key.encoded)

        return Base64.encodeToString(encryptedKey, Base64.DEFAULT)
    }

    private fun unwrapKey(encryptedKeyBase64: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        val privateKey = keyStore.getKey(ALIAS, null) as? java.security.PrivateKey
            ?: throw IllegalStateException("RSA Private key not found")

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val encryptedBytes = Base64.decode(encryptedKeyBase64, Base64.DEFAULT)
        val decodedKey = cipher.doFinal(encryptedBytes)

        return SecretKeySpec(decodedKey, "AES")
    }

    private fun generateRsaKeyPair(context: Context) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 25)

        val generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE)
        val spec = KeyPairGeneratorSpec.Builder(context)
            .setAlias(ALIAS)
            .setSubject(X500Principal("CN=$ALIAS"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .build()

        generator.initialize(spec)
        generator.generateKeyPair()
    }

    fun clearKey(context: Context) {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            if (keyStore.containsAlias(ALIAS)) {
                keyStore.deleteEntry(ALIAS)
            }
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY_ENCRYPTED_AES).apply()
        } catch (e: Exception) {
            Timber.e(e, "clearKey failed")
        }
    }
}