package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sermons")
data class Sermon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateMillis: Long,
    val biblicalPassage: String,
    val themeAndObjective: String = "",
    val introduction: String = "",
    val historicalContext: String = "",
    val verseExegesis: String = "",
    val theologicalAnalysis: String = "",
    val biblicalArguments: String = "",
    val homileticPlan: String = "",
    val practicalApplications: String = "",
    val illustrations: String = "",
    val conclusion: String = "",
    val finalPrayer: String = "",
    val plannedDurationMinutes: Int = 30,
    val progressPercent: Int = 0,
    val status: String = "A_COMMENCER", // A_COMMENCER, EN_COURS, A_REVISER, TERMINEE, PRECHEE
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "meetings")
data class Meeting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "BUREAU", // BUREAU, RESPONSABLES, COMMISSION, DEPARTEMENT, AUTRE
    val dateTimeMillis: Long,
    val location: String = "",
    val participants: String = "",
    val objective: String = "",
    val agenda: String = "",
    val discussionPoints: String = "",
    val decisionsTaken: String = "",
    val designatedLeaders: String = "",
    val deadlines: String = "",
    val minutesNotes: String = "",
    val observations: String = "",
    val status: String = "PREVUE" // PREVUE, EN_COURS, CLOTUREE, REPORTEE
)

@Entity(tableName = "events")
data class ChurchEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val eventType: String = "CONFERENCE", // CONFERENCE, SEMINAIRE, FORMATION, EVANGELISATION, CULTE_SPECIAL, PRIERE, ENSEIGNEMENT_FOI, MARIAGE, FUNERAILLES, AUTRE
    val description: String = "",
    val startDateMillis: Long,
    val endDateMillis: Long,
    val scheduleProgram: String = "",
    val speakers: String = "",
    val leaders: String = "",
    val tasksTodo: String = "",
    val materialNeeds: String = "",
    val plannedBudget: Double = 0.0,
    val realizedExpenses: Double = 0.0,
    val finalSummaryReview: String = "",
    val status: String = "PLANIFIE" // PLANIFIE, EN_PREPARATION, EN_COURS, TERMINE, ANNULE
)

@Entity(tableName = "tasks")
data class PastoralTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDateMillis: Long,
    val isCompleted: Boolean = false,
    val priority: String = "MOYENNE", // BASSE, MOYENNE, HAUTE, URGENTE
    val linkedType: String = "AUCUN", // AUCUN, PREDICATION, REUNION, EVENEMENT
    val linkedId: Long? = null,
    val reminderSet: Boolean = false,
    val reminderTimeMillis: Long? = null,
    val isAppointment: Boolean = false
)

@Entity(tableName = "admin_members")
data class AdminMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val roleFunction: String,
    val entityType: String = "BUREAU", // BUREAU, RESPONSABLE, COMMISSION, DEPARTEMENT
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val responsibilities: String = "",
    val notes: String = ""
)

@Entity(tableName = "admin_documents")
data class AdminDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val documentType: String = "REGLEMENT", // REGLEMENT, RAPPORT, LETTRE, CERTIFICAT, DECRET, AUTRE
    val dateMillis: Long,
    val contentOrSummary: String = "",
    val referenceNumber: String = ""
)

@Entity(tableName = "finances")
data class FinanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val transactionType: String, // RECETTE, DEPENSE, BUDGET_PREVISIONNEL, DON_CONTRIBUTION
    val category: String, // DIME_FANATITRA, DON_MINISTERE, TRAVAUX_BATIMENT, EVENEMENT, CHARGES, MISSION, SOCIAL_ENTRAIDE, AUTRE
    val amount: Double,
    val currency: String = "MGA", // MGA, EUR, USD
    val dateMillis: Long,
    val notes: String = "",
    val linkedEventId: Long? = null
)
