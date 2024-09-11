package com.example.guessing.player.infra

import com.example.guessing.player.EncodedPassword
import com.example.guessing.player.Password
import com.example.guessing.player.PasswordEncoder

fun PasswordEncoder.Companion.Fake() = FakePasswordEncoder

object FakePasswordEncoder: PasswordEncoder {
    override fun invoke(password: Password): EncodedPassword {
        return EncodedPassword("encoded-${password.value}")
    }

    override fun invoke(password: Password, encodedPassword: EncodedPassword): Boolean = encodedPassword == this(password)
}
