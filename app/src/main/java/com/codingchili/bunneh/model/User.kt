package com.codingchili.bunneh.model

class User(val id: String, val name: String) {

    override fun equals(other: Any?): Boolean {
        return (other as User).id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}