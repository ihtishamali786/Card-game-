package com.solitaire.hyper.card.games.ui.customization

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Zen Background Theme.
 */
data class BackgroundTheme(
    val id: String,
    val name: String,
    val category: String,
    val primaryColor: Color,
    val brush: Brush,
    val subtitle: String
)

/**
 * Procedural Card Back Theme.
 */
enum class CardBackPattern {
    ROYAL_SAPPHIRE,
    IMPERIAL_CRIMSON,
    ZEN_EMERALD,
    MIDNIGHT_GOLD,
    SAKURA_BLOSSOM,
    CELESTIAL_STARS,
    HERITAGE_WOOD,
    JOKER_AMETHYST
}

data class CardBackTheme(
    val id: String,
    val name: String,
    val patternType: CardBackPattern,
    val baseColor: Color,
    val accentColor: Color
)

/**
 * Card Face Styles (Standard, Modern Clean, Jumbo Easy-Read).
 */
enum class CardFaceStyle {
    STANDARD,
    MODERN_CLEAN,
    JUMBO_EASY_READ
}

data class CardFaceTheme(
    val id: String,
    val name: String,
    val style: CardFaceStyle,
    val surfaceColor: Color,
    val description: String
)

object CustomizationRegistry {

    // 8 Curated Zen Backgrounds (Classic green, wood, night blue, sunset, and 4 nature scenes)
    val BACKGROUNDS: List<BackgroundTheme> = listOf(
        BackgroundTheme(
            id = "BG_CLASSIC_GREEN",
            name = "Classic Green",
            category = "Classic",
            primaryColor = Color(0xFF1B4D3E),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF1C5243), Color(0xFF13392E), Color(0xFF0C241D))
            ),
            subtitle = "Traditional felt with subtle texture"
        ),
        BackgroundTheme(
            id = "BG_WOOD",
            name = "Warm Mahogany",
            category = "Wood",
            primaryColor = Color(0xFF3E2723),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF4E342E), Color(0xFF3E2723), Color(0xFF271510))
            ),
            subtitle = "Rich polished timber surface"
        ),
        BackgroundTheme(
            id = "BG_NIGHT_BLUE",
            name = "Night Blue Zen",
            category = "Zen",
            primaryColor = Color(0xFF0F172A),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
            ),
            subtitle = "Deep calming midnight gradient"
        ),
        BackgroundTheme(
            id = "BG_SUNSET",
            name = "Golden Sunset",
            category = "Atmospheric",
            primaryColor = Color(0xFF7C2D12),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF9A3412), Color(0xFF7C2D12), Color(0xFF431407))
            ),
            subtitle = "Warm glowing amber twilight"
        ),
        BackgroundTheme(
            id = "BG_FOREST",
            name = "Tranquil Forest",
            category = "Nature",
            primaryColor = Color(0xFF14532D),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF166534), Color(0xFF14532D), Color(0xFF052E16))
            ),
            subtitle = "Lush pine forest canopy"
        ),
        BackgroundTheme(
            id = "BG_OCEAN",
            name = "Ocean Waves",
            category = "Nature",
            primaryColor = Color(0xFF0E7490),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF0891B2), Color(0xFF0E7490), Color(0xFF164E63))
            ),
            subtitle = "Soothing deep coastal aqua"
        ),
        BackgroundTheme(
            id = "BG_AUTUMN",
            name = "Autumn Leaves",
            category = "Nature",
            primaryColor = Color(0xFF92400E),
            brush = Brush.verticalGradient(
                listOf(Color(0xFFB45309), Color(0xFF92400E), Color(0xFF78350F))
            ),
            subtitle = "Warm cedar and golden maple"
        ),
        BackgroundTheme(
            id = "BG_MOUNTAIN",
            name = "Mountain Mist",
            category = "Nature",
            primaryColor = Color(0xFF334155),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF475569), Color(0xFF334155), Color(0xFF1E293B))
            ),
            subtitle = "Serene alpine morning haze"
        )
    )

    // 8 Elegant Card Backs
    val CARD_BACKS: List<CardBackTheme> = listOf(
        CardBackTheme(
            id = "BACK_ROYAL_BLUE",
            name = "Royal Sapphire",
            patternType = CardBackPattern.ROYAL_SAPPHIRE,
            baseColor = Color(0xFF1E3A8A),
            accentColor = Color(0xFF60A5FA)
        ),
        CardBackTheme(
            id = "BACK_CRIMSON",
            name = "Imperial Crimson",
            patternType = CardBackPattern.IMPERIAL_CRIMSON,
            baseColor = Color(0xFF881337),
            accentColor = Color(0xFFFB7185)
        ),
        CardBackTheme(
            id = "BACK_EMERALD_ZEN",
            name = "Zen Emerald",
            patternType = CardBackPattern.ZEN_EMERALD,
            baseColor = Color(0xFF064E3B),
            accentColor = Color(0xFF34D399)
        ),
        CardBackTheme(
            id = "BACK_MIDNIGHT_GOLD",
            name = "Midnight & Gold",
            patternType = CardBackPattern.MIDNIGHT_GOLD,
            baseColor = Color(0xFF18181B),
            accentColor = Color(0xFFFBBF24)
        ),
        CardBackTheme(
            id = "BACK_SAKURA",
            name = "Sakura Blossom",
            patternType = CardBackPattern.SAKURA_BLOSSOM,
            baseColor = Color(0xFF831843),
            accentColor = Color(0xFFF472B6)
        ),
        CardBackTheme(
            id = "BACK_CELESTIAL",
            name = "Celestial Stars",
            patternType = CardBackPattern.CELESTIAL_STARS,
            baseColor = Color(0xFF312E81),
            accentColor = Color(0xFFA5B4FC)
        ),
        CardBackTheme(
            id = "BACK_RETRO_WOOD",
            name = "Heritage Woodgrain",
            patternType = CardBackPattern.HERITAGE_WOOD,
            baseColor = Color(0xFF451A03),
            accentColor = Color(0xFFD97706)
        ),
        CardBackTheme(
            id = "BACK_JOKER_PURPLE",
            name = "Joker Amethyst",
            patternType = CardBackPattern.JOKER_AMETHYST,
            baseColor = Color(0xFF581C87),
            accentColor = Color(0xFFC084FC)
        )
    )

    // 3 Card Face Styles
    val CARD_FACES: List<CardFaceTheme> = listOf(
        CardFaceTheme(
            id = "FACE_STANDARD",
            name = "Classic Standard",
            style = CardFaceStyle.STANDARD,
            surfaceColor = Color(0xFFFFFFFF),
            description = "Real casino cards with full pips and court figures"
        ),
        CardFaceTheme(
            id = "FACE_MODERN",
            name = "Modern Clean",
            style = CardFaceStyle.MODERN_CLEAN,
            surfaceColor = Color(0xFFF8FAFC),
            description = "Real card layout with clean sans-serif numbers"
        ),
        CardFaceTheme(
            id = "FACE_JUMBO",
            name = "Jumbo Easy-Read",
            style = CardFaceStyle.JUMBO_EASY_READ,
            surfaceColor = Color(0xFFFFFFFF),
            description = "Huge corner numbers and one big center suit"
        )
    )

    fun getBackground(id: String): BackgroundTheme {
        return BACKGROUNDS.firstOrNull { it.id == id } ?: BACKGROUNDS[0]
    }

    fun getCardBack(id: String): CardBackTheme {
        return CARD_BACKS.firstOrNull { it.id == id } ?: CARD_BACKS[0]
    }

    fun getCardFace(id: String): CardFaceTheme {
        return CARD_FACES.firstOrNull { it.id == id } ?: CARD_FACES[0]
    }
}
