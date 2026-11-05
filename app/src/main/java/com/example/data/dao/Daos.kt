package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminDocument
import com.example.data.model.AdminMember
import com.example.data.model.ChurchEvent
import com.example.data.model.FinanceRecord
import com.example.data.model.Meeting
import com.example.data.model.PastoralTask
import com.example.data.model.Sermon
import kotlinx.coroutines.flow.Flow

@Dao
interface SermonDao {
    @Query("SELECT * FROM sermons ORDER BY dateMillis DESC")
    fun getAllSermons(): Flow<List<Sermon>>

    @Query("SELECT * FROM sermons WHERE id = :id LIMIT 1")
    suspend fun getSermonById(id: Long): Sermon?

    @Query("SELECT * FROM sermons WHERE dateMillis >= :startMillis ORDER BY dateMillis ASC LIMIT 5")
    fun getUpcomingSermons(startMillis: Long): Flow<List<Sermon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSermon(sermon: Sermon): Long

    @Update
    suspend fun updateSermon(sermon: Sermon)

    @Delete
    suspend fun deleteSermon(sermon: Sermon)

    @Query("DELETE FROM sermons WHERE id = :id")
    suspend fun deleteSermonById(id: Long)
}

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings ORDER BY dateTimeMillis DESC")
    fun getAllMeetings(): Flow<List<Meeting>>

    @Query("SELECT * FROM meetings WHERE id = :id LIMIT 1")
    suspend fun getMeetingById(id: Long): Meeting?

    @Query("SELECT * FROM meetings WHERE dateTimeMillis >= :startMillis ORDER BY dateTimeMillis ASC LIMIT 5")
    fun getUpcomingMeetings(startMillis: Long): Flow<List<Meeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: Meeting): Long

    @Update
    suspend fun updateMeeting(meeting: Meeting)

    @Delete
    suspend fun deleteMeeting(meeting: Meeting)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeetingById(id: Long)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startDateMillis DESC")
    fun getAllEvents(): Flow<List<ChurchEvent>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): ChurchEvent?

    @Query("SELECT * FROM events WHERE endDateMillis >= :nowMillis ORDER BY startDateMillis ASC LIMIT 5")
    fun getUpcomingEvents(nowMillis: Long): Flow<List<ChurchEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ChurchEvent): Long

    @Update
    suspend fun updateEvent(event: ChurchEvent)

    @Delete
    suspend fun deleteEvent(event: ChurchEvent)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getAllTasks(): Flow<List<PastoralTask>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): PastoralTask?

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND (priority = 'URGENTE' OR priority = 'HAUTE') ORDER BY dueDateMillis ASC LIMIT 5")
    fun getUrgentTasks(): Flow<List<PastoralTask>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDateMillis < :nowMillis ORDER BY dueDateMillis ASC")
    fun getOverdueTasks(nowMillis: Long): Flow<List<PastoralTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: PastoralTask): Long

    @Update
    suspend fun updateTask(task: PastoralTask)

    @Delete
    suspend fun deleteTask(task: PastoralTask)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
}

@Dao
interface AdminDao {
    @Query("SELECT * FROM admin_members ORDER BY entityType ASC, fullName ASC")
    fun getAllMembers(): Flow<List<AdminMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: AdminMember): Long

    @Update
    suspend fun updateMember(member: AdminMember)

    @Delete
    suspend fun deleteMember(member: AdminMember)

    @Query("DELETE FROM admin_members WHERE id = :id")
    suspend fun deleteMemberById(id: Long)

    @Query("SELECT * FROM admin_documents ORDER BY dateMillis DESC")
    fun getAllDocuments(): Flow<List<AdminDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: AdminDocument): Long

    @Update
    suspend fun updateDocument(document: AdminDocument)

    @Delete
    suspend fun deleteDocument(document: AdminDocument)

    @Query("DELETE FROM admin_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finances ORDER BY dateMillis DESC")
    fun getAllFinances(): Flow<List<FinanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinance(record: FinanceRecord): Long

    @Update
    suspend fun updateFinance(record: FinanceRecord)

    @Delete
    suspend fun deleteFinance(record: FinanceRecord)

    @Query("DELETE FROM finances WHERE id = :id")
    suspend fun deleteFinanceById(id: Long)
}
