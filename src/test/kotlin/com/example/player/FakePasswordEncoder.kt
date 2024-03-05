package com.example.player

fun PasswordEncoder.Companion.Fake() = FakePasswordEncoder

object FakePasswordEncoder: PasswordEncoder {
    override fun invoke(password: Password): EncodedPassword {
        return EncodedPassword("encoded-${password.value}")
    }
}
