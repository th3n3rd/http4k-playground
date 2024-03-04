package com.example.player

object FakePasswordEncoder: PasswordEncoder {
    override fun invoke(password: Password): EncodedPassword {
        return EncodedPassword("encoded-${password.value}")
    }

}
