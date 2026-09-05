package com.solitaire.hyper.card.games.ui.customization

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Background themes available for selection.
 */
data class BackgroundTheme(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val brush: Brush
)

/**
 * Card back themes available for selection.
 */
data class CardBackTheme(
    val id: String,
    val name: String,
    val baseColor: Color,
    val accentColor: Color,
    val patternType: PatternType
) {
    enum class PatternType {
        SLEEK_EMERALD,
        ROYAL_CREST,
        DIAMOND_GEOMETRY,
        CLASSIC_TARTAN,
        HYPER_GRID,
        VINTAGE_FILIGREE,
        MIDNIGHT_STAR,
        CRIMSON_DRAGON
    }
}

/**
 * Card face styles.
 */
data class CardFaceTheme(
    val id: String,
    val name: String,
    val fontStyleName: String,
    val highContrast: Boolean = false
)

object CustomizationRegistry {

    val backgrounds = listOf(
        BackgroundTheme(
            id = "SLEEK_EMERALD",
            name = "Sleek Interface",
            primaryColor = Color(0xFF142D25),
            brush = Brush.verticalGradient(listOf(Color(0xFF142D25), Color(0xFF0B1411)))
        ),
        BackgroundTheme(
            id = "CLASSIC_FELT",
            name = "Classic Felt",
            primaryColor = Color(0xFF0C5A35),
            brush = Brush.verticalGradient(listOf(Color(0xFF146C43), Color(0xFF0B4628), Color(0xFF062A17)))
        ),
        BackgroundTheme(
            id = "EMERALD",
            name = "Emerald Royale",
            primaryColor = Color(0xFF007953),
            brush = Brush.radialGradient(listOf(Color(0xFF009B6A), Color(0xFF006847), Color(0xFF003824)))
        ),
        BackgroundTheme(
            id = "MIDNIGHT",
            name = "Midnight Velvet",
            primaryColor = Color(0xFF0B332A),
            brush = Brush.verticalGradient(listOf(Color(0xFF134E41), Color(0xFF0C352C), Color(0xFF051713)))
        ),
        BackgroundTheme(
            id = "SAPPHIRE",
            name = "Sapphire Casino",
            primaryColor = Color(0xFF0F3E6D),
            brush = Brush.verticalGradient(listOf(Color(0xFF1A5A9C), Color(0xFF104173), Color(0xFF07213D)))
        ),
        BackgroundTheme(
            id = "OBSIDIAN",
            name = "Obsidian Dark",
            primaryColor = Color(0xFF1E2124),
            brush = Brush.verticalGradient(listOf(Color(0xFF2B2F36), Color(0xFF1C1E23), Color(0xFF0F1012)))
        ),
        BackgroundTheme(
            id = "OCEAN",
            name = "Ocean Breeze",
            primaryColor = Color(0xFF026873),
            brush = Brush.verticalGradient(listOf(Color(0xFF048797), Color(0xFF025761), Color(0xFF012C31)))
        ),
        BackgroundTheme(
            id = "FOREST",
            name = "Nordic Forest",
            primaryColor = Color(0xFF1C442A),
            brush = Brush.verticalGradient(listOf(Color(0xFF265B39), Color(0xFF1A3F27), Color(0xFF0D2114)))
        ),
        BackgroundTheme(
            id = "SUNSET",
            name = "Sunset Amber",
            primaryColor = Color(0xFF5E271F),
            brush = Brush.verticalGradient(listOf(Color(0xFF7A3429), Color(0xFF54221A), Color(0xFF2C100C)))
        ),
        BackgroundTheme(
            id = "HYPER_CYBER",
            name = "Hyper Cyber",
            primaryColor = Color(0xFF231145),
            brush = Brush.verticalGradient(listOf(Color(0xFF3B1C75), Color(0xFF210F42), Color(0xFF0F061F)))
        ),
        BackgroundTheme(
            id = "MINIMAL_SLATE",
            name = "Minimal Slate",
            primaryColor = Color(0xFF2D3748),
            brush = Brush.verticalGradient(listOf(Color(0xFF3F4E64), Color(0xFF2B3545), Color(0xFF1A202C)))
        )
    )

    val cardBacks = listOf(
        CardBackTheme(
            id = "SLEEK_EMERALD",
            name = "Sleek Emerald",
            baseColor = Color(0xFF1D3D33),
            accentColor = Color(0xFF34D399),
            patternType = CardBackTheme.PatternType.SLEEK_EMERALD
        ),
        CardBackTheme(
            id = "ROYAL_CREST",
            name = "Royal Crest",
            baseColor = Color(0xFF1B3B6F),
            accentColor = Color(0xFFFFD700),
            patternType = CardBackTheme.PatternType.ROYAL_CREST
        ),
        CardBackTheme(
            id = "DIAMOND_GEOMETRY",
            name = "Diamond Geometry",
            baseColor = Color(0xFF9E1B32),
            accentColor = Color(0xFFFFE082),
            patternType = CardBackTheme.PatternType.DIAMOND_GEOMETRY
        ),
        CardBackTheme(
            id = "CLASSIC_TARTAN",
            name = "Classic Tartan",
            baseColor = Color(0xFF1E3F20),
            accentColor = Color(0xFF81C784),
            patternType = CardBackTheme.PatternType.CLASSIC_TARTAN
        ),
        CardBackTheme(
            id = "HYPER_GRID",
            name = "Hyper Grid",
            baseColor = Color(0xFF120E2E),
            accentColor = Color(0xFF00E5FF),
            patternType = CardBackTheme.PatternType.HYPER_GRID
        ),
        CardBackTheme(
            id = "VINTAGE_FILIGREE",
            name = "Vintage Filigree",
            baseColor = Color(0xFF4A154B),
            accentColor = Color(0xFFF8BBD0),
            patternType = CardBackTheme.PatternType.VINTAGE_FILIGREE
        ),
        CardBackTheme(
            id = "MIDNIGHT_STAR",
            name = "Midnight Star",
            baseColor = Color(0xFF0A192F),
            accentColor = Color(0xFF64FFDA),
            patternType = CardBackTheme.PatternType.MIDNIGHT_STAR
        ),
        CardBackTheme(
            id = "CRIMSON_DRAGON",
            name = "Crimson Dragon",
            baseColor = Color(0xFF7F0000),
            accentColor = Color(0xFFFFC107),
            patternType = CardBackTheme.PatternType.CRIMSON_DRAGON
        )
    )

    val cardFaces = listOf(
        CardFaceTheme("CLASSIC", "Classic Standard", "Standard Serif"),
        CardFaceTheme("MODERN", "Modern Clean", "Clean Sans"),
        CardFaceTheme("MINIMAL", "Bold Minimal", "Minimalist Bold"),
        CardFaceTheme("VINTAGE", "Vintage Renaissance", "Ornate Antique"),
        CardFaceTheme("ELEGANT", "Elegant Serif", "High Contrast Luxury"),
        CardFaceTheme("HIGH_CONTRAST", "High Contrast Dark", "High Visibility", highContrast = true)
    )

    fun getBackground(id: String): BackgroundTheme {
        return backgrounds.firstOrNull { it.id == id } ?: backgrounds.first()
    }

    fun getCardBack(id: String): CardBackTheme {
        return cardBacks.firstOrNull { it.id == id } ?: cardBacks.first()
    }

    fun getCardFace(id: String): CardFaceTheme {
        return cardFaces.firstOrNull { it.id == id } ?: cardFaces.first()
    }
}
