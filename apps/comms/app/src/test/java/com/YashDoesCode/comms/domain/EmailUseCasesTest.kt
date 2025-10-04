package com.YashDoesCode.comms.domain

import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.domain.usecase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmailUseCasesTest {

    private lateinit var fakeRepository: FakeEmailRepository
    private lateinit var getEmailThreadsUseCase: GetEmailThreadsUseCase
    private lateinit var sendEmailUseCase: SendEmailUseCase
    private lateinit var markThreadAsReadUseCase: MarkThreadAsReadUseCase
    private lateinit var toggleStarThreadUseCase: ToggleStarThreadUseCase
    private lateinit var deleteEmailThreadUseCase: DeleteEmailThreadUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeEmailRepository()
        getEmailThreadsUseCase = GetEmailThreadsUseCase(fakeRepository)
        sendEmailUseCase = SendEmailUseCase(fakeRepository)
        markThreadAsReadUseCase = MarkThreadAsReadUseCase(fakeRepository)
        toggleStarThreadUseCase = ToggleStarThreadUseCase(fakeRepository)
        deleteEmailThreadUseCase = DeleteEmailThreadUseCase(fakeRepository)
    }

    @Test
    fun sendEmailUseCase_appendsNewThread() = runTest {
        val result = sendEmailUseCase("bob@example.com", "Project Sync", "Let's meet tomorrow.")
        assertTrue(result.isSuccess)

        val threads = getEmailThreadsUseCase().first()
        assertEquals(1, threads.size)
        assertEquals("Project Sync", threads[0].subject)
        assertEquals("Let's meet tomorrow.", threads[0].snippet)
    }

    @Test
    fun markThreadAsReadUseCase_updatesUnreadCount() = runTest {
        val thread = EmailThread(
            id = "thread-1",
            senderName = "Alice",
            senderEmail = "alice@example.com",
            subject = "Hello",
            snippet = "Hi there",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 1,
            isStarred = false,
            messages = listOf(
                EmailMessage(
                    id = "msg-1",
                    threadId = "thread-1",
                    senderName = "Alice",
                    senderEmail = "alice@example.com",
                    recipientEmail = "me@example.com",
                    subject = "Hello",
                    body = "Hi there",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
            )
        )
        fakeRepository.setThreads(listOf(thread))

        markThreadAsReadUseCase("thread-1", true)
        val updatedThreads = getEmailThreadsUseCase().first()
        assertEquals(0, updatedThreads[0].unreadCount)
        assertTrue(updatedThreads[0].messages[0].isRead)
    }

    @Test
    fun toggleStarThreadUseCase_togglesStarState() = runTest {
        val thread = EmailThread(
            id = "thread-1",
            senderName = "Alice",
            senderEmail = "alice@example.com",
            subject = "Hello",
            snippet = "Hi there",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0,
            isStarred = false,
            messages = emptyList()
        )
        fakeRepository.setThreads(listOf(thread))

        toggleStarThreadUseCase("thread-1")
        val updated = getEmailThreadsUseCase().first()
        assertTrue(updated[0].isStarred)

        toggleStarThreadUseCase("thread-1")
        val reverted = getEmailThreadsUseCase().first()
        assertFalse(reverted[0].isStarred)
    }

    @Test
    fun deleteEmailThreadUseCase_removesThread() = runTest {
        val thread = EmailThread(
            id = "thread-1",
            senderName = "Alice",
            senderEmail = "alice@example.com",
            subject = "Hello",
            snippet = "Hi there",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0,
            isStarred = false,
            messages = emptyList()
        )
        fakeRepository.setThreads(listOf(thread))

        deleteEmailThreadUseCase("thread-1")
        val updated = getEmailThreadsUseCase().first()
        assertTrue(updated.isEmpty())
    }
}
