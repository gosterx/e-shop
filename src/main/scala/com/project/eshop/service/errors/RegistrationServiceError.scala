package com.project.eshop.service.errors

abstract class RegistrationServiceError(message: String) extends Throwable(message)

case object LoginAlreadyUse extends RegistrationServiceError("Login is already in use")

case object EmailAlreadyUse extends RegistrationServiceError("Email is already in use")