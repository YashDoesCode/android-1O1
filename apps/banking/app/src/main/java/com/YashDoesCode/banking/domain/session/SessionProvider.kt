package com.YashDoesCode.banking.domain.session

/**
 * Provides the current authenticated user identity.
 *
 * // TODO(auth): replace mock session with real auth; data layer stays unchanged.
 */
interface SessionProvider {
    val currentUserId: String
    val currentUserName: String
    val currentUserEmail: String
}

class MockSessionProvider : SessionProvider {
    override val currentUserId: String = "user_demo_42"
    override val currentUserName: String = "Yash Srivastava"
    override val currentUserEmail: String = "yash@example.com"
}
