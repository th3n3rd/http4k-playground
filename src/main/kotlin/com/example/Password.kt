package com.example

@JvmInline
value class Password(val value: String)

@JvmInline
value class EncodedPassword(val value: String)

interface PasswordEncoder {
    operator fun invoke(password: Password): EncodedPassword
    operator fun invoke(password: Password, encodedPassword: EncodedPassword): Boolean = encodedPassword == this(password)
}

object NoPasswordEncoding: PasswordEncoder {
    override fun invoke(password: Password): EncodedPassword = EncodedPassword(password.value)
}
