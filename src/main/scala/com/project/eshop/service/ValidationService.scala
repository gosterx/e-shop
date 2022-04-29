package com.project.eshop.service

import cats.effect.Sync
import cats.syntax.all._
import com.project.eshop.repository.UserRepository
import com.project.eshop.routes.dto.PasswordValidation
import com.project.eshop.service.errors.ValidationError
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

trait ValidationService[F[_]] {
  def validateName(name: String): F[Option[ValidationError]]

  def validateLastName(lastName: String): F[Option[ValidationError]]

  def validateUsername(username: String): F[Option[ValidationError]]

  def validateEmail(email: String): F[Option[ValidationError]]

  def validatePassword(password: String): F[Option[ValidationError]]

  def validateConfirmPassword(passwords: PasswordValidation): F[Option[ValidationError]]

  def validateCategoryImageLink(name: String): F[Option[ValidationError]]

  def validateCategoryTitle(title: String): F[Option[ValidationError]]
}

object ValidationService {
  def of[F[_]: Sync](userRepository: UserRepository[ConnectionIO], transactor: Transactor[F]): ValidationService[F] =
    new ValidationService[F] {
      override def validateName(name: String): F[Option[ValidationError]] =
        (if (name.nonEmpty && name.length <= 30) none[ValidationError]
         else ValidationError("Name length should be between 1 and 30 characters").some).pure[F]

      override def validateLastName(lastName: String): F[Option[ValidationError]] =
        (if (lastName.nonEmpty && lastName.length <= 30) none[ValidationError]
         else ValidationError("Last name length should be between 1 and 30 characters").some).pure[F]

      override def validateUsername(username: String): F[Option[ValidationError]] = {
        val usernameRegex = "^(?=.{6,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$".r
        (for {
          afterRegex <- (if (usernameRegex.matches(username)) none
                         else
                           ValidationError(
                             "Username name length should be between 6 and 20 characters. Contains letters, number, point and underscore"
                           ).some).pure[ConnectionIO]
          usernames <- userRepository.selectLogins
          res <- afterRegex match {
            case Some(value) => value.some.pure[ConnectionIO]
            case None =>
              (if (usernames.contains(username)) ValidationError("Username already in use").some
               else none).pure[ConnectionIO]
          }
        } yield res).transact(transactor)
      }

      override def validateEmail(email: String): F[Option[ValidationError]] = {
        val emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".r
        (for {
          afterRegex <- (if (emailRegex.matches(email)) none
                         else ValidationError("Incorrect email").some).pure[ConnectionIO]
          emails <- userRepository.selectEmails
          res <- afterRegex match {
            case Some(value) => value.some.pure[ConnectionIO]
            case None =>
              (if (emails.contains(email)) ValidationError("Email already in use").some
               else none).pure[ConnectionIO]
          }
        } yield res).transact(transactor)
      }

      override def validatePassword(password: String): F[Option[ValidationError]] =
        (if (password.length >= 8 && password.length <= 30) none[ValidationError]
        else ValidationError("Password should be between 8 and 30 characters").some).pure[F]

      override def validateConfirmPassword(passwords: PasswordValidation): F[Option[ValidationError]] =
        (if (passwords.password == passwords.confirm) none[ValidationError]
        else ValidationError("Password mismatch").some).pure[F]

      override def validateCategoryImageLink(link: String): F[Option[ValidationError]] =
        (if (link.nonEmpty) none[ValidationError]
        else ValidationError("Cannot be empty").some).pure[F]

      override def validateCategoryTitle(title: String): F[Option[ValidationError]] =
        (if (title.nonEmpty && title.length <= 10) none[ValidationError]
        else ValidationError("Length should be between 1 and 10").some).pure[F]
    }
}
