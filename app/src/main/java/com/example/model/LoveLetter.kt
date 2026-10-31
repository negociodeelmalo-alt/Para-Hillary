package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

data class LoveLetter(
    val id: Int,
    val title: String,
    val dateTag: String,
    val message: String,
    @DrawableRes val photoRes: Int,
    val stampIcon: String = "💘",
    val decorativePhrase: String = "Para mi amor eterno 💘"
)

object LoveLettersRepository {
    val letters = listOf(
        LoveLetter(
            id = 1,
            title = "Carta 1 💘",
            dateTag = "Nuestro comienzo",
            message = "Te amo hillary, con todo mi alma",
            photoRes = R.drawable.photo_1,
            stampIcon = "💘",
            decorativePhrase = "Desde el fondo de mi corazón 💘"
        ),
        LoveLetter(
            id = 2,
            title = "Carta 2 💘",
            dateTag = "Agradecido con la vida",
            message = "Eres lo mejor que me pudo pasar en este mundo y estoy agradecido contigo",
            photoRes = R.drawable.photo_2,
            stampIcon = "💘",
            decorativePhrase = "El regalo más hermoso del cielo 💘"
        ),
        LoveLetter(
            id = 3,
            title = "Carta 3 💘",
            dateTag = "Mi consentida",
            message = "te amo musho sabeshh mi negla",
            photoRes = R.drawable.photo_3,
            stampIcon = "💘",
            decorativePhrase = "Tú y solo tú para siempre 💘"
        ),
        LoveLetter(
            id = 4,
            title = "Carta 4 💘",
            dateTag = "Todo mi ser",
            message = "Eres mi vidaa!!",
            photoRes = R.drawable.photo_4,
            stampIcon = "💘",
            decorativePhrase = "Late mi corazón por ti 💘"
        ),
        LoveLetter(
            id = 5,
            title = "Carta 5 💘",
            dateTag = "Promesa eterna",
            message = "Quiero permanecer a tu lado siempde",
            photoRes = R.drawable.photo_5,
            stampIcon = "💘",
            decorativePhrase = "Por todos los días que Dios nos dé 💘"
        ),
        LoveLetter(
            id = 6,
            title = "Carta 6 💘",
            dateTag = "Mi refugio y paz",
            message = "Te amo y eres lo mas lindo que me pudo suceder, eres mi calma y mi paz",
            photoRes = R.drawable.photo_6,
            stampIcon = "💘",
            decorativePhrase = "Mi calma y mi universo entero 💘"
        )
    )

    // Floating romantic words in the galaxy
    val galaxyWords = listOf(
        "💘 Te amo",
        "Mi calma y mi paz 💘",
        "Mi negla hermosa 💘",
        "💘 Por siempre",
        "Amor eterno 💘",
        "Juntos tú y yo 💘",
        "💘 Mi bendición",
        "Hillary 💘",
        "Eres mi vida 💘",
        "💘 Infinito",
        "Bajo las estrellas 💘",
        "💘 Mi destino",
        "Dios con nosotros 💘",
        "Mi corazón es tuyo 💘"
    )
}
