package com.example.player.infra

import com.example.player.EncodedPassword
import com.example.player.Password
import com.example.player.PasswordEncoder
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

fun PasswordEncoder.Companion.Argon2() = Argon2

// https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id
object Argon2: PasswordEncoder {
    private val encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    override fun invoke(password: Password): EncodedPassword {
        return EncodedPassword(encoder.encode(password.value))
    }

    override fun invoke(password: Password, encodedPassword: EncodedPassword): Boolean {
        return encoder.matches(password.value, encodedPassword.value)
    }
}
