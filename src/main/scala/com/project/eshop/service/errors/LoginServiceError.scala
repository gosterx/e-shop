package com.project.eshop.service.errors

abstract class LoginServiceError(message: String) extends Throwable(message)

case object IncorrectLogin extends LoginServiceError("Incorrect login")

case object IncorrectPassword extends LoginServiceError("Incorrect password")
