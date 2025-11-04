package com.tumme.scrudstudents.util

import java.security.MessageDigest

/**
 * Utility class for password hashing.
 * 
 * Uses SHA-256 for educational purposes only.
 * 
 * WARNING: SHA-256 is NOT secure for production password hashing.
 * Production applications should use proper password hashing algorithms
 * like bcrypt, scrypt, or Argon2 with salt and proper iteration counts.
 * 
 * This implementation is for labwork purposes only.
 */
object PasswordHasher {
    /**
     * Hashes a password using SHA-256.
     * 
     * @param password The plain text password
     * @return The hexadecimal hash string
     */
    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies a password against a hash.
     * 
     * @param password The plain text password to verify
     * @param hash The stored hash to compare against
     * @return true if the password matches the hash
     */
    fun verify(password: String, hash: String): Boolean {
        return hash(password) == hash
    }
}
