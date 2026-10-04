package com.YashDoesCode.comms.data.datasource

import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

// TODO(imap/smtp): swap InMemory impl for a JavaMail-backed one. Keep the repository interface stable so the swap needs no UI change.
class InMemoryEmailDataSource {

    private val threadsFlow = MutableStateFlow<List<EmailThread>>(createInitialSeedThreads())

    fun getThreads(): Flow<List<EmailThread>> = threadsFlow.asStateFlow()

    fun getThreadById(threadId: String): Flow<EmailThread?> =
        threadsFlow.map { list -> list.find { it.id == threadId } }

    suspend fun sendEmail(recipientEmail: String, subject: String, body: String): Result<EmailMessage> {
        val now = System.currentTimeMillis()
        val messageId = "msg_${UUID.randomUUID().toString().take(8)}"
        val threadId = "thread_${UUID.randomUUID().toString().take(8)}"

        val newMessage = EmailMessage(
            id = messageId,
            threadId = threadId,
            senderName = "Me",
            senderEmail = "user@example.com",
            recipientEmail = recipientEmail,
            subject = subject,
            body = body,
            timestamp = now,
            isRead = true,
            isStarred = false
        )

        val newThread = EmailThread(
            id = threadId,
            senderName = recipientEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
            senderEmail = recipientEmail,
            subject = subject,
            snippet = body.take(60),
            lastMessageTimestamp = now,
            unreadCount = 0,
            isStarred = false,
            messages = listOf(newMessage)
        )

        val currentList = threadsFlow.value.toMutableList()
        currentList.add(0, newThread)
        threadsFlow.value = currentList

        return Result.success(newMessage)
    }

    suspend fun markThreadAsRead(threadId: String, isRead: Boolean) {
        val currentList = threadsFlow.value.map { thread ->
            if (thread.id == threadId) {
                thread.copy(
                    unreadCount = if (isRead) 0 else 1,
                    messages = thread.messages.map { it.copy(isRead = isRead) }
                )
            } else thread
        }
        threadsFlow.value = currentList
    }

    suspend fun toggleStarThread(threadId: String) {
        val currentList = threadsFlow.value.map { thread ->
            if (thread.id == threadId) {
                val newStarred = !thread.isStarred
                thread.copy(
                    isStarred = newStarred,
                    messages = thread.messages.map { it.copy(isStarred = newStarred) }
                )
            } else thread
        }
        threadsFlow.value = currentList
    }

    suspend fun deleteThread(threadId: String) {
        threadsFlow.value = threadsFlow.value.filterNot { it.id == threadId }
    }

    private fun createInitialSeedThreads(): List<EmailThread> {
        val now = System.currentTimeMillis()
        val minute = 1000L * 60
        val hour = minute * 60
        val day = hour * 24

        return listOf(
            EmailThread(
                id = "thread_1",
                senderName = "Android Developers",
                senderEmail = "newsletter@android-weekly.com",
                subject = "Android Weekly Issue #650 - Compose Multiplatform & Architecture",
                snippet = "Check out the latest insights on Jetpack Compose, Room 2.7, and Kotlin 2.1...",
                lastMessageTimestamp = now - 15 * minute,
                unreadCount = 1,
                isStarred = true,
                messages = listOf(
                    EmailMessage(
                        id = "msg_1",
                        threadId = "thread_1",
                        senderName = "Android Developers",
                        senderEmail = "newsletter@android-weekly.com",
                        recipientEmail = "user@example.com",
                        subject = "Android Weekly Issue #650",
                        body = "Welcome to issue #650 of Android Weekly!\n\nThis week we explore Kotlin 2.1 compiler enhancements, building custom Canvas charts in Jetpack Compose, and offline-first Room architectures.",
                        timestamp = now - 15 * minute,
                        isRead = false,
                        isStarred = true
                    )
                )
            ),
            EmailThread(
                id = "thread_2",
                senderName = "GitHub Notifications",
                senderEmail = "notifications@github.com",
                subject = "[YashDoesCode/android-1O1] Pull Request #2 ready for review",
                snippet = "Yash requested your review on feat/comms-dialer-mail...",
                lastMessageTimestamp = now - 2 * hour,
                unreadCount = 1,
                isStarred = false,
                messages = listOf(
                    EmailMessage(
                        id = "msg_2",
                        threadId = "thread_2",
                        senderName = "GitHub Notifications",
                        senderEmail = "notifications@github.com",
                        recipientEmail = "user@example.com",
                        subject = "[YashDoesCode/android-1O1] Pull Request #2",
                        body = "Pull request #2 has been opened: comms: combined dialer + email client app.\n\nAll CI checks have passed successfully.",
                        timestamp = now - 2 * hour,
                        isRead = false
                    )
                )
            ),
            EmailThread(
                id = "thread_3",
                senderName = "Google Security",
                senderEmail = "no-reply@accounts.google.com",
                subject = "Security Alert: New sign-in on Android device",
                snippet = "Your Google Account was recently accessed from an Android 15 emulator...",
                lastMessageTimestamp = now - 5 * hour,
                unreadCount = 0,
                isStarred = false,
                messages = listOf(
                    EmailMessage(
                        id = "msg_3",
                        threadId = "thread_3",
                        senderName = "Google Security",
                        senderEmail = "no-reply@accounts.google.com",
                        recipientEmail = "user@example.com",
                        subject = "Security Alert",
                        body = "A new sign-in occurred on your Android device. If this was you, no action is needed.",
                        timestamp = now - 5 * hour,
                        isRead = true
                    )
                )
            ),
            EmailThread(
                id = "thread_4",
                senderName = "Alice Johnson",
                senderEmail = "alice@example.com",
                subject = "Project Architecture Review & API Design",
                snippet = "Hey Yash, I reviewed the multi-module layout for the browser and comms app...",
                lastMessageTimestamp = now - 1 * day,
                unreadCount = 0,
                isStarred = true,
                messages = listOf(
                    EmailMessage(
                        id = "msg_4",
                        threadId = "thread_4",
                        senderName = "Alice Johnson",
                        senderEmail = "alice@example.com",
                        recipientEmail = "user@example.com",
                        subject = "Project Architecture Review & API Design",
                        body = "Hey Yash,\n\nThe clean architecture split with UDF and StateFlow looks solid! Let's make sure the Banking Suite also follows the same Room caching pattern.\n\nBest,\nAlice",
                        timestamp = now - 1 * day,
                        isRead = true,
                        isStarred = true
                    )
                )
            ),
            EmailThread(
                id = "thread_5",
                senderName = "Kotlin Weekly",
                senderEmail = "digest@kotlinweekly.net",
                subject = "Kotlin Weekly Digest #430",
                snippet = "Coroutines best practices, context receivers, and KSP2 performance...",
                lastMessageTimestamp = now - 2 * day,
                unreadCount = 0,
                isStarred = false,
                messages = listOf(
                    EmailMessage(
                        id = "msg_5",
                        threadId = "thread_5",
                        senderName = "Kotlin Weekly",
                        senderEmail = "digest@kotlinweekly.net",
                        recipientEmail = "user@example.com",
                        subject = "Kotlin Weekly Digest #430",
                        body = "Here are top articles from the Kotlin community this week.",
                        timestamp = now - 2 * day,
                        isRead = true
                    )
                )
            ),
            EmailThread(
                id = "thread_6",
                senderName = "Firebase Team",
                senderEmail = "updates@firebase.google.com",
                subject = "What's new in Firebase App Hosting and Data Connect",
                snippet = "Explore instant full-stack Next.js deployment and PostgreSQL connectivity...",
                lastMessageTimestamp = now - 3 * day,
                unreadCount = 0,
                isStarred = false,
                messages = listOf(
                    EmailMessage(
                        id = "msg_6",
                        threadId = "thread_6",
                        senderName = "Firebase Team",
                        senderEmail = "updates@firebase.google.com",
                        recipientEmail = "user@example.com",
                        subject = "What's new in Firebase App Hosting",
                        body = "Discover streamlined cloud integrations with Firebase modern tooling.",
                        timestamp = now - 3 * day,
                        isRead = true
                    )
                )
            ),
            EmailThread(
                id = "thread_7",
                senderName = "Design Team",
                senderEmail = "design@system.org",
                subject = "Material 3 Expressive Color Guidelines Update",
                snippet = "New dynamic color token mappings and typography scales have landed...",
                lastMessageTimestamp = now - 4 * day,
                unreadCount = 0,
                isStarred = false,
                messages = listOf(
                    EmailMessage(
                        id = "msg_7",
                        threadId = "thread_7",
                        senderName = "Design Team",
                        senderEmail = "design@system.org",
                        recipientEmail = "user@example.com",
                        subject = "Material 3 Expressive Color Guidelines",
                        body = "Review the updated Figma tokens and ensure compose theme tokens match.",
                        timestamp = now - 4 * day,
                        isRead = true
                    )
                )
            ),
            EmailThread(
                id = "thread_8",
                senderName = "Cloud Provider",
                senderEmail = "billing@cloud.service.com",
                subject = "Monthly Invoice for October 2026",
                snippet = "Your monthly invoice is now available for download ($0.00)...",
                lastMessageTimestamp = now - 5 * day,
                unreadCount = 0,
                isStarred = false,
                messages = listOf(
                    EmailMessage(
                        id = "msg_8",
                        threadId = "thread_8",
                        senderName = "Cloud Provider",
                        senderEmail = "billing@cloud.service.com",
                        recipientEmail = "user@example.com",
                        subject = "Monthly Invoice for October 2026",
                        body = "Your account balance is $0.00. Thank you for building on our platform.",
                        timestamp = now - 5 * day,
                        isRead = true
                    )
                )
            )
        )
    }
}
