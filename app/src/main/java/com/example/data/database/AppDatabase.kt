package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AdminDao
import com.example.data.dao.EventDao
import com.example.data.dao.FinanceDao
import com.example.data.dao.MeetingDao
import com.example.data.dao.SermonDao
import com.example.data.dao.TaskDao
import com.example.data.model.AdminDocument
import com.example.data.model.AdminMember
import com.example.data.model.ChurchEvent
import com.example.data.model.FinanceRecord
import com.example.data.model.Meeting
import com.example.data.model.PastoralTask
import com.example.data.model.Sermon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Sermon::class,
        Meeting::class,
        ChurchEvent::class,
        PastoralTask::class,
        AdminMember::class,
        AdminDocument::class,
        FinanceRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sermonDao(): SermonDao
    abstract fun meetingDao(): MeetingDao
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun adminDao(): AdminDao
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pastoral_ministry.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val now = System.currentTimeMillis()
            val dayMillis = 86_400_000L

            // 1. Initial Sermon
            db.sermonDao().insertSermon(
                Sermon(
                    title = "Ny fahatokiana amin'ny fanompoana an'i Kristy",
                    dateMillis = now + (3 * dayMillis),
                    biblicalPassage = "I Timoty 3:1-7; II Timoty 4:1-5",
                    themeAndObjective = "Fampaherezana ny fiangonana sy ny mpanompo amin'ny faharetana sy ny fahamarinana",
                    introduction = "Ny fiantsoana ho mpiandry ondry dia fanomezam-pahasoavana lehibe avy amin'Andriamanitra. Takina amintsika ny fahatokiana tanteraka.",
                    historicalContext = "Nanoratra ho an'i Timoty tanora i Paoly tamin'ny fotoana nanahirana ny fiangonana tany Efesosy.",
                    verseExegesis = "And. 1: 'Mahatoky izao teny izao' - formula pastoralis manamarika fahamarinana fototra.",
                    theologicalAnalysis = "Ny toetra maha-mpitandrina dia taratry ny toetran'i Kristy Mpiandry Tsara.",
                    biblicalArguments = "I Petera 5:2-4; Hebreo 13:17; Asan'ny Apostoly 20:28.",
                    homileticPlan = "I. Ny fahamendrehana takin'ny fanompoana\nII. Ny faharetana manoloana ny fisedrana\nIII. Ny satroboninahitry ny voninahitra tsy mety levona",
                    practicalApplications = "Fanoloran-tena amin'ny vavaka, fijoroana ho modely ho an'ny fiangonana, fiahiana ny madinika.",
                    illustrations = "Ny sarin'ny mpiandry ondry manara-dia ny ondry very eny an-tendrombohitra.",
                    conclusion = "Katsaho mandrakariva ny sitrapon'ny Tompo amin'ny fonao rehetra.",
                    finalPrayer = "Tompo ô, tahio ny asan'ny mpanomponao ary ampitomboy ny finoanay. Amena.",
                    plannedDurationMinutes = 35,
                    progressPercent = 75,
                    status = "EN_COURS"
                )
            )

            // 2. Initial Meeting
            db.meetingDao().insertMeeting(
                Meeting(
                    title = "Fivorian'ny Biraom-piangonana (Komity Mpanatanteraka)",
                    category = "BUREAU",
                    dateTimeMillis = now + (2 * dayMillis),
                    location = "Trano fiangonana - Efitrano fivoriana",
                    participants = "Mpitandrina, Filoha mpanampy, Mpitahiry vola, Mpitantsoratra, Mpanolo-tsaina",
                    objective = "Fandinihana ny fandaharan'asa ho an'ny telovolana manaraka sy ny fanavaozana ny trano fiangonana",
                    agenda = "1. Vavaka fanokafana\n2. Famakiana ny PV teo aloha\n3. Tatitry ny vola\n4. Hetsika fitoriana filazantsara\n5. Asa fanitarana\n6. Hafa",
                    discussionPoints = "Tetibola ilaina amin'ny fampiofanana; fandaminana ny fanompoam-pivavahana.",
                    decisionsTaken = "Neken'ny rehetra ny hanombohana ny fanavaozana amin'ny volana ambony.",
                    designatedLeaders = "Komitin'ny Asa sy ny Mpitahiry vola",
                    deadlines = "Famaranana ny tolo-bidy mialoha ny faran'ny volana",
                    minutesNotes = "Nisokatra tamin'ny 03:00 tolakandro ny fivoriana. Nofaranana tamin'ny vavaka iombonana.",
                    observations = "Fandraisana anjara feno sy firaisan-kina nasehon'ny mpikambana rehetra.",
                    status = "PREVUE"
                )
            )

            // 3. Initial Event
            db.eventDao().insertEvent(
                ChurchEvent(
                    title = "Semineran'ny Fampianaram-pinoana sy Fiofanana ara-Baiboly",
                    eventType = "SEMINAIRE",
                    description = "Fandalinana ny tenin'Andriamanitra sy fanamafisana ny fahaiza-mitarika ho an'ny mpiandraikitra rehetra.",
                    startDateMillis = now + (10 * dayMillis),
                    endDateMillis = now + (12 * dayMillis),
                    scheduleProgram = "Zoma 17h: Fanokafana sy fotoam-bavaka\nSabotsy 08h-16h: Fampianarana sy atrikasa\nAlahady: Culte de clôture",
                    speakers = "Mpitandrina Solohery RAKOTOARISOA, Mpitandrina Vahiny nasaina",
                    leaders = "Komity mpikarakara ny Fampianarana",
                    tasksTodo = "Fanamarinana ny fitaovana fanamafisam-peo, bokikely fampianarana, fandraisana vahiny",
                    materialNeeds = "Videoprojecteur, micro sans fil, cahiers de notes, fandraisam-bahiny",
                    plannedBudget = 850000.0,
                    realizedExpenses = 200000.0,
                    finalSummaryReview = "",
                    status = "EN_PREPARATION"
                )
            )

            // 4. Initial Tasks
            db.taskDao().insertTask(
                PastoralTask(
                    title = "Famonjena sy fampiononana ny fianakaviana marary",
                    description = "Fitoriana teny sy vavaka ho an'ny mpianakavin'ny finoana ao amin'ny fokontany.",
                    dueDateMillis = now + dayMillis,
                    isCompleted = false,
                    priority = "URGENTE",
                    linkedType = "AUCUN",
                    isAppointment = true
                )
            )
            db.taskDao().insertTask(
                PastoralTask(
                    title = "Famaranana ny drafitry ny toriteny alahady",
                    description = "Fandinihana ireo andininy fanampiny sy fanomanana ny fampiharana azo tsapain-tanana.",
                    dueDateMillis = now + (2 * dayMillis),
                    isCompleted = false,
                    priority = "HAUTE",
                    linkedType = "PREDICATION",
                    isAppointment = false
                )
            )

            // 5. Initial Admin Members
            db.adminDao().insertMember(
                AdminMember(
                    fullName = "Solohery RAKOTOARISOA",
                    roleFunction = "Mpitandrina FJKM / Mpitondra Fitandremana",
                    entityType = "BUREAU",
                    phone = "034 00 000 00",
                    email = "pastor@fitandremana.mg",
                    address = "Trano fonenan'ny Mpitandrina",
                    responsibilities = "Fitarihana ara-panahy, fampianarana, fitoriana ny teny, fitantanana ankapobeny"
                )
            )
            db.adminDao().insertMember(
                AdminMember(
                    fullName = "Andriamatoa Rasoanaivo Jean",
                    roleFunction = "Filoha Mpanampy ny Biraom-piangonana",
                    entityType = "BUREAU",
                    phone = "032 11 222 33",
                    email = "jean.rasoanaivo@gmail.com",
                    address = "Antananarivo",
                    responsibilities = "Fanampiana ny mpitandrina amin'ny fandrindrana ny fiangonana sy ny fivoriana"
                )
            )

            // 6. Initial Admin Document
            db.adminDao().insertDocument(
                AdminDocument(
                    title = "Fandaminana ankapoben'ny Fitandremana",
                    documentType = "REGLEMENT",
                    dateMillis = now - (30 * dayMillis),
                    contentOrSummary = "Fitsipika anatiny mifehy ny fiangonana, ny komity, ary ny sampam-pikambanana.",
                    referenceNumber = "DOC-ADM-2026-001"
                )
            )

            // 7. Initial Finance Records
            db.financeDao().insertFinance(
                FinanceRecord(
                    title = "Ampahafolonkarena sy Fanatitra Alahady",
                    transactionType = "RECETTE",
                    category = "DIME_FANATITRA",
                    amount = 1450000.0,
                    currency = "MGA",
                    dateMillis = now - (4 * dayMillis),
                    notes = "Vola voaray tamin'ny fotoam-pivavahana alahady teo"
                )
            )
            db.financeDao().insertFinance(
                FinanceRecord(
                    title = "Fikojakojana ny fitaovam-panamafisam-peo",
                    transactionType = "DEPENSE",
                    category = "CHARGES",
                    amount = 180000.0,
                    currency = "MGA",
                    dateMillis = now - (2 * dayMillis),
                    notes = "Fanamboarana micro sy tariby tao amin'ny alitara"
                )
            )
        }
    }
}
