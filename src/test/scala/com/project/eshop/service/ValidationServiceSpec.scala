package com.project.eshop.service

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all._
import com.project.eshop.repository.{DbSetup, UserRepository}
import com.project.eshop.routes.dto.PasswordValidation
import com.project.eshop.service.errors.ValidationError
import doobie.ConnectionIO
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ValidationServiceSpec extends AnyWordSpec with Matchers with DbSetup {
  private val userRepository: UserRepository[ConnectionIO] = UserRepository.make
  private val validationService = ValidationService.of[IO](userRepository, transactor)

  "Validation service" should {
    "validate correct first name" in {
      val correct = "Alexey"
      validationService.validateName(correct).unsafeRunSync() shouldEqual none[ValidationError]
    }

    "validate incorrect first name" in {
      val incorrect = ""
      validationService.validateName(incorrect).unsafeRunSync() shouldEqual
        ValidationError("Name length should be between 1 and 30 characters").some
    }

    "validate correct last name" in {
      val correct = "Suslov"
      validationService.validateLastName(correct).unsafeRunSync() shouldEqual none[ValidationError]
    }

    "validate incorrect last name" in {
      val incorrect = ""
      validationService.validateLastName(incorrect).unsafeRunSync() shouldEqual
        ValidationError("Last name length should be between 1 and 30 characters").some
    }

    "validate correct password" in {
      val correct = "123445678"
      validationService.validatePassword(correct).unsafeRunSync() shouldEqual none[ValidationError]
    }

    "validate incorrect password" in {
      val incorrect = "1234567"
      validationService.validatePassword(incorrect).unsafeRunSync() shouldEqual
        ValidationError("Password should be between 8 and 30 characters").some
    }

    "validate correct confirm password" in {
      val correct = PasswordValidation("password", "password")
      validationService.validateConfirmPassword(correct).unsafeRunSync() shouldEqual none[ValidationError]
    }

    "validate incorrect confirm password" in {
      val incorrect = PasswordValidation("password1", "password")
      validationService.validateConfirmPassword(incorrect).unsafeRunSync() shouldEqual
        ValidationError("Password mismatch").some
    }
  }

}
