package com.YashDoesCode.comms.data.datasource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat
import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.CallType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CallLogDataSource(private val context: Context) {

    suspend fun fetchRecentCalls(): List<CallLogEntry> = withContext(Dispatchers.IO) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return@withContext getSampleCallLogs()
        }

        val callLogs = mutableListOf<CallLogEntry>()
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )

        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(CallLog.Calls._ID)
                val numberCol = it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val nameCol = it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                val typeCol = it.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                val dateCol = it.getColumnIndexOrThrow(CallLog.Calls.DATE)
                val durationCol = it.getColumnIndexOrThrow(CallLog.Calls.DURATION)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val number = it.getString(numberCol) ?: ""
                    val name = it.getString(nameCol)
                    val typeInt = it.getInt(typeCol)
                    val date = it.getLong(dateCol)
                    val duration = it.getLong(durationCol)

                    val callType = when (typeInt) {
                        CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                        CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                        CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                        CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
                        else -> CallType.UNKNOWN
                    }

                    callLogs.add(
                        CallLogEntry(
                            id = id,
                            number = number,
                            callerName = name,
                            type = callType,
                            timestamp = date,
                            durationSeconds = duration
                        )
                    )
                }
            }
        } catch (e: Exception) {
            return@withContext getSampleCallLogs()
        }

        if (callLogs.isEmpty()) {
            getSampleCallLogs()
        } else {
            callLogs
        }
    }

    private fun getSampleCallLogs(): List<CallLogEntry> {
        val now = System.currentTimeMillis()
        return listOf(
            CallLogEntry(1L, "+1 (555) 123-4567", "Alice Johnson", CallType.INCOMING, now - 1000 * 60 * 15, 120),
            CallLogEntry(2L, "+1 (555) 234-5678", "Bob Smith", CallType.MISSED, now - 1000 * 60 * 60 * 2, 0),
            CallLogEntry(3L, "+1 (555) 345-6789", "Charlie Brown", CallType.OUTGOING, now - 1000 * 60 * 60 * 5, 340),
            CallLogEntry(4L, "+1 (555) 456-7890", "Diana Prince", CallType.REJECTED, now - 1000 * 60 * 60 * 24, 0),
            CallLogEntry(5L, "+1 (555) 567-8901", "Evan Wright", CallType.INCOMING, now - 1000 * 60 * 60 * 48, 65)
        )
    }
}
