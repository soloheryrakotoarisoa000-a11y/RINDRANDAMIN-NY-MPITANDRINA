package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.FinanceRecord
import com.example.data.model.Meeting
import com.example.data.model.Sermon
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private fun getPdfDir(context: Context): File {
        val dir = File(context.cacheDir, "pdf")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun shareFile(context: Context, file: File, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun exportSermonPdf(context: Context, sermon: Sermon) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(13, 27, 54) // Navy
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.rgb(197, 160, 89) // Gold
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionPaint = Paint().apply {
            color = Color.rgb(13, 27, 54)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(40, 45, 55)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(200, 205, 215)
            strokeWidth = 1f
        }

        var y = 40f
        val left = 40f
        val right = 555f
        val lineHeight = 14f

        // Header
        canvas.drawText("RINDRANDAMIN'NY MPITANDRINA", left, y, subtitlePaint)
        y += 18f
        canvas.drawText("FANOMANANA NY TORITENY", left, y, titlePaint)
        y += 8f
        canvas.drawLine(left, y, right, y, linePaint)
        y += 20f

        // Metadata
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date(sermon.dateMillis))
        canvas.drawText("Lohahevitra: ${sermon.title}", left, y, sectionPaint)
        y += 16f
        canvas.drawText("Soratra Masina: ${sermon.biblicalPassage}  |  Daty: $dateStr  |  Faharetana: ${sermon.plannedDurationMinutes} min", left, y, bodyPaint)
        y += 16f
        if (sermon.themeAndObjective.isNotBlank()) {
            canvas.drawText("Tanjona sy Hafatra: ${sermon.themeAndObjective}", left, y, bodyPaint)
            y += 16f
        }
        canvas.drawLine(left, y, right, y, linePaint)
        y += 18f

        // Sections helper
        fun drawSection(sectionTitle: String, text: String) {
            if (text.isBlank() || y > 790f) return
            canvas.drawText(sectionTitle, left, y, sectionPaint)
            y += 14f
            val words = text.split("\n", " ")
            var line = ""
            for (word in words) {
                if (word == "\n") {
                    canvas.drawText(line, left, y, bodyPaint)
                    y += lineHeight
                    line = ""
                    if (y > 800f) break
                    continue
                }
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (bodyPaint.measureText(testLine) > (right - left)) {
                    canvas.drawText(line, left, y, bodyPaint)
                    y += lineHeight
                    line = word
                    if (y > 800f) break
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty() && y <= 800f) {
                canvas.drawText(line, left, y, bodyPaint)
                y += lineHeight
            }
            y += 8f
        }

        drawSection("1. FAMPIDIRANA", sermon.introduction)
        drawSection("2. TONTOLO MANODIDINA (CONTEXTE)", sermon.historicalContext)
        drawSection("3. EXEGESE SY TEOLOJIA", sermon.verseExegesis)
        drawSection("4. DRAFITRY NY TORITENY (PLAN)", sermon.homileticPlan)
        drawSection("5. FAMPIHARANA SY FANOHARANA", sermon.practicalApplications)
        drawSection("6. FAMARANANA SY VAVAKA", sermon.conclusion)

        // Footer
        val footerPaint = Paint().apply {
            color = Color.rgb(120, 130, 145)
            textSize = 8f
            isAntiAlias = true
        }
        canvas.drawText("RINDRANDAMIN'NY MPITANDRINA — Solohery RAKOTOARISOA — I Timoty 3.1", left, 820f, footerPaint)

        document.finishPage(page)

        val file = File(getPdfDir(context), "Toriteny_${System.currentTimeMillis()}.pdf")
        val stream = FileOutputStream(file)
        document.writeTo(stream)
        stream.close()
        document.close()

        shareFile(context, file, sermon.title)
    }

    fun exportMeetingMinutesPdf(context: Context, meeting: Meeting) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(13, 27, 54)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.rgb(197, 160, 89)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val sectionPaint = Paint().apply {
            color = Color.rgb(13, 27, 54)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.rgb(40, 45, 55)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.rgb(200, 205, 215)
            strokeWidth = 1f
        }

        var y = 40f
        val left = 40f
        val right = 555f
        val lineHeight = 14f

        canvas.drawText("RINDRANDAMIN'NY MPITANDRINA", left, y, subtitlePaint)
        y += 18f
        canvas.drawText("PROCES-VERBAL NY FIVORIANA", left, y, titlePaint)
        y += 8f
        canvas.drawLine(left, y, right, y, linePaint)
        y += 20f

        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date(meeting.dateTimeMillis))
        canvas.drawText("Lohan-kevitra: ${meeting.title}", left, y, sectionPaint)
        y += 16f
        canvas.drawText("Daty sy Ora: $dateStr  |  Toerana: ${meeting.location.ifBlank { "Fiangonana" }}", left, y, bodyPaint)
        y += 16f
        canvas.drawText("Sokajy: ${meeting.category}  |  Mpanatrika: ${meeting.participants}", left, y, bodyPaint)
        y += 16f
        canvas.drawLine(left, y, right, y, linePaint)
        y += 18f

        fun drawSection(sectionTitle: String, text: String) {
            if (text.isBlank() || y > 790f) return
            canvas.drawText(sectionTitle, left, y, sectionPaint)
            y += 14f
            val words = text.split("\n", " ")
            var line = ""
            for (word in words) {
                if (word == "\n") {
                    canvas.drawText(line, left, y, bodyPaint)
                    y += lineHeight
                    line = ""
                    if (y > 800f) break
                    continue
                }
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (bodyPaint.measureText(testLine) > (right - left)) {
                    canvas.drawText(line, left, y, bodyPaint)
                    y += lineHeight
                    line = word
                    if (y > 800f) break
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty() && y <= 800f) {
                canvas.drawText(line, left, y, bodyPaint)
                y += lineHeight
            }
            y += 8f
        }

        drawSection("1. ORDRE DU JOUR / FANDAHARAM-POTOANA", meeting.agenda)
        drawSection("2. HEVITRA NODINIHINA", meeting.discussionPoints)
        drawSection("3. FANAPAHAN-KEVITRA NORAISINA", meeting.decisionsTaken)
        drawSection("4. TOMPON'ANDRAIKITRA SY FE-POTOANA", "${meeting.designatedLeaders} (Fe-potoana: ${meeting.deadlines})")
        drawSection("5. TATITRA SY FANAMARIHANA (PV)", meeting.minutesNotes)

        val footerPaint = Paint().apply {
            color = Color.rgb(120, 130, 145)
            textSize = 8f
            isAntiAlias = true
        }
        canvas.drawText("RINDRANDAMIN'NY MPITANDRINA — Solohery RAKOTOARISOA", left, 820f, footerPaint)

        document.finishPage(page)

        val file = File(getPdfDir(context), "PV_Fivoriana_${System.currentTimeMillis()}.pdf")
        val stream = FileOutputStream(file)
        document.writeTo(stream)
        stream.close()
        document.close()

        shareFile(context, file, meeting.title)
    }

    fun exportFinancialReportPdf(context: Context, finances: List<FinanceRecord>, currency: String) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(13, 27, 54)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.rgb(197, 160, 89)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val sectionPaint = Paint().apply {
            color = Color.rgb(13, 27, 54)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.rgb(40, 45, 55)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val boldBodyPaint = Paint().apply {
            color = Color.rgb(40, 45, 55)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.rgb(200, 205, 215)
            strokeWidth = 1f
        }

        var y = 40f
        val left = 40f
        val right = 555f

        canvas.drawText("RINDRANDAMIN'NY MPITANDRINA", left, y, subtitlePaint)
        y += 18f
        canvas.drawText("TATITRA ARA-BOLAN'NY FIANGONANA", left, y, titlePaint)
        y += 8f
        canvas.drawLine(left, y, right, y, linePaint)
        y += 20f

        val totalIncome = finances.filter { it.transactionType == "RECETTE" || it.transactionType == "DON_CONTRIBUTION" }.sumOf { it.amount }
        val totalExpense = finances.filter { it.transactionType == "DEPENSE" }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        canvas.drawText("Daty nanaovana ny tatitra: ${df.format(Date())}", left, y, bodyPaint)
        y += 16f
        canvas.drawText("Vola niditra (Total Recettes): %,.2f %s".format(totalIncome, currency), left, y, sectionPaint)
        y += 14f
        canvas.drawText("Vola nivoaka (Total Dépenses): %,.2f %s".format(totalExpense, currency), left, y, sectionPaint)
        y += 14f
        canvas.drawText("Solde / Tahiry tavela: %,.2f %s".format(balance, currency), left, y, subtitlePaint)
        y += 16f
        canvas.drawLine(left, y, right, y, linePaint)
        y += 20f

        canvas.drawText("LISITRY NY FANDANIANA SY NY FIDIRAM-BOLA:", left, y, sectionPaint)
        y += 16f

        // Table header
        canvas.drawText("Daty", left, y, boldBodyPaint)
        canvas.drawText("Lohahevitra / Karazana", left + 80f, y, boldBodyPaint)
        canvas.drawText("Karazana", left + 300f, y, boldBodyPaint)
        canvas.drawText("Vola", right - 90f, y, boldBodyPaint)
        y += 12f
        canvas.drawLine(left, y, right, y, linePaint)
        y += 14f

        for (item in finances.take(35)) {
            if (y > 790f) break
            val itemDate = df.format(Date(item.dateMillis))
            canvas.drawText(itemDate, left, y, bodyPaint)
            val truncatedTitle = if (item.title.length > 32) item.title.take(30) + "..." else item.title
            canvas.drawText(truncatedTitle, left + 80f, y, bodyPaint)
            canvas.drawText(item.transactionType, left + 300f, y, bodyPaint)
            val prefix = if (item.transactionType == "DEPENSE") "- " else "+ "
            canvas.drawText("$prefix%,.0f".format(item.amount), right - 90f, y, boldBodyPaint)
            y += 14f
        }

        val footerPaint = Paint().apply {
            color = Color.rgb(120, 130, 145)
            textSize = 8f
            isAntiAlias = true
        }
        canvas.drawText("RINDRANDAMIN'NY MPITANDRINA — Solohery RAKOTOARISOA", left, 820f, footerPaint)

        document.finishPage(page)

        val file = File(getPdfDir(context), "Tatitra_Vola_${System.currentTimeMillis()}.pdf")
        val stream = FileOutputStream(file)
        document.writeTo(stream)
        stream.close()
        document.close()

        shareFile(context, file, "Tatitra ara-bola")
    }
}
