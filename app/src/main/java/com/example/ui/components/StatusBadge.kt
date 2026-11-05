package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusOrangeContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        "A_COMMENCER", "PREVUE", "PLANIFIE" -> Triple(
            Color(0xFFE8EEF5),
            Color(0xFF1E3A6E),
            when (status) {
                "A_COMMENCER" -> "Hatombohana"
                "PREVUE" -> "Voalahatra"
                else -> "Planifié"
            }
        )
        "EN_COURS", "EN_PREPARATION" -> Triple(
            StatusOrangeContainer,
            StatusOrange,
            "Eo an-dalam-panaovana"
        )
        "A_REVISER" -> Triple(
            Color(0xFFF3E5F5),
            Color(0xFF7B1FA2),
            "Haverina hojerena"
        )
        "TERMINEE", "CLOTUREE", "TERMINE" -> Triple(
            StatusGreenContainer,
            StatusGreen,
            "Vita"
        )
        "PRECHEE" -> Triple(
            Color(0xFFE0F2F1),
            Color(0xFF00695C),
            "Efa nitoriana"
        )
        "URGENTE" -> Triple(
            StatusRedContainer,
            StatusRed,
            "Tena maika"
        )
        "HAUTE" -> Triple(
            StatusOrangeContainer,
            StatusOrange,
            "Ambony"
        )
        "MOYENNE" -> Triple(
            Color(0xFFE8EEF5),
            Color(0xFF1E3A6E),
            "Antonony"
        )
        "BASSE" -> Triple(
            Color(0xFFF0F0F0),
            Color(0xFF616161),
            "Tsotra"
        )
        else -> Triple(
            Color(0xFFE8EEF5),
            Color(0xFF1E3A6E),
            status
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
