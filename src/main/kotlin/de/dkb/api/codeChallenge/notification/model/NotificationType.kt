package de.dkb.api.codeChallenge.notification.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Suppress("EnumEntryName")
enum class NotificationType {
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    ;

    companion object {

        private val groups = listOf(
            setOf(type1, type2, type3, type6),
            setOf(type4, type5),
        )

        fun getCategorySet(notificationType: NotificationType): Set<NotificationType> {

            return groups.firstOrNull { it.contains(notificationType) } ?: setOf()
        }
    }
}

@Converter
class NotificationTypeSetConverter : AttributeConverter<MutableSet<NotificationType>, String> {

    override fun convertToDatabaseColumn(valueSet: MutableSet<NotificationType>?): String =
        valueSet.orEmpty()
            .joinToString(separator = ";") { it.name }

    override fun convertToEntityAttribute(databaseString: String?): MutableSet<NotificationType> =
        databaseString.orEmpty()
            .split(";")
            .map { NotificationType.valueOf(it) }
            .toMutableSet()
}