package de.dkb.api.codeChallenge

import de.dkb.api.codeChallenge.notification.NotificationService
import de.dkb.api.codeChallenge.notification.model.NotificationDto
import de.dkb.api.codeChallenge.notification.model.NotificationType
import de.dkb.api.codeChallenge.notification.model.User
import jdk.internal.org.jline.utils.InfoCmp
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@Testcontainers
@SpringBootTest
class CodeChallengeApplicationTests(
	@Autowired
	val notificationService: NotificationService
) {

	companion object {
		@Container
		val postgres = PostgreSQLContainer("postgres:15")

		@JvmStatic
		@DynamicPropertySource
		@Suppress("unused", "UsePropertyAccessSyntax")
		fun registerPgProperties(registry: org.springframework.test.context.DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", postgres::getJdbcUrl)
			registry.add("spring.datasource.username", postgres::getUsername)
			registry.add("spring.datasource.password", postgres::getPassword)
		}



		val uuid1 = UUID.randomUUID()
		val uuid2 = UUID.randomUUID()
	}

	val user1 = User(uuid1, mutableSetOf(NotificationType.type1, NotificationType.type2))
	val user2 = User(uuid2, mutableSetOf(NotificationType.type4, NotificationType.type5))

	init {
	    postgres.start()

		this.notificationService.registerUser(user1)
		this.notificationService.registerUser(user2)
	}

	@Test
	fun `when send notification for user1 with type1 then expect it to be sent`() {

		val notification1 = NotificationDto(uuid1, NotificationType.type1, "message1");
		notificationService.sendNotification(notification1);

		// Expect registered notification as user1 is subscribed to type 1
		val res1 = NotificationService.registeredNotifications.firstOrNull {
			it.first == NotificationType.type1 && it.second == user1
		}
		assertNotNull(res1)
	}

	@Test
	fun `when send notification for user1 with type6 then expect it to be sent`() {

		val notification2 = NotificationDto(uuid1, NotificationType.type6, "message2");

		notificationService.sendNotification(notification2);

		// Expect registered notification as user1 is subscribed to type 1 which is the same group as type6
		val res2 = NotificationService.registeredNotifications.firstOrNull {
			it.first == NotificationType.type6 && it.second == user1
		}
		assertNotNull(res2)
	}

	@Test
	fun `when send notification for user2 with type6 then expect it to not be sent`() {

		val notification3 = NotificationDto(uuid2, NotificationType.type6, "message3");
		notificationService.sendNotification(notification3);

		// Not expect registered notification as user2 is subscribed to type4 and type5
		val res3 = NotificationService.registeredNotifications.firstOrNull {
			it.first == NotificationType.type6 && it.second == user2
		}
		assertNull(res3)
	}
}
