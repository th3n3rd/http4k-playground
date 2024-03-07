package com.example.player

data class RegisteredPlayer(val id: PlayerId = PlayerId(), val username: String, val password: EncodedPassword)
