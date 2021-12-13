package com.project.eshop.repository

import cats.effect.unsafe.implicits.global
import cats.syntax.all._
import com.project.eshop.domain.{User, UserWithAuthInfo}
import com.project.eshop.routes.dto.UserDTO
import doobie.ConnectionIO
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import doobie.implicits._

class UserRepositorySpec extends AnyWordSpec with Matchers with DbSetup {
  private val userRepository: UserRepository[ConnectionIO] = UserRepository.make
  private val expectedUser = User("1", "example", "example@gmail.com", "user", "firstName", "lastName")

  "User repository" should {
    "create new user" in {
       val createdUser = userRepository.create(UserDTO.CreateUser("example", "example@gmail.com", "password", "lastName", "firstName"))
         .transact(transactor)
         .unsafeRunSync()

      createdUser.copy(id = "1") shouldEqual expectedUser
    }

    "select user by userId" in {
      val createdUser = userRepository.create(UserDTO.CreateUser("example", "example@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val selectedUser = userRepository.select(createdUser.id)
        .transact(transactor)
        .unsafeRunSync()

      selectedUser shouldEqual createdUser.some
    }

    "select user by username" in {
      val createdUser = userRepository.create(UserDTO.CreateUser("example", "example@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val selectedUser = userRepository.selectUserByUsername(createdUser.username)
        .transact(transactor)
        .unsafeRunSync()

      selectedUser shouldEqual createdUser.some
    }

    "select logins" in {
      val createdUser1 = userRepository.create(UserDTO.CreateUser("example1", "example1@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val createdUser2 = userRepository.create(UserDTO.CreateUser("example2", "example2@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val selectedLogins = userRepository.selectLogins.transact(transactor).unsafeRunSync()

      selectedLogins shouldEqual List("example1", "example2")
    }

    "select emails" in {
      val createdUser1 = userRepository.create(UserDTO.CreateUser("example1", "example1@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val createdUser2 = userRepository.create(UserDTO.CreateUser("example2", "example2@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val selectedLogins = userRepository.selectEmails.transact(transactor).unsafeRunSync()

      selectedLogins shouldEqual List("example1@gmail.com", "example2@gmail.com")
    }

    "select user auth info" in {
      val createdUser = userRepository.create(UserDTO.CreateUser("example", "example@gmail.com", "password", "lastName", "firstName"))
        .transact(transactor)
        .unsafeRunSync()

      val selectedAuthInfo = userRepository.selectUserAuthInfoByUsername(createdUser.username)
        .transact(transactor)
        .unsafeRunSync()

      selectedAuthInfo shouldEqual UserWithAuthInfo(createdUser.id, createdUser.username, "password").some
    }
  }

  override protected def afterEach(): Unit = {
    sql"TRUNCATE table users cascade".update.run.transact(transactor).unsafeRunSync()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }
}
