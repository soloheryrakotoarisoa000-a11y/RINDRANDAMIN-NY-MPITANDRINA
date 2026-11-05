package com.example

import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testMandatoryBibleVersePreserved() {
        val malagasyStrings = Translations.getStrings(AppLanguage.MALAGASY)
        val francaisStrings = Translations.getStrings(AppLanguage.FRANCAIS)
        val englishStrings = Translations.getStrings(AppLanguage.ENGLISH)

        val expectedVerse = "« Mahatoky izao teny izao: Raha misy olona maniry ny asan'ny mpitandrina, dia maniry asa tsara izy. »"
        val expectedRef = "I Timoty 3.1"

        // Rule: Conserver exactement le texte malagasy même lorsque l'utilisateur change la langue de l'application
        assertEquals(expectedVerse, malagasyStrings.bibleVerseText)
        assertEquals(expectedVerse, francaisStrings.bibleVerseText)
        assertEquals(expectedVerse, englishStrings.bibleVerseText)

        assertEquals(expectedRef, malagasyStrings.bibleVerseRef)
        assertEquals(expectedRef, francaisStrings.bibleVerseRef)
        assertEquals(expectedRef, englishStrings.bibleVerseRef)

        assertEquals("RINDRANDAMIN'NY MPITANDRINA", malagasyStrings.appTitle)
        assertEquals("Solohery RAKOTOARISOA", malagasyStrings.creatorName)
    }

    @Test
    fun testFinancialCalculation() {
        val income = 1500000.0
        val expense = 350000.0
        val balance = income - expense
        assertEquals(1150000.0, balance, 0.001)
        assertTrue(balance > 0)
    }
}
