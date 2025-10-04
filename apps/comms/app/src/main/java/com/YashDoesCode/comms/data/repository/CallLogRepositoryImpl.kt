package com.YashDoesCode.comms.data.repository

import com.YashDoesCode.comms.data.datasource.CallLogDataSource
import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.repository.CallLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CallLogRepositoryImpl(
    private val dataSource: CallLogDataSource
) : CallLogRepository {

    override fun getRecentCalls(): Flow<List<CallLogEntry>> = flow {
        emit(dataSource.fetchRecentCalls())
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchRecentCalls(): Result<List<CallLogEntry>> = withContext(Dispatchers.IO) {
        try {
            Result.success(dataSource.fetchRecentCalls())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
