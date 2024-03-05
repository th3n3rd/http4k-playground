package com.example.player.infra

import com.example.player.EncodedPassword
import com.example.player.Password
import com.example.player.PasswordEncoder

fun PasswordEncoder.Companion.Fake() = FakePasswordEncoder

object FakePasswordEncoder: PasswordEncoder {
    override fun invoke(password: Password): EncodedPassword {
        return EncodedPassword("encoded-${password.value}")
    }
}
