package com.example.guessing.player

data class RegisteredPlayer(val id: PlayerId = PlayerId(), val username: String, val password: EncodedPassword)
