package com.YashDoesCode.comms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.YashDoesCode.comms.ui.MainCommsScreen
import com.YashDoesCode.comms.ui.dialer.DialerViewModel
import com.YashDoesCode.comms.ui.mail.MailViewModel
import com.YashDoesCode.comms.ui.theme.CommsTheme

class MainActivity : ComponentActivity() {

    private val dialerViewModel: DialerViewModel by viewModels {
        val app = application as CommsApp
        DialerViewModel.Factory(app.container)
    }

    private val mailViewModel: MailViewModel by viewModels {
        val app = application as CommsApp
        MailViewModel.Factory(app.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommsTheme {
                MainCommsScreen(
                    dialerViewModel = dialerViewModel,
                    mailViewModel = mailViewModel
                )
            }
        }
    }
}
