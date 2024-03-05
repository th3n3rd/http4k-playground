package com.example.player

@JvmInline
value class Password(val value: String)

@JvmInline
value class EncodedPassword(val value: String)

interface PasswordEncoder {
    operator fun invoke(password: Password): EncodedPassword
    operator fun invoke(password: Password, encodedPassword: EncodedPassword): Boolean = encodedPassword == this(password)

    companion object
}

