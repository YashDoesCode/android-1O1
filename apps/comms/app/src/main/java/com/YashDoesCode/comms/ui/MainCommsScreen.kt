package com.YashDoesCode.comms.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.YashDoesCode.comms.ui.components.CommsBottomBar
import com.YashDoesCode.comms.ui.components.CommsTab
import com.YashDoesCode.comms.ui.dialer.DialerScreen
import com.YashDoesCode.comms.ui.dialer.DialerViewModel
import com.YashDoesCode.comms.ui.mail.MailScreen
import com.YashDoesCode.comms.ui.mail.MailViewModel

@Composable
fun MainCommsScreen(
    dialerViewModel: DialerViewModel,
    mailViewModel: MailViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(CommsTab.CALLS) }

    Scaffold(
        bottomBar = {
            CommsBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        when (currentTab) {
            CommsTab.CALLS -> {
                DialerScreen(
                    viewModel = dialerViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            CommsTab.MAIL -> {
                MailScreen(
                    viewModel = mailViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
