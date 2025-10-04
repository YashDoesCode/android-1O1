package com.YashDoesCode.comms.ui

import com.YashDoesCode.comms.domain.FakeEmailRepository
import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.domain.usecase.*
import com.YashDoesCode.comms.ui.mail.MailAction
import com.YashDoesCode.comms.ui.mail.MailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeEmailRepository
    private lateinit var viewModel: MailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeEmailRepository()
        viewModel = MailViewModel(
            getEmailThreadsUseCase = GetEmailThreadsUseCase(fakeRepository),
            sendEmailUseCase = SendEmailUseCase(fakeRepository),
            markThreadAsReadUseCase = MarkThreadAsReadUseCase(fakeRepository),
            toggleStarThreadUseCase = ToggleStarThreadUseCase(fakeRepository),
            deleteEmailThreadUseCase = DeleteEmailThreadUseCase(fakeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectThread_marksThreadAsReadAutomatically() = runTest {
        val thread = EmailThread(
            id = "t1",
            senderName = "Bob",
            senderEmail = "bob@example.com",
            subject = "Meeting",
            snippet = "Tomorrow at 10",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 2,
            isStarred = false,
            messages = listOf(
                EmailMessage("m1", "t1", "Bob", "bob@example.com", "me@example.com", "Meeting", "Tomorrow at 10", System.currentTimeMillis(), false)
            )
        )
        fakeRepository.setThreads(listOf(thread))
        testScheduler.advanceUntilIdle()

        viewModel.onAction(MailAction.SelectThread("t1"))
        testScheduler.advanceUntilIdle()

        assertEquals("t1", viewModel.uiState.value.selectedThread?.id)
        assertEquals(0, viewModel.uiState.value.threads[0].unreadCount)
    }

    @Test
    fun sendEmail_withEmptyRecipient_showsToastError() = runTest {
        viewModel.onAction(MailAction.SetComposeOpen(true))
        viewModel.onAction(MailAction.UpdateComposeRecipient(""))
        viewModel.onAction(MailAction.UpdateComposeSubject("Subject"))
        viewModel.onAction(MailAction.UpdateComposeBody("Body"))
        viewModel.onAction(MailAction.SendComposeEmail)
        testScheduler.advanceUntilIdle()

        assertEquals("Please enter a recipient email", viewModel.uiState.value.toastMessage)
        assertTrue(viewModel.uiState.value.isComposeOpen)
    }

    @Test
    fun sendEmail_withValidFields_succeedsAndClosesCompose() = runTest {
        viewModel.onAction(MailAction.SetComposeOpen(true))
        viewModel.onAction(MailAction.UpdateComposeRecipient("test@example.com"))
        viewModel.onAction(MailAction.UpdateComposeSubject("Hello"))
        viewModel.onAction(MailAction.UpdateComposeBody("This is a test."))
        viewModel.onAction(MailAction.SendComposeEmail)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isComposeOpen)
        assertEquals("Email sent to test@example.com", viewModel.uiState.value.toastMessage)
        assertEquals(1, viewModel.uiState.value.threads.size)
    }

    @Test
    fun filterUnreadAndStarred_filtersThreadList() = runTest {
        val threads = listOf(
            EmailThread("t1", "User 1", "user1@example.com", "Sub 1", "Snip 1", System.currentTimeMillis(), 1, false, emptyList()),
            EmailThread("t2", "User 2", "user2@example.com", "Sub 2", "Snip 2", System.currentTimeMillis(), 0, true, emptyList())
        )
        fakeRepository.setThreads(threads)
        testScheduler.advanceUntilIdle()

        viewModel.onAction(MailAction.SetFilterUnread(true))
        assertEquals(1, viewModel.uiState.value.filteredThreads.size)
        assertEquals("t1", viewModel.uiState.value.filteredThreads[0].id)

        viewModel.onAction(MailAction.SetFilterUnread(false))
        viewModel.onAction(MailAction.SetFilterStarred(true))
        assertEquals(1, viewModel.uiState.value.filteredThreads.size)
        assertEquals("t2", viewModel.uiState.value.filteredThreads[0].id)
    }
}
