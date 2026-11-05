package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.AdminDocument
import com.example.data.model.AdminMember
import com.example.data.model.ChurchEvent
import com.example.data.model.FinanceRecord
import com.example.data.model.Meeting
import com.example.data.model.PastoralTask
import com.example.data.model.Sermon
import kotlinx.coroutines.flow.Flow

class PastoralRepository(private val db: AppDatabase) {

    // Sermons
    val allSermons: Flow<List<Sermon>> = db.sermonDao().getAllSermons()
    fun getUpcomingSermons(startMillis: Long): Flow<List<Sermon>> = db.sermonDao().getUpcomingSermons(startMillis)
    suspend fun getSermonById(id: Long): Sermon? = db.sermonDao().getSermonById(id)
    suspend fun insertSermon(sermon: Sermon): Long = db.sermonDao().insertSermon(sermon)
    suspend fun updateSermon(sermon: Sermon) = db.sermonDao().updateSermon(sermon)
    suspend fun deleteSermon(sermon: Sermon) = db.sermonDao().deleteSermon(sermon)
    suspend fun deleteSermonById(id: Long) = db.sermonDao().deleteSermonById(id)

    // Meetings
    val allMeetings: Flow<List<Meeting>> = db.meetingDao().getAllMeetings()
    fun getUpcomingMeetings(startMillis: Long): Flow<List<Meeting>> = db.meetingDao().getUpcomingMeetings(startMillis)
    suspend fun getMeetingById(id: Long): Meeting? = db.meetingDao().getMeetingById(id)
    suspend fun insertMeeting(meeting: Meeting): Long = db.meetingDao().insertMeeting(meeting)
    suspend fun updateMeeting(meeting: Meeting) = db.meetingDao().updateMeeting(meeting)
    suspend fun deleteMeeting(meeting: Meeting) = db.meetingDao().deleteMeeting(meeting)
    suspend fun deleteMeetingById(id: Long) = db.meetingDao().deleteMeetingById(id)

    // Events
    val allEvents: Flow<List<ChurchEvent>> = db.eventDao().getAllEvents()
    fun getUpcomingEvents(nowMillis: Long): Flow<List<ChurchEvent>> = db.eventDao().getUpcomingEvents(nowMillis)
    suspend fun getEventById(id: Long): ChurchEvent? = db.eventDao().getEventById(id)
    suspend fun insertEvent(event: ChurchEvent): Long = db.eventDao().insertEvent(event)
    suspend fun updateEvent(event: ChurchEvent) = db.eventDao().updateEvent(event)
    suspend fun deleteEvent(event: ChurchEvent) = db.eventDao().deleteEvent(event)
    suspend fun deleteEventById(id: Long) = db.eventDao().deleteEventById(id)

    // Tasks & Agenda
    val allTasks: Flow<List<PastoralTask>> = db.taskDao().getAllTasks()
    val urgentTasks: Flow<List<PastoralTask>> = db.taskDao().getUrgentTasks()
    fun getOverdueTasks(nowMillis: Long): Flow<List<PastoralTask>> = db.taskDao().getOverdueTasks(nowMillis)
    suspend fun getTaskById(id: Long): PastoralTask? = db.taskDao().getTaskById(id)
    suspend fun insertTask(task: PastoralTask): Long = db.taskDao().insertTask(task)
    suspend fun updateTask(task: PastoralTask) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: PastoralTask) = db.taskDao().deleteTask(task)
    suspend fun deleteTaskById(id: Long) = db.taskDao().deleteTaskById(id)

    // Admin
    val allMembers: Flow<List<AdminMember>> = db.adminDao().getAllMembers()
    suspend fun insertMember(member: AdminMember): Long = db.adminDao().insertMember(member)
    suspend fun updateMember(member: AdminMember) = db.adminDao().updateMember(member)
    suspend fun deleteMember(member: AdminMember) = db.adminDao().deleteMember(member)
    suspend fun deleteMemberById(id: Long) = db.adminDao().deleteMemberById(id)

    val allDocuments: Flow<List<AdminDocument>> = db.adminDao().getAllDocuments()
    suspend fun insertDocument(document: AdminDocument): Long = db.adminDao().insertDocument(document)
    suspend fun updateDocument(document: AdminDocument) = db.adminDao().updateDocument(document)
    suspend fun deleteDocument(document: AdminDocument) = db.adminDao().deleteDocument(document)
    suspend fun deleteDocumentById(id: Long) = db.adminDao().deleteDocumentById(id)

    // Finances
    val allFinances: Flow<List<FinanceRecord>> = db.financeDao().getAllFinances()
    suspend fun insertFinance(record: FinanceRecord): Long = db.financeDao().insertFinance(record)
    suspend fun updateFinance(record: FinanceRecord) = db.financeDao().updateFinance(record)
    suspend fun deleteFinance(record: FinanceRecord) = db.financeDao().deleteFinance(record)
    suspend fun deleteFinanceById(id: Long) = db.financeDao().deleteFinanceById(id)
}
