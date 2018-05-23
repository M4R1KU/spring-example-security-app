package me.mkweb.gibb.m183.securityapp.domain

import javax.persistence.Entity

@Entity
data class User(var username: String = "",
                var password: String = ""): AbstractEntity()