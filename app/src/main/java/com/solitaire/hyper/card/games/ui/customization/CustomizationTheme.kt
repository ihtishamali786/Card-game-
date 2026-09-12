package com.solitaire.hyper.card.games.ui.customization

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Background themes available for selection (Free & Premium 3D).
 */
data class BackgroundTheme(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val brush: Brush,
    val isPremium: Boolean = false,
    val coinCost: Int = 0,
    val is3D: Boolean = false
)

/**
 * Card back themes available for selection (Free & Premium 3D).
 */
data class CardBackTheme(
    val id: String,
    val name: String,
    val baseColor: Color,
    val accentColor: Color,
    val patternType: PatternType,
    val isPremium: Boolean = false,
    val coinCost: Int = 0,
    val is3D: Boolean = false
) {
    enum class PatternType {
        // The 7 Distinct User Photo Designs
        CRIMSON_ANVIL,   // Photo 3: Main Board Card (Forge Anvil, crossed swords & hearth glow)
        SILVER_DRAGON,   // Photo 1: Brushed Chrome & Coiled Dragon sunburst
        GOLDEN_HEARTS,   // Photo 2: 24K Pure Gold bullion weave & royal diamond
        VINTAGE_TAVERN,  // Photo 4: Rustic tavern woodcut Celtic knot & brass
        BAROQUE_ACES,    // Photo 5: Reformation gold & ivory illuminated rosettes
        AZTEC_MANDALA,   // Photo 6: Obsidian & scarlet sacred Aztec sunstone wheel
        MODERN_POPART,   // Photo 7: Contemporary designer neon pop-art grid

        // Standard / 3D Patterns
        SLEEK_EMERALD,
        ROYAL_CREST,
        DIAMOND_GEOMETRY,
        CLASSIC_TARTAN,
        HYPER_GRID,
        VINTAGE_FILIGREE,
        MIDNIGHT_STAR,
        CRIMSON_DRAGON,
        HOLOGRAPHIC_3D,
        GOLD_FOIL_3D,
        DRAGON_3D,
        CYBERPUNK_3D,
        CRYSTAL_3D,
        BEACH_5D,
        FLORAL_4D
    }
}

/**
 * Card face styles representing each artistic deck design.
 */
enum class CardFaceStyle {
    CRIMSON_ANVIL,   // Photo 3: Main default! Ivory canvas, ink-black & blood crimson geometric court
    SILVER_DRAGON,   // Photo 1: Metallic chrome-silver brushed finish, dark armor King & metallic pips
    GOLDEN_HEARTS,   // Photo 2: 24K gold foil textured canvas, ruby red indices & gleaming hearts
    VINTAGE_TAVERN,  // Photo 4: Rustic woodcut renaissance court, antique ochre & tavern burgundy
    BAROQUE_ACES,    // Photo 5: Reformation illuminated manuscript, gold rosettes & historic emblems
    AZTEC_MANDALA,   // Photo 6: Concentric geometric Aztec/Mayan tribal mandalas in red & black
    MODERN_POPART,   // Photo 7: Designer pop art (neon tube heart, green monster, bird dispersion)
    STANDARD         // Classic standard indices
}

/**
 * Card face themes available for selection.
 */
data class CardFaceTheme(
    val id: String,
    val name: String,
    val fontStyleName: String,
    val highContrast: Boolean = false,
    val isPremium: Boolean = false,
    val coinCost: Int = 0,
    val is3D: Boolean = false,
    val style: CardFaceStyle = CardFaceStyle.STANDARD
)

object CustomizationRegistry {

    val backgrounds = listOf(
        // Free Themes
        BackgroundTheme(
            id = "CLASSIC_FELT",
            name = "Classic Felt",
            primaryColor = Color(0xFF0C5A35),
            brush = Brush.verticalGradient(listOf(Color(0xFF146C43), Color(0xFF0B4628), Color(0xFF062A17))),
            isPremium = false,
            coinCost = 0
        ),
        BackgroundTheme(
            id = "SLEEK_EMERALD",
            name = "Sleek Emerald",
            primaryColor = Color(0xFF142D25),
            brush = Brush.verticalGradient(listOf(Color(0xFF142D25), Color(0xFF0B1411))),
            isPremium = false,
            coinCost = 0
        ),
        BackgroundTheme(
            id = "SAPPHIRE",
            name = "Sapphire Casino",
            primaryColor = Color(0xFF0F3E6D),
            brush = Brush.verticalGradient(listOf(Color(0xFF1A5A9C), Color(0xFF104173), Color(0xFF07213D))),
            isPremium = false,
            coinCost = 0
        ),
        BackgroundTheme(
            id = "OBSIDIAN",
            name = "Obsidian Dark",
            primaryColor = Color(0xFF1E2124),
            brush = Brush.verticalGradient(listOf(Color(0xFF2B2F36), Color(0xFF1C1E23), Color(0xFF0F1012))),
            isPremium = false,
            coinCost = 0
        ),
        BackgroundTheme(
            id = "MINIMAL_SLATE",
            name = "Minimal Slate",
            primaryColor = Color(0xFF2D3748),
            brush = Brush.verticalGradient(listOf(Color(0xFF3F4E64), Color(0xFF2B3545), Color(0xFF1A202C))),
            isPremium = false,
            coinCost = 0
        ),

        // Premium 3D Themes
        BackgroundTheme(
            id = "3D_COSMIC_HOLO",
            name = "3D Cosmic Hologram",
            primaryColor = Color(0xFF2E0854),
            brush = Brush.radialGradient(
                listOf(Color(0xFF8A2BE2), Color(0xFF4B0082), Color(0xFF190033), Color(0xFF0A0017))
            ),
            isPremium = true,
            coinCost = 600,
            is3D = true
        ),
        BackgroundTheme(
            id = "3D_ROYAL_GOLD",
            name = "3D Royal Gold Velvet",
            primaryColor = Color(0xFF2B1F00),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF5C4708), Color(0xFF2E2204), Color(0xFF140F02), Color(0xFF000000))
            ),
            isPremium = true,
            coinCost = 800,
            is3D = true
        ),
        BackgroundTheme(
            id = "3D_CYBER_NEON",
            name = "3D Cyber Grid Matrix",
            primaryColor = Color(0xFF051C2C),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF00E5FF), Color(0xFF005577), Color(0xFF021B27), Color(0xFF010B10))
            ),
            isPremium = true,
            coinCost = 700,
            is3D = true
        ),
        BackgroundTheme(
            id = "3D_DEEP_OCEAN",
            name = "3D Deep Ocean Emerald",
            primaryColor = Color(0xFF004D40),
            brush = Brush.radialGradient(
                listOf(Color(0xFF00BFA5), Color(0xFF00796B), Color(0xFF00332C), Color(0xFF001512))
            ),
            isPremium = true,
            coinCost = 600,
            is3D = true
        ),
        BackgroundTheme(
            id = "3D_MAGMA_ROYALE",
            name = "3D Volcanic Magma",
            primaryColor = Color(0xFF3E1109),
            brush = Brush.verticalGradient(
                listOf(Color(0xFFFF3D00), Color(0xFFBF360C), Color(0xFF4E1408), Color(0xFF1B0502))
            ),
            isPremium = true,
            coinCost = 750,
            is3D = true
        ),
        BackgroundTheme(
            id = "3D_DIAMOND_CARBON",
            name = "3D Platinum Carbon",
            primaryColor = Color(0xFF181C20),
            brush = Brush.radialGradient(
                listOf(Color(0xFF78909C), Color(0xFF37474F), Color(0xFF21272B), Color(0xFF0D0F10))
            ),
            isPremium = true,
            coinCost = 850,
            is3D = true
        ),
        // 5D / 4D Themes (Tropical Beach & Floral Paradise)
        BackgroundTheme(
            id = "5D_TROPICAL_BEACH",
            name = "5D Tropical Beach Lagoon",
            primaryColor = Color(0xFF00695C),
            brush = Brush.verticalGradient(
                listOf(Color(0xFF00E5FF), Color(0xFF00ACC1), Color(0xFF00796B), Color(0xFF004D40), Color(0xFFD7CCC8))
            ),
            isPremium = true,
            coinCost = 900,
            is3D = true
        ),
        BackgroundTheme(
            id = "4D_FLORAL_PARADISE",
            name = "4D Royal Blossom Floral",
            primaryColor = Color(0xFF880E4F),
            brush = Brush.verticalGradient(
                listOf(Color(0xFFF48FB1), Color(0xFFD81B60), Color(0xFF880E4F), Color(0xFF4A148C), Color(0xFF1A0520))
            ),
            isPremium = true,
            coinCost = 900,
            is3D = true
        )
    )

    val cardBacks = listOf(
        // ⭐ 1. PHOTO 3: Crimson Anvil Royal Forge (Main Board Default Card Back)
        CardBackTheme(
            id = "BACK_CRIMSON_ANVIL",
            name = "Crimson Anvil Forge",
            baseColor = Color(0xFF160D0C),
            accentColor = Color(0xFFB71C1C),
            patternType = CardBackTheme.PatternType.CRIMSON_ANVIL,
            isPremium = false,
            coinCost = 0
        ),
        // 2. PHOTO 1: Silver Dragon Waterproof
        CardBackTheme(
            id = "BACK_SILVER_DRAGON",
            name = "Silver Dragon Metallic",
            baseColor = Color(0xFF26282B),
            accentColor = Color(0xFFD4D4D8),
            patternType = CardBackTheme.PatternType.SILVER_DRAGON,
            isPremium = false,
            coinCost = 0
        ),
        // 3. PHOTO 2: 24K Golden Hearts Luxury
        CardBackTheme(
            id = "BACK_GOLDEN_HEARTS",
            name = "24K Golden Hearts Weave",
            baseColor = Color(0xFF2A1F02),
            accentColor = Color(0xFFFFD700),
            patternType = CardBackTheme.PatternType.GOLDEN_HEARTS,
            isPremium = false,
            coinCost = 0
        ),
        // 4. PHOTO 4: Vintage Tavern Kings
        CardBackTheme(
            id = "BACK_VINTAGE_TAVERN",
            name = "Vintage Tavern Oak",
            baseColor = Color(0xFF2B180D),
            accentColor = Color(0xFFC8963E),
            patternType = CardBackTheme.PatternType.VINTAGE_TAVERN,
            isPremium = false,
            coinCost = 0
        ),
        // 5. PHOTO 5: Reformation Baroque Aces
        CardBackTheme(
            id = "BACK_BAROQUE_ACES",
            name = "Reformation Baroque Gold",
            baseColor = Color(0xFFFBF8F2),
            accentColor = Color(0xFFB8860B),
            patternType = CardBackTheme.PatternType.BAROQUE_ACES,
            isPremium = false,
            coinCost = 0
        ),
        // 6. PHOTO 6: Aztec Tribal Mandala
        CardBackTheme(
            id = "BACK_AZTEC_MANDALA",
            name = "Aztec Sunstone Mandala",
            baseColor = Color(0xFF141414),
            accentColor = Color(0xFFD32F2F),
            patternType = CardBackTheme.PatternType.AZTEC_MANDALA,
            isPremium = false,
            coinCost = 0
        ),
        // 7. PHOTO 7: Modern Pop Art Avant-Garde
        CardBackTheme(
            id = "BACK_MODERN_POPART",
            name = "Modern Pop Art Matrix",
            baseColor = Color(0xFF0F172A),
            accentColor = Color(0xFF00E5FF),
            patternType = CardBackTheme.PatternType.MODERN_POPART,
            isPremium = false,
            coinCost = 0
        ),

        // Classic / Free Card Backs
        CardBackTheme(
            id = "ROYAL_CREST",
            name = "Royal Crest",
            baseColor = Color(0xFF1B3B6F),
            accentColor = Color(0xFFFFD700),
            patternType = CardBackTheme.PatternType.ROYAL_CREST,
            isPremium = false,
            coinCost = 0
        ),
        CardBackTheme(
            id = "SLEEK_EMERALD",
            name = "Sleek Emerald",
            baseColor = Color(0xFF1D3D33),
            accentColor = Color(0xFF34D399),
            patternType = CardBackTheme.PatternType.SLEEK_EMERALD,
            isPremium = false,
            coinCost = 0
        ),
        CardBackTheme(
            id = "DIAMOND_GEOMETRY",
            name = "Diamond Geometry",
            baseColor = Color(0xFF9E1B32),
            accentColor = Color(0xFFFFE082),
            patternType = CardBackTheme.PatternType.DIAMOND_GEOMETRY,
            isPremium = false,
            coinCost = 0
        ),
        CardBackTheme(
            id = "CLASSIC_TARTAN",
            name = "Classic Tartan",
            baseColor = Color(0xFF1E3F20),
            accentColor = Color(0xFF81C784),
            patternType = CardBackTheme.PatternType.CLASSIC_TARTAN,
            isPremium = false,
            coinCost = 0
        ),

        // Premium 3D Card Backs
        CardBackTheme(
            id = "3D_HOLOGRAPHIC_FOIL",
            name = "3D Holographic Foil",
            baseColor = Color(0xFF120E2E),
            accentColor = Color(0xFF00E5FF),
            patternType = CardBackTheme.PatternType.HOLOGRAPHIC_3D,
            isPremium = true,
            coinCost = 700,
            is3D = true
        ),
        CardBackTheme(
            id = "3D_GOLD_FILIGREE",
            name = "3D Gold Filigree 24K",
            baseColor = Color(0xFF1F1600),
            accentColor = Color(0xFFFFD700),
            patternType = CardBackTheme.PatternType.GOLD_FOIL_3D,
            isPremium = true,
            coinCost = 900,
            is3D = true
        ),
        CardBackTheme(
            id = "3D_IMPERIAL_DRAGON",
            name = "3D Imperial Dragon",
            baseColor = Color(0xFF5C0606),
            accentColor = Color(0xFFFFB300),
            patternType = CardBackTheme.PatternType.DRAGON_3D,
            isPremium = true,
            coinCost = 850,
            is3D = true
        ),
        CardBackTheme(
            id = "3D_CYBERPUNK_NEON",
            name = "3D Cyberpunk Matrix",
            baseColor = Color(0xFF090D1A),
            accentColor = Color(0xFFFF007F),
            patternType = CardBackTheme.PatternType.CYBERPUNK_3D,
            isPremium = true,
            coinCost = 750,
            is3D = true
        ),
        CardBackTheme(
            id = "3D_CRYSTAL_OBSIDIAN",
            name = "3D Crystal Obsidian",
            baseColor = Color(0xFF0F1115),
            accentColor = Color(0xFF80D8FF),
            patternType = CardBackTheme.PatternType.CRYSTAL_3D,
            isPremium = true,
            coinCost = 800,
            is3D = true
        ),
        CardBackTheme(
            id = "5D_BEACH_BREEZE",
            name = "5D Tropical Beach Breeze",
            baseColor = Color(0xFF00363A),
            accentColor = Color(0xFF00E5FF),
            patternType = CardBackTheme.PatternType.BEACH_5D,
            isPremium = true,
            coinCost = 900,
            is3D = true
        ),
        CardBackTheme(
            id = "4D_BLOSSOM_FLORAL",
            name = "4D Royal Blossom Floral",
            baseColor = Color(0xFF2E001F),
            accentColor = Color(0xFFFF4081),
            patternType = CardBackTheme.PatternType.FLORAL_4D,
            isPremium = true,
            coinCost = 900,
            is3D = true
        )
    )

    val cardFaces = listOf(
        // ⭐ 1. PHOTO 3: Crimson Anvil Royal Forge (Main Board Default Card Face)
        CardFaceTheme(
            id = "FACE_CRIMSON_ANVIL",
            name = "Crimson Anvil Forge",
            fontStyleName = "Royal Medieval Gothic",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.CRIMSON_ANVIL
        ),
        // 2. PHOTO 1: Silver Dragon Waterproof
        CardFaceTheme(
            id = "FACE_SILVER_DRAGON",
            name = "Silver Dragon Waterproof",
            fontStyleName = "Chrome Metallic Armor",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.SILVER_DRAGON
        ),
        // 3. PHOTO 2: 24K Golden Hearts Luxury
        CardFaceTheme(
            id = "FACE_GOLDEN_HEARTS",
            name = "24K Golden Hearts Luxury",
            fontStyleName = "24K Gold & Ruby Foil",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.GOLDEN_HEARTS
        ),
        // 4. PHOTO 4: Vintage Tavern Kings
        CardFaceTheme(
            id = "FACE_VINTAGE_TAVERN",
            name = "Vintage Tavern Kings",
            fontStyleName = "Woodcut Renaissance",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.VINTAGE_TAVERN
        ),
        // 5. PHOTO 5: Reformation Baroque Aces
        CardFaceTheme(
            id = "FACE_BAROQUE_ACES",
            name = "Reformation Baroque Aces",
            fontStyleName = "Illuminated Scripture",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.BAROQUE_ACES
        ),
        // 6. PHOTO 6: Aztec Tribal Mandala
        CardFaceTheme(
            id = "FACE_AZTEC_MANDALA",
            name = "Aztec Tribal Mandala",
            fontStyleName = "Sacred Aztec Mandala",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.AZTEC_MANDALA
        ),
        // 7. PHOTO 7: Modern Pop Art Avant-Garde
        CardFaceTheme(
            id = "FACE_MODERN_POPART",
            name = "Modern Pop Art Avant-Garde",
            fontStyleName = "Designer Gallery Neon",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.MODERN_POPART
        ),

        // Classic Card Faces
        CardFaceTheme(
            id = "CLASSIC",
            name = "Classic Standard",
            fontStyleName = "Standard Serif",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.STANDARD
        ),
        CardFaceTheme(
            id = "MODERN",
            name = "Modern Clean",
            fontStyleName = "Clean Sans",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.STANDARD
        ),
        CardFaceTheme(
            id = "MINIMAL",
            name = "Bold Minimal",
            fontStyleName = "Minimalist Bold",
            isPremium = false,
            coinCost = 0,
            style = CardFaceStyle.STANDARD
        ),

        // Premium 3D Card Faces
        CardFaceTheme(
            id = "3D_GOLD_LUXURY",
            name = "3D Gold Foil Luxury",
            fontStyleName = "24K Gold Indices",
            isPremium = true,
            coinCost = 750,
            is3D = true,
            style = CardFaceStyle.GOLDEN_HEARTS
        ),
        CardFaceTheme(
            id = "3D_CYBER_GLOW",
            name = "3D Cyber Glow",
            fontStyleName = "Neon Vector Indices",
            isPremium = true,
            coinCost = 700,
            is3D = true,
            style = CardFaceStyle.MODERN_POPART
        ),
        CardFaceTheme(
            id = "3D_IMPERIAL_ROYALE",
            name = "3D Imperial Royale",
            fontStyleName = "Baroque Royal Indices",
            isPremium = true,
            coinCost = 800,
            is3D = true,
            style = CardFaceStyle.BAROQUE_ACES
        ),
        CardFaceTheme(
            id = "5D_BEACH_PARADISE",
            name = "5D Beach Paradise",
            fontStyleName = "Aqua Lagoon Serifs",
            isPremium = true,
            coinCost = 900,
            is3D = true,
            style = CardFaceStyle.CRIMSON_ANVIL
        ),
        CardFaceTheme(
            id = "4D_ROYAL_BLOSSOM",
            name = "4D Royal Blossom",
            fontStyleName = "Velvet Petal Script",
            isPremium = true,
            coinCost = 900,
            is3D = true,
            style = CardFaceStyle.BAROQUE_ACES
        )
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
