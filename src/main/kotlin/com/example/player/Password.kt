package com.example.player

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

@JvmInline
value class Password(val value: String)

@JvmInline
value class EncodedPassword(val value: String)

interface PasswordEncoder {
    operator fun invoke(password: Password): EncodedPassword
    operator fun invoke(password: Password, encodedPassword: EncodedPassword): Boolean = encodedPassword == this(password)
}

object PasswordEncodings {

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
}
