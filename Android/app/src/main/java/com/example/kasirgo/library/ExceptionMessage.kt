package com.example.kasirgo.library

class ExceptionMessage {
    class IgnorableException(msg: String) : Exception(msg)
    class AuthException : Exception("You are not authorized")
}