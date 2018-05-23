package me.mkweb.gibb.m183.securityapp.domain

import javax.persistence.Id

data class User(@Id var id: Long,
                var username: String,
                var password: String)