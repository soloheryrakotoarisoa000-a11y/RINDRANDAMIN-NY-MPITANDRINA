package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.database.AppDatabase
import com.example.data.model.AdminDocument
import com.example.data.model.AdminMember
import com.example.data.model.ChurchEvent
import com.example.data.model.FinanceRecord
import com.example.data.model.Meeting
import com.example.data.model.PastoralTask
import com.example.data.model.Sermon
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    suspend fun createBackupJson(context: Context, database: AppDatabase): File {
        val root = JSONObject()
        root.put("app", "RINDRANDAMIN_NY_MPITANDRINA")
        root.put("version", 1)
        root.put("creator", "Solohery RAKOTOARISOA")
        root.put("timestamp", System.currentTimeMillis())

        // 1. Sermons
        val sermons = database.sermonDao().getAllSermons().first()
        val sermonsArray = JSONArray()
        for (s in sermons) {
            val obj = JSONObject()
            obj.put("title", s.title)
            obj.put("dateMillis", s.dateMillis)
            obj.put("biblicalPassage", s.biblicalPassage)
            obj.put("themeAndObjective", s.themeAndObjective)
            obj.put("introduction", s.introduction)
            obj.put("historicalContext", s.historicalContext)
            obj.put("verseExegesis", s.verseExegesis)
            obj.put("theologicalAnalysis", s.theologicalAnalysis)
            obj.put("biblicalArguments", s.biblicalArguments)
            obj.put("homileticPlan", s.homileticPlan)
            obj.put("practicalApplications", s.practicalApplications)
            obj.put("illustrations", s.illustrations)
            obj.put("conclusion", s.conclusion)
            obj.put("finalPrayer", s.finalPrayer)
            obj.put("plannedDurationMinutes", s.plannedDurationMinutes)
            obj.put("progressPercent", s.progressPercent)
            obj.put("status", s.status)
            sermonsArray.put(obj)
        }
        root.put("sermons", sermonsArray)

        // 2. Meetings
        val meetings = database.meetingDao().getAllMeetings().first()
        val meetingsArray = JSONArray()
        for (m in meetings) {
            val obj = JSONObject()
            obj.put("title", m.title)
            obj.put("category", m.category)
            obj.put("dateTimeMillis", m.dateTimeMillis)
            obj.put("location", m.location)
            obj.put("participants", m.participants)
            obj.put("objective", m.objective)
            obj.put("agenda", m.agenda)
            obj.put("discussionPoints", m.discussionPoints)
            obj.put("decisionsTaken", m.decisionsTaken)
            obj.put("designatedLeaders", m.designatedLeaders)
            obj.put("deadlines", m.deadlines)
            obj.put("minutesNotes", m.minutesNotes)
            obj.put("observations", m.observations)
            obj.put("status", m.status)
            meetingsArray.put(obj)
        }
        root.put("meetings", meetingsArray)

        // 3. Events
        val events = database.eventDao().getAllEvents().first()
        val eventsArray = JSONArray()
        for (e in events) {
            val obj = JSONObject()
            obj.put("title", e.title)
            obj.put("eventType", e.eventType)
            obj.put("description", e.description)
            obj.put("startDateMillis", e.startDateMillis)
            obj.put("endDateMillis", e.endDateMillis)
            obj.put("scheduleProgram", e.scheduleProgram)
            obj.put("speakers", e.speakers)
            obj.put("leaders", e.leaders)
            obj.put("tasksTodo", e.tasksTodo)
            obj.put("materialNeeds", e.materialNeeds)
            obj.put("plannedBudget", e.plannedBudget)
            obj.put("realizedExpenses", e.realizedExpenses)
            obj.put("finalSummaryReview", e.finalSummaryReview)
            obj.put("status", e.status)
            eventsArray.put(obj)
        }
        root.put("events", eventsArray)

        // 4. Tasks
        val tasks = database.taskDao().getAllTasks().first()
        val tasksArray = JSONArray()
        for (t in tasks) {
            val obj = JSONObject()
            obj.put("title", t.title)
            obj.put("description", t.description)
            obj.put("dueDateMillis", t.dueDateMillis)
            obj.put("isCompleted", t.isCompleted)
            obj.put("priority", t.priority)
            obj.put("linkedType", t.linkedType)
            obj.put("isAppointment", t.isAppointment)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        // 5. Admin Members & Documents
        val members = database.adminDao().getAllMembers().first()
        val membersArray = JSONArray()
        for (mem in members) {
            val obj = JSONObject()
            obj.put("fullName", mem.fullName)
            obj.put("roleFunction", mem.roleFunction)
            obj.put("entityType", mem.entityType)
            obj.put("phone", mem.phone)
            obj.put("email", mem.email)
            obj.put("address", mem.address)
            obj.put("responsibilities", mem.responsibilities)
            membersArray.put(obj)
        }
        root.put("members", membersArray)

        val documents = database.adminDao().getAllDocuments().first()
        val docsArray = JSONArray()
        for (doc in documents) {
            val obj = JSONObject()
            obj.put("title", doc.title)
            obj.put("documentType", doc.documentType)
            obj.put("dateMillis", doc.dateMillis)
            obj.put("contentOrSummary", doc.contentOrSummary)
            obj.put("referenceNumber", doc.referenceNumber)
            docsArray.put(obj)
        }
        root.put("documents", docsArray)

        // 6. Finances
        val finances = database.financeDao().getAllFinances().first()
        val financesArray = JSONArray()
        for (f in finances) {
            val obj = JSONObject()
            obj.put("title", f.title)
            obj.put("transactionType", f.transactionType)
            obj.put("category", f.category)
            obj.put("amount", f.amount)
            obj.put("currency", f.currency)
            obj.put("dateMillis", f.dateMillis)
            obj.put("notes", f.notes)
            financesArray.put(obj)
        }
        root.put("finances", financesArray)

        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "pastoral_backup_${sdf.format(Date())}.json"
        val file = File(backupDir, fileName)
        val fos = FileOutputStream(file)
        fos.write(root.toString(2).toByteArray(Charsets.UTF_8))
        fos.close()

        return file
    }

    fun shareBackup(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "RINDRANDAMIN'NY MPITANDRINA - Tahiry Backup")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Hizara na hitahiry ny angona").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    suspend fun restoreFromJsonString(jsonString: String, database: AppDatabase): Boolean {
        return try {
            val root = JSONObject(jsonString)

            if (root.has("sermons")) {
                val array = root.getJSONArray("sermons")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.sermonDao().insertSermon(
                        Sermon(
                            title = obj.optString("title", ""),
                            dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                            biblicalPassage = obj.optString("biblicalPassage", ""),
                            themeAndObjective = obj.optString("themeAndObjective", ""),
                            introduction = obj.optString("introduction", ""),
                            historicalContext = obj.optString("historicalContext", ""),
                            verseExegesis = obj.optString("verseExegesis", ""),
                            theologicalAnalysis = obj.optString("theologicalAnalysis", ""),
                            biblicalArguments = obj.optString("biblicalArguments", ""),
                            homileticPlan = obj.optString("homileticPlan", ""),
                            practicalApplications = obj.optString("practicalApplications", ""),
                            illustrations = obj.optString("illustrations", ""),
                            conclusion = obj.optString("conclusion", ""),
                            finalPrayer = obj.optString("finalPrayer", ""),
                            plannedDurationMinutes = obj.optInt("plannedDurationMinutes", 30),
                            progressPercent = obj.optInt("progressPercent", 0),
                            status = obj.optString("status", "A_COMMENCER")
                        )
                    )
                }
            }

            if (root.has("meetings")) {
                val array = root.getJSONArray("meetings")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.meetingDao().insertMeeting(
                        Meeting(
                            title = obj.optString("title", ""),
                            category = obj.optString("category", "BUREAU"),
                            dateTimeMillis = obj.optLong("dateTimeMillis", System.currentTimeMillis()),
                            location = obj.optString("location", ""),
                            participants = obj.optString("participants", ""),
                            objective = obj.optString("objective", ""),
                            agenda = obj.optString("agenda", ""),
                            discussionPoints = obj.optString("discussionPoints", ""),
                            decisionsTaken = obj.optString("decisionsTaken", ""),
                            designatedLeaders = obj.optString("designatedLeaders", ""),
                            deadlines = obj.optString("deadlines", ""),
                            minutesNotes = obj.optString("minutesNotes", ""),
                            observations = obj.optString("observations", ""),
                            status = obj.optString("status", "PREVUE")
                        )
                    )
                }
            }

            if (root.has("events")) {
                val array = root.getJSONArray("events")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.eventDao().insertEvent(
                        ChurchEvent(
                            title = obj.optString("title", ""),
                            eventType = obj.optString("eventType", "CONFERENCE"),
                            description = obj.optString("description", ""),
                            startDateMillis = obj.optLong("startDateMillis", System.currentTimeMillis()),
                            endDateMillis = obj.optLong("endDateMillis", System.currentTimeMillis()),
                            scheduleProgram = obj.optString("scheduleProgram", ""),
                            speakers = obj.optString("speakers", ""),
                            leaders = obj.optString("leaders", ""),
                            tasksTodo = obj.optString("tasksTodo", ""),
                            materialNeeds = obj.optString("materialNeeds", ""),
                            plannedBudget = obj.optDouble("plannedBudget", 0.0),
                            realizedExpenses = obj.optDouble("realizedExpenses", 0.0),
                            finalSummaryReview = obj.optString("finalSummaryReview", ""),
                            status = obj.optString("status", "PLANIFIE")
                        )
                    )
                }
            }

            if (root.has("tasks")) {
                val array = root.getJSONArray("tasks")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.taskDao().insertTask(
                        PastoralTask(
                            title = obj.optString("title", ""),
                            description = obj.optString("description", ""),
                            dueDateMillis = obj.optLong("dueDateMillis", System.currentTimeMillis()),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            priority = obj.optString("priority", "MOYENNE"),
                            linkedType = obj.optString("linkedType", "AUCUN"),
                            isAppointment = obj.optBoolean("isAppointment", false)
                        )
                    )
                }
            }

            if (root.has("members")) {
                val array = root.getJSONArray("members")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.adminDao().insertMember(
                        AdminMember(
                            fullName = obj.optString("fullName", ""),
                            roleFunction = obj.optString("roleFunction", ""),
                            entityType = obj.optString("entityType", "BUREAU"),
                            phone = obj.optString("phone", ""),
                            email = obj.optString("email", ""),
                            address = obj.optString("address", ""),
                            responsibilities = obj.optString("responsibilities", "")
                        )
                    )
                }
            }

            if (root.has("documents")) {
                val array = root.getJSONArray("documents")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.adminDao().insertDocument(
                        AdminDocument(
                            title = obj.optString("title", ""),
                            documentType = obj.optString("documentType", "REGLEMENT"),
                            dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                            contentOrSummary = obj.optString("contentOrSummary", ""),
                            referenceNumber = obj.optString("referenceNumber", "")
                        )
                    )
                }
            }

            if (root.has("finances")) {
                val array = root.getJSONArray("finances")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    database.financeDao().insertFinance(
                        FinanceRecord(
                            title = obj.optString("title", ""),
                            transactionType = obj.optString("transactionType", "RECETTE"),
                            category = obj.optString("category", "DIME_FANATITRA"),
                            amount = obj.optDouble("amount", 0.0),
                            currency = obj.optString("currency", "MGA"),
                            dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
