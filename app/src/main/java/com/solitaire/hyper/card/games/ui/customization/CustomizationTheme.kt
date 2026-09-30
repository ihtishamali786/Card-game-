package com.solitaire.hyper.card.games.ui.customization

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Category classification for the 56 Table Themes.
 */
enum class TableCategory(val displayName: String) {
    CITIES("Cities (14)"),
    COUNTRIES("Countries & Landscapes (14)"),
    NATURE("Nature & Wilderness (10)"),
    SPACE_FANTASY("Space & Fantasy (6)"),
    ABSTRACT_TEXTURES("Abstract & Textures (8)"),
    CLASSIC_FELTS("Classic Solid Felts (4)")
}

/**
 * Full-screen Background Theme.
 */
data class BackgroundTheme(
    val id: String,
    val name: String,
    val category: TableCategory,
    val primaryColor: Color,
    val brush: Brush,
    val subtitle: String,
    val isPremium: Boolean = false,
    val coinCost: Int = 0
)

/**
 * Patterns for Card Backs (25 distinct procedural designs).
 */
enum class CardBackPattern {
    GEOMETRIC_DIAMONDS,
    MANDALA,
    ARABESQUE,
    LATTICE_WEAVE,
    GRADIENT_BLUE,
    CITY_SKYLINE,
    FLOWER_BLOOM,
    DRAGON,
    GALAXY_SWIRL,
    DARK_WOOD,
    CARBON_FIBRE,
    LEATHER_STITCH,
    CIRCUIT_BOARD,
    OCEAN_WAVES,
    MOUNTAIN_SUNRISE,
    SUNSET_PALMS,
    PAISLEY,
    TRIBAL_PATTERN,
    ORNATE_CREST,
    CHECKERBOARD,
    STARRY_NIGHT,
    MINIMALIST_MONOGRAM,
    ANIMAL_SILHOUETTE,
    VINTAGE_POSTAGE,
    HOLOGRAPHIC_SHIMMER,
    CUSTOM_PHOTO
}

data class CardBackTheme(
    val id: String,
    val name: String,
    val patternType: CardBackPattern,
    val baseColor: Color,
    val accentColor: Color,
    val isPremium: Boolean = false,
    val coinCost: Int = 0
)

/**
 * 25 Card Face styles.
 */
enum class CardFaceStyle {
    CLASSIC_STANDARD,
    MODERN_MINIMAL,
    LARGE_INDEX,
    FOUR_COLOUR_CLEAR,
    VINTAGE_IVORY,
    ROYAL_GOLD,
    NEON_GLOW,
    DARK_OBSIDIAN,
    FROSTED_GLASS,
    PAPER_CRAFT,
    WATERCOLOUR,
    PIXEL_RETRO,
    COMIC_POP,
    CARVED_WOOD,
    WHITE_MARBLE,
    CHROME_METALLIC,
    CYBERPUNK,
    OCEAN_BLUE,
    FLORAL_GARDEN,
    GALAXY,
    LINE_ART,
    CASINO_CLASSIC,
    ARABESQUE,
    MUGHAL_MINIATURE,
    ART_DECO
}

data class CardFaceTheme(
    val id: String,
    val name: String,
    val style: CardFaceStyle,
    val surfaceColor: Color,
    val isDarkSurface: Boolean = false,
    val description: String,
    val isPremium: Boolean = false,
    val coinCost: Int = 0
)

/**
 * 12 Suit Icon Styles.
 */
enum class SuitIconStyle {
    CLASSIC_FILLED,
    OUTLINE,
    ROUNDED_SOFT,
    FLAT_GEOMETRIC,
    THREE_D_EMBOSSED,
    GOLD_FOIL,
    NEON_LINE,
    HAND_DRAWN,
    PIXEL,
    GRADIENT,
    GLASS,
    ORNATE
}

data class SuitStyleTheme(
    val id: String,
    val name: String,
    val style: SuitIconStyle,
    val description: String
)

/**
 * 4 Suit Color Schemes.
 */
data class SuitColorScheme(
    val id: String,
    val name: String,
    val heartsColor: Color,
    val diamondsColor: Color,
    val clubsColor: Color,
    val spadesColor: Color,
    val description: String
)

/**
 * Preset Combination of styles for quick 1-tap immersion.
 */
data class PresetCombo(
    val id: String,
    val name: String,
    val description: String,
    val cardFaceId: String,
    val suitStyleId: String,
    val suitSchemeId: String,
    val cardBackId: String,
    val backgroundId: String
)

object CustomizationRegistry {

    // =========================================================================
    // 1. THE 25 CARD FACE STYLES
    // =========================================================================
    val cardFaces = listOf(
        CardFaceTheme("FACE_CLASSIC_STANDARD", "Classic Standard", CardFaceStyle.CLASSIC_STANDARD, Color(0xFFFFFFFF), false, "Traditional crisp white cards with iconic standard typography"),
        CardFaceTheme("FACE_MODERN_MINIMAL", "Modern Minimal", CardFaceStyle.MODERN_MINIMAL, Color(0xFFF8FAFC), false, "Clean sans-serif indices with sleek modern geometry"),
        CardFaceTheme("FACE_LARGE_INDEX", "Large Index (Senior-Friendly)", CardFaceStyle.LARGE_INDEX, Color(0xFFFFFFFF), false, "Extra large indices for effortless readability on all phones"),
        CardFaceTheme("FACE_FOUR_COLOUR_CLEAR", "Four-Colour Clear", CardFaceStyle.FOUR_COLOUR_CLEAR, Color(0xFFFAFAFA), false, "High distinction card faces optimized for zero-mistake play"),
        CardFaceTheme("FACE_VINTAGE_IVORY", "Vintage Ivory", CardFaceStyle.VINTAGE_IVORY, Color(0xFFFFFBEB), false, "Warm antique parchment with aged renaissance feel"),
        CardFaceTheme("FACE_ROYAL_GOLD", "Royal Gold", CardFaceStyle.ROYAL_GOLD, Color(0xFFFFFDF5), false, "24K gilded borders and royal illuminated typography"),
        CardFaceTheme("FACE_NEON_GLOW", "Neon Glow", CardFaceStyle.NEON_GLOW, Color(0xFF0F172A), true, "Electric luminous pips on an ultra-dark background"),
        CardFaceTheme("FACE_DARK_OBSIDIAN", "Dark Obsidian", CardFaceStyle.DARK_OBSIDIAN, Color(0xFF0A0F1D), true, "Stealth matte black finish with gleaming high-contrast accents"),
        CardFaceTheme("FACE_FROSTED_GLASS", "Frosted Glass", CardFaceStyle.FROSTED_GLASS, Color(0xFFF1F5F9), false, "Translucent glassy textures with crisp frosted edges"),
        CardFaceTheme("FACE_PAPER_CRAFT", "Paper Craft", CardFaceStyle.PAPER_CRAFT, Color(0xFFFDFBF7), false, "Handmade textured paper with subtle debossed relief"),
        CardFaceTheme("FACE_WATERCOLOUR", "Watercolour", CardFaceStyle.WATERCOLOUR, Color(0xFFFAF5FF), false, "Artistic washed pigments with painterly elegance"),
        CardFaceTheme("FACE_PIXEL_RETRO", "Pixel Retro", CardFaceStyle.PIXEL_RETRO, Color(0xFFF0FDF4), false, "8-bit nostalgic arcade numerals and pixelated court art"),
        CardFaceTheme("FACE_COMIC_POP", "Comic Pop", CardFaceStyle.COMIC_POP, Color(0xFFFFF1F2), false, "Dynamic halftone dots and bold graphic novel borders"),
        CardFaceTheme("FACE_CARVED_WOOD", "Carved Wood", CardFaceStyle.CARVED_WOOD, Color(0xFFF5EBE1), false, "Fine grain beechwood tones with etched artisan numerals"),
        CardFaceTheme("FACE_WHITE_MARBLE", "White Marble", CardFaceStyle.WHITE_MARBLE, Color(0xFFF8FAFC), false, "Polished Carrara marble with subtle grey veining"),
        CardFaceTheme("FACE_CHROME_METALLIC", "Chrome Metallic", CardFaceStyle.CHROME_METALLIC, Color(0xFFE2E8F0), false, "Brushed titanium finish with specular metallic luster"),
        CardFaceTheme("FACE_CYBERPUNK", "Cyberpunk", CardFaceStyle.CYBERPUNK, Color(0xFF030712), true, "Cyber grid matrix with magenta & cyan laser aesthetics"),
        CardFaceTheme("FACE_OCEAN_BLUE", "Ocean Blue", CardFaceStyle.OCEAN_BLUE, Color(0xFFF0F9FF), false, "Crisp marine atmosphere with aquatic azure highlights"),
        CardFaceTheme("FACE_FLORAL_GARDEN", "Floral Garden", CardFaceStyle.FLORAL_GARDEN, Color(0xFFFFF7ED), false, "Delicate botanical accents surrounding classical indices"),
        CardFaceTheme("FACE_GALAXY", "Galaxy", CardFaceStyle.GALAXY, Color(0xFF090D16), true, "Deep cosmos nebula with stellar starlight highlights"),
        CardFaceTheme("FACE_LINE_ART", "Line-Art Sketch", CardFaceStyle.LINE_ART, Color(0xFFFFFFFF), false, "Architectural fine-liner illustrations and minimalist purity"),
        CardFaceTheme("FACE_CASINO_CLASSIC", "Casino Classic", CardFaceStyle.CASINO_CLASSIC, Color(0xFFFFFFFF), false, "Authentic Las Vegas high-roller tournament cards"),
        CardFaceTheme("FACE_ARABESQUE", "Arabesque Geometric", CardFaceStyle.ARABESQUE, Color(0xFFFDFCF7), false, "Intricate geometric Islamic art lattice borders (decorative)"),
        CardFaceTheme("FACE_MUGHAL_MINIATURE", "Mughal Miniature", CardFaceStyle.MUGHAL_MINIATURE, Color(0xFFFFFBEB), false, "Court cards styled after classical South Asian miniature paintings"),
        CardFaceTheme("FACE_ART_DECO", "Art Deco", CardFaceStyle.ART_DECO, Color(0xFF0F172A), true, "1920s Great Gatsby golden geometry on black velvet")
    )

    // =========================================================================
    // 2. THE 12 SUIT ICON STYLES
    // =========================================================================
    val suitStyles = listOf(
        SuitStyleTheme("SUIT_CLASSIC_FILLED", "Classic Filled", SuitIconStyle.CLASSIC_FILLED, "Solid timeless silhouette standard pips"),
        SuitStyleTheme("SUIT_OUTLINE", "Outline", SuitIconStyle.OUTLINE, "Modern wireframe outline with hollow center"),
        SuitStyleTheme("SUIT_ROUNDED_SOFT", "Rounded / Soft", SuitIconStyle.ROUNDED_SOFT, "Friendly rounded corners and soft geometry"),
        SuitStyleTheme("SUIT_FLAT_GEOMETRIC", "Flat Geometric", SuitIconStyle.FLAT_GEOMETRIC, "Sharp mathematical polygon angles"),
        SuitStyleTheme("SUIT_3D_EMBOSSED", "3D Embossed", SuitIconStyle.THREE_D_EMBOSSED, "Tactile bevelled shadows and raised depth"),
        SuitStyleTheme("SUIT_GOLD_FOIL", "Gold Foil", SuitIconStyle.GOLD_FOIL, "Lustrous gold gradient specular reflections"),
        SuitStyleTheme("SUIT_NEON_LINE", "Neon Line", SuitIconStyle.NEON_LINE, "High-voltage glowing laser contour"),
        SuitStyleTheme("SUIT_HAND_DRAWN", "Hand-Drawn", SuitIconStyle.HAND_DRAWN, "Artisan sketched strokes with organic charm"),
        SuitStyleTheme("SUIT_PIXEL", "Pixel", SuitIconStyle.PIXEL, "Retro 8-bit stepped pixel corners"),
        SuitStyleTheme("SUIT_GRADIENT", "Gradient", SuitIconStyle.GRADIENT, "Smooth multi-tone sunset & violet gradient fill"),
        SuitStyleTheme("SUIT_GLASS", "Glass", SuitIconStyle.GLASS, "Glossy translucent highlights and refractive aura"),
        SuitStyleTheme("SUIT_ORNATE", "Ornate", SuitIconStyle.ORNATE, "Baroque filigree flourishes and decorative curls")
    )

    // =========================================================================
    // 3. THE 4 SUIT COLOUR SCHEMES
    // =========================================================================
    val suitColorSchemes = listOf(
        SuitColorScheme(
            "SCHEME_STANDARD",
            "Standard (Red & Black)",
            heartsColor = Color(0xFFDC2626),
            diamondsColor = Color(0xFFDC2626),
            clubsColor = Color(0xFF1E293B),
            spadesColor = Color(0xFF0F172A),
            description = "Traditional 2-colour deck: Hearts and Diamonds Red, Spades and Clubs Black."
        ),
        SuitColorScheme(
            "SCHEME_FOUR_COLOUR",
            "Four-Colour Clear (Tournament)",
            heartsColor = Color(0xFFDC2626), // Red
            diamondsColor = Color(0xFF2563EB), // Blue
            clubsColor = Color(0xFF059669),    // Green
            spadesColor = Color(0xFF0F172A),   // Black
            description = "Tournament favourite: Hearts Red, Diamonds Blue, Clubs Green, Spades Black."
        ),
        SuitColorScheme(
            "SCHEME_HIGH_CONTRAST",
            "High Contrast (Accessible)",
            heartsColor = Color(0xFFB91C1C),
            diamondsColor = Color(0xFF1D4ED8),
            clubsColor = Color(0xFF047857),
            spadesColor = Color(0xFF000000),
            description = "Maximum saturation & thick contours for instant distinction on any screen."
        ),
        SuitColorScheme(
            "SCHEME_CUSTOM_JEWEL",
            "Custom Jewel Tones",
            heartsColor = Color(0xFFE11D48), // Ruby
            diamondsColor = Color(0xFF0284C7), // Sapphire
            clubsColor = Color(0xFF10B981), // Emerald
            spadesColor = Color(0xFF7C3AED), // Amethyst
            description = "Rich gemstone palette for luxury aesthetic."
        )
    )

    // =========================================================================
    // 4. THE 25 CARD BACK DESIGNS + MY PHOTO
    // =========================================================================
    val cardBacks = listOf(
        CardBackTheme("BACK_GEOMETRIC_DIAMONDS", "Geometric Diamonds", CardBackPattern.GEOMETRIC_DIAMONDS, Color(0xFF1E3A8A), Color(0xFF60A5FA)),
        CardBackTheme("BACK_MANDALA", "Mandala", CardBackPattern.MANDALA, Color(0xFF831843), Color(0xFFF472B6)),
        CardBackTheme("BACK_ARABESQUE", "Arabesque Pattern", CardBackPattern.ARABESQUE, Color(0xFF14532D), Color(0xFF86EFAC)),
        CardBackTheme("BACK_LATTICE_WEAVE", "Lattice Weave", CardBackPattern.LATTICE_WEAVE, Color(0xFF312E81), Color(0xFFA5B4FC)),
        CardBackTheme("BACK_GRADIENT_BLUE", "Gradient Blue", CardBackPattern.GRADIENT_BLUE, Color(0xFF0369A1), Color(0xFF38BDF8)),
        CardBackTheme("BACK_CITY_SKYLINE", "City Skyline", CardBackPattern.CITY_SKYLINE, Color(0xFF0F172A), Color(0xFFFBBF24)),
        CardBackTheme("BACK_FLOWER_BLOOM", "Flower Bloom", CardBackPattern.FLOWER_BLOOM, Color(0xFF9D174D), Color(0xFFFBCFE8)),
        CardBackTheme("BACK_DRAGON", "Dragon Sunburst", CardBackPattern.DRAGON, Color(0xFF7F1D1D), Color(0xFFFDE047)),
        CardBackTheme("BACK_GALAXY_SWIRL", "Galaxy Swirl", CardBackPattern.GALAXY_SWIRL, Color(0xFF3B0764), Color(0xFFC084FC)),
        CardBackTheme("BACK_DARK_WOOD", "Dark Wood Grain", CardBackPattern.DARK_WOOD, Color(0xFF451A03), Color(0xFFD97706)),
        CardBackTheme("BACK_CARBON_FIBRE", "Carbon Fibre", CardBackPattern.CARBON_FIBRE, Color(0xFF18181B), Color(0xFF71717A)),
        CardBackTheme("BACK_LEATHER_STITCH", "Leather with Stitching", CardBackPattern.LEATHER_STITCH, Color(0xFF78350F), Color(0xFFFCD34D)),
        CardBackTheme("BACK_CIRCUIT_BOARD", "Circuit Board", CardBackPattern.CIRCUIT_BOARD, Color(0xFF064E3B), Color(0xFF34D399)),
        CardBackTheme("BACK_OCEAN_WAVES", "Ocean Waves", CardBackPattern.OCEAN_WAVES, Color(0xFF0C4A6E), Color(0xFF7DD3FC)),
        CardBackTheme("BACK_MOUNTAIN_SUNRISE", "Mountain Sunrise", CardBackPattern.MOUNTAIN_SUNRISE, Color(0xFF1E293B), Color(0xFFFB923C)),
        CardBackTheme("BACK_SUNSET_PALMS", "Sunset Palms", CardBackPattern.SUNSET_PALMS, Color(0xFF881337), Color(0xFFFDE047)),
        CardBackTheme("BACK_PAISLEY", "Paisley Royal", CardBackPattern.PAISLEY, Color(0xFF4C1D95), Color(0xFFDDD6FE)),
        CardBackTheme("BACK_TRIBAL_PATTERN", "Tribal Pattern", CardBackPattern.TRIBAL_PATTERN, Color(0xFF7C2D12), Color(0xFFFED7AA)),
        CardBackTheme("BACK_ORNATE_CREST", "Ornate Royal Crest", CardBackPattern.ORNATE_CREST, Color(0xFF1E1B4B), Color(0xFFFACC15)),
        CardBackTheme("BACK_CHECKERBOARD", "Checkerboard", CardBackPattern.CHECKERBOARD, Color(0xFF111827), Color(0xFFE5E7EB)),
        CardBackTheme("BACK_STARRY_NIGHT", "Starry Night", CardBackPattern.STARRY_NIGHT, Color(0xFF0F172A), Color(0xFFE2E8F0)),
        CardBackTheme("BACK_MINIMALIST_MONOGRAM", "Minimalist Monogram", CardBackPattern.MINIMALIST_MONOGRAM, Color(0xFF0284C7), Color(0xFFBAE6FD)),
        CardBackTheme("BACK_ANIMAL_SILHOUETTE", "Animal Silhouette (Lion/Eagle)", CardBackPattern.ANIMAL_SILHOUETTE, Color(0xFF1C1917), Color(0xFFE7E5E4)),
        CardBackTheme("BACK_VINTAGE_POSTAGE", "Vintage Postage Stamp", CardBackPattern.VINTAGE_POSTAGE, Color(0xFF713F12), Color(0xFFFEF08A)),
        CardBackTheme("BACK_HOLOGRAPHIC_SHIMMER", "Holographic Shimmer", CardBackPattern.HOLOGRAPHIC_SHIMMER, Color(0xFF2E1065), Color(0xFFF43F5E)),
        // Custom photo card back
        CardBackTheme("BACK_CUSTOM_PHOTO", "My Custom Photo", CardBackPattern.CUSTOM_PHOTO, Color(0xFF1E293B), Color(0xFF38BDF8))
    )

    // =========================================================================
    // 5. THE 56 TABLE THEMES (All Categories)
    // =========================================================================
    val backgrounds = listOf(
        // --- 1. CITIES (14) ---
        BackgroundTheme("CITY_PARIS", "Paris Skyline", TableCategory.CITIES, Color(0xFF1E1B4B), Brush.verticalGradient(listOf(Color(0xFF312E81), Color(0xFF1E1B4B), Color(0xFF0F172A))), "Eiffel Tower twilight and illuminated Seine bridges"),
        BackgroundTheme("CITY_DUBAI", "Dubai Skyline", TableCategory.CITIES, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0F172A), Color(0xFF78350F))), "Burj Khalifa spire gleaming above desert metropolis"),
        BackgroundTheme("CITY_ISTANBUL", "Istanbul Bosphorus", TableCategory.CITIES, Color(0xFF134E4A), Brush.verticalGradient(listOf(Color(0xFF0D9488), Color(0xFF115E59), Color(0xFF042F2E))), "Historic minarets and glistening straits at dusk"),
        BackgroundTheme("CITY_TOKYO", "Tokyo at Night", TableCategory.CITIES, Color(0xFF0F172A), Brush.verticalGradient(listOf(Color(0xFF701A75), Color(0xFF1E1B4B), Color(0xFF020617))), "Neon-drenched Shibuya crossings and electric billboards"),
        BackgroundTheme("CITY_NEW_YORK", "New York Skyline", TableCategory.CITIES, Color(0xFF090D16), Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), Color(0xFF020617))), "Manhattan skyscrapers and yellow cab reflections"),
        BackgroundTheme("CITY_LONDON", "London Tower Bridge", TableCategory.CITIES, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))), "Thames mist framing the illuminated gothic twin towers"),
        BackgroundTheme("CITY_ROME", "Rome Old Town", TableCategory.CITIES, Color(0xFF78350F), Brush.verticalGradient(listOf(Color(0xFF9A3412), Color(0xFF78350F), Color(0xFF451A03))), "Ancient cobblestone alleyways and warm Colosseum amber"),
        BackgroundTheme("CITY_VENICE", "Venice Canals", TableCategory.CITIES, Color(0xFF0C4A6E), Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF075985), Color(0xFF082F49))), "Gondolas gliding along grand turquoise waterways"),
        BackgroundTheme("CITY_SINGAPORE", "Singapore Marina Bay", TableCategory.CITIES, Color(0xFF14532D), Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF047857), Color(0xFF022C22))), "Supertrees of the Bay and futuristic waterfront architecture"),
        BackgroundTheme("CITY_SYDNEY", "Sydney Harbour", TableCategory.CITIES, Color(0xFF075985), Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF0C4A6E))), "White sails of the Opera House on deep Pacific blue"),
        BackgroundTheme("CITY_CAIRO", "Cairo & Pyramids", TableCategory.CITIES, Color(0xFF713F12), Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF78350F), Color(0xFF451A03))), "Timeless Great Giza Pyramids under golden desert sunset"),
        BackgroundTheme("CITY_LAHORE", "Lahore Fort Area", TableCategory.CITIES, Color(0xFF881337), Brush.verticalGradient(listOf(Color(0xFF9F1239), Color(0xFF4C0519), Color(0xFF1C1917))), "Imperial Mughal arches, sheesh mahal and Badshahi red sandstone"),
        BackgroundTheme("CITY_JAIPUR", "Jaipur Pink City", TableCategory.CITIES, Color(0xFF9F1239), Brush.verticalGradient(listOf(Color(0xFFBE123C), Color(0xFF881337), Color(0xFF4C0519))), "Hawa Mahal facade in iconic terracotta terracotta rose"),
        BackgroundTheme("CITY_HONG_KONG", "Hong Kong Harbour", TableCategory.CITIES, Color(0xFF1E1B4B), Brush.verticalGradient(listOf(Color(0xFF4338CA), Color(0xFF1E1B4B), Color(0xFF020617))), "Victoria Peak panorama over laser symphony across the bay"),

        // --- 2. COUNTRIES & LANDSCAPES (14) ---
        BackgroundTheme("LAND_HUNZA", "Hunza Valley (Pakistan)", TableCategory.COUNTRIES, Color(0xFF0F766E), Brush.verticalGradient(listOf(Color(0xFF14B8A6), Color(0xFF0F766E), Color(0xFF134E4A))), "Snow-capped Rakaposhi peak towering over terraced apricot orchards"),
        BackgroundTheme("LAND_RAJASTHAN", "Rajasthan Desert (India)", TableCategory.COUNTRIES, Color(0xFFB45309), Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFF78350F))), "Endless wind-sculpted golden dunes under warm sunset skies"),
        BackgroundTheme("LAND_JAPAN_CHERRY", "Japan Cherry Blossom", TableCategory.COUNTRIES, Color(0xFF831843), Brush.verticalGradient(listOf(Color(0xFFDB2777), Color(0xFF9D174D), Color(0xFF4C0519))), "Mount Fuji mirrored in quiet lakes under blooming sakura blossoms"),
        BackgroundTheme("LAND_SWISS_ALPS", "Swiss Alps", TableCategory.COUNTRIES, Color(0xFF1E3A8A), Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8), Color(0xFF1E3A8A))), "Pristine glaciated peaks, alpine meadows and crystal air"),
        BackgroundTheme("LAND_NORWAY_FJORD", "Norway Fjord", TableCategory.COUNTRIES, Color(0xFF064E3B), Brush.verticalGradient(listOf(Color(0xFF0D9488), Color(0xFF064E3B), Color(0xFF022C22))), "Towering granite cliffs plunging into emerald mirror waters"),
        BackgroundTheme("LAND_ICELAND_AURORA", "Iceland Northern Lights", TableCategory.COUNTRIES, Color(0xFF064E3B), Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF0F172A), Color(0xFF020617))), "Vibrant celestial aurora dancing over black volcanic sand"),
        BackgroundTheme("LAND_MALDIVES", "Maldives Water Villas", TableCategory.COUNTRIES, Color(0xFF0284C7), Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0369A1))), "Overwater luxury bungalows above crystal turquoise atolls"),
        BackgroundTheme("LAND_CAPPADOCIA", "Cappadocia Balloons", TableCategory.COUNTRIES, Color(0xFF7C2D12), Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFFC2410C), Color(0xFF431407))), "Hundred colourful hot-air balloons rising above fairy chimneys"),
        BackgroundTheme("LAND_SANTORINI", "Santorini (Greece)", TableCategory.COUNTRIES, Color(0xFF1D4ED8), Brush.verticalGradient(listOf(Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF0F172A))), "Whitewashed cliffside villas and iconic sapphire church domes"),
        BackgroundTheme("LAND_SCOTLAND", "Scottish Highlands", TableCategory.COUNTRIES, Color(0xFF14532D), Brush.verticalGradient(listOf(Color(0xFF15803D), Color(0xFF166534), Color(0xFF14532D))), "Misty heather moorlands, ancient castles and quiet lochs"),
        BackgroundTheme("LAND_CANADA_LAKE", "Canadian Mountain Lake", TableCategory.COUNTRIES, Color(0xFF0E7490), Brush.verticalGradient(listOf(Color(0xFF06B6D4), Color(0xFF0891B2), Color(0xFF164E63))), "Moraine Lake glacial blues embraced by the rugged Rockies"),
        BackgroundTheme("LAND_AUSTRALIA", "Australian Outback", TableCategory.COUNTRIES, Color(0xFF9A3412), Brush.verticalGradient(listOf(Color(0xFFC2410C), Color(0xFF9A3412), Color(0xFF431407))), "Vibrant red ochre earth and ancient monoliths under southern stars"),
        BackgroundTheme("LAND_RIO", "Rio de Janeiro", TableCategory.COUNTRIES, Color(0xFF047857), Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF047857), Color(0xFF064E3B))), "Sugarloaf mountain and lush rainforest meeting the ocean"),
        BackgroundTheme("LAND_MOROCCO", "Moroccan Blue Streets", TableCategory.COUNTRIES, Color(0xFF1D4ED8), Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1E40AF), Color(0xFF172554))), "Chefchaouen dreamscape painted in soothing cobalt blues"),

        // --- 3. NATURE & WILDERNESS (10) ---
        BackgroundTheme("NATURE_PINE_MIST", "Pine Forest in Mist", TableCategory.NATURE, Color(0xFF14532D), Brush.verticalGradient(listOf(Color(0xFF166534), Color(0xFF14532D), Color(0xFF052E16))), "Towering evergreen canopy shrouded in cool dawn fog"),
        BackgroundTheme("NATURE_BEACH_SUNSET", "Tropical Beach Sunset", TableCategory.NATURE, Color(0xFF9A3412), Brush.verticalGradient(listOf(Color(0xFFF97316), Color(0xFFC2410C), Color(0xFF7C2D12))), "Gentle ocean breakers catching the last crimson sunlight"),
        BackgroundTheme("NATURE_DESERT_DUNES", "Golden Desert Dunes", TableCategory.NATURE, Color(0xFFB45309), Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFB45309), Color(0xFF78350F))), "Serene ripple patterns carved by gentle desert winds"),
        BackgroundTheme("NATURE_SNOW_PEAK", "Snowy Mountain Peak", TableCategory.NATURE, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF64748B), Color(0xFF334155), Color(0xFF0F172A))), "Jagged summit piercing above silent white sea of clouds"),
        BackgroundTheme("NATURE_WATERFALL", "Waterfall in Jungle", TableCategory.NATURE, Color(0xFF065F46), Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF047857), Color(0xFF064E3B))), "Cascading crystalline torrent surrounded by exotic tropical flora"),
        BackgroundTheme("NATURE_RAINY_WINDOW", "Rainy Window at Night", TableCategory.NATURE, Color(0xFF0F172A), Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))), "Soothing rain beads illuminated by cozy distant city lights"),
        BackgroundTheme("NATURE_AUTUMN_LEAVES", "Autumn Leaves", TableCategory.NATURE, Color(0xFF9A3412), Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFF9A3412), Color(0xFF451A03))), "Canopy of golden birch and fiery maple foliage"),
        BackgroundTheme("NATURE_SPRING_MEADOW", "Spring Meadow", TableCategory.NATURE, Color(0xFF15803D), Brush.verticalGradient(listOf(Color(0xFF22C55E), Color(0xFF15803D), Color(0xFF14532D))), "Lush rolling green hills dotted with blooming wildflowers"),
        BackgroundTheme("NATURE_CALM_LAKE", "Calm Lake at Dawn", TableCategory.NATURE, Color(0xFF0369A1), Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0F172A))), "Glass-like reflection of morning mist on still mountain water"),
        BackgroundTheme("NATURE_STARRY_NIGHT", "Starry Night Sky", TableCategory.NATURE, Color(0xFF0F172A), Brush.verticalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF020617))), "Glittering Milky Way arching across an obsidian sky"),

        // --- 4. SPACE & FANTASY (6) ---
        BackgroundTheme("SPACE_GALAXY_NEBULA", "Galaxy Nebula", TableCategory.SPACE_FANTASY, Color(0xFF581C87), Brush.verticalGradient(listOf(Color(0xFF9333EA), Color(0xFF581C87), Color(0xFF0F172A))), "Cosmic dust cloud glowing with stellar nursery brilliance"),
        BackgroundTheme("SPACE_EARTH", "Earth from Space", TableCategory.SPACE_FANTASY, Color(0xFF0C4A6E), Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0C4A6E), Color(0xFF020617))), "The pale blue dot curved gracefully against infinite cosmos"),
        BackgroundTheme("SPACE_MOON_SURFACE", "Moon Surface", TableCategory.SPACE_FANTASY, Color(0xFF18181B), Brush.verticalGradient(listOf(Color(0xFF52525B), Color(0xFF27272A), Color(0xFF09090B))), "Ancient impact craters and grey lunar regolith in stark contrast"),
        BackgroundTheme("FANTASY_ENCHANTED_FOREST", "Enchanted Forest", TableCategory.SPACE_FANTASY, Color(0xFF064E3B), Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF064E3B), Color(0xFF1E1B4B))), "Bioluminescent mushrooms and mystical fireflies"),
        BackgroundTheme("FANTASY_FLOATING_ISLANDS", "Floating Islands", TableCategory.SPACE_FANTASY, Color(0xFF0369A1), Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF1E1B4B))), "Gravity-defying mossy cliffs drifting above clouds"),
        BackgroundTheme("FANTASY_CORAL_REEF", "Underwater Coral Reef", TableCategory.SPACE_FANTASY, Color(0xFF0E7490), Brush.verticalGradient(listOf(Color(0xFF06B6D4), Color(0xFF0891B2), Color(0xFF164E63))), "Sunbeams filtering through crystal ocean down to living corals"),

        // --- 5. ABSTRACT & TEXTURES (8) ---
        BackgroundTheme("TEX_NEON_GRID", "Neon Grid", TableCategory.ABSTRACT_TEXTURES, Color(0xFF0F172A), Brush.verticalGradient(listOf(Color(0xFF831843), Color(0xFF0F172A), Color(0xFF020617))), "Retro synthwave horizon wireframe in magenta and cyan"),
        BackgroundTheme("TEX_GRADIENT_WAVES", "Soft Gradient Waves", TableCategory.ABSTRACT_TEXTURES, Color(0xFF312E81), Brush.verticalGradient(listOf(Color(0xFF6366F1), Color(0xFF3730A3), Color(0xFF1E1B4B))), "Silky fluid waves with peaceful iridescent sheen"),
        BackgroundTheme("TEX_WATERCOLOUR", "Watercolour Wash", TableCategory.ABSTRACT_TEXTURES, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF0D9488), Color(0xFF1E293B), Color(0xFF312E81))), "Subtle hand-painted indigo and emerald pigments"),
        BackgroundTheme("TEX_LOW_POLY", "Geometric Low-Poly", TableCategory.ABSTRACT_TEXTURES, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF1E293B), Color(0xFF0F172A))), "Faceted triangular crystal mesh with ambient occlusion"),
        BackgroundTheme("TEX_CARBON_FIBRE", "Carbon Fibre", TableCategory.ABSTRACT_TEXTURES, Color(0xFF18181B), Brush.verticalGradient(listOf(Color(0xFF27272A), Color(0xFF18181B), Color(0xFF09090B))), "High-tech composite twill weave texture"),
        BackgroundTheme("TEX_DARK_WOOD", "Dark Wood Table", TableCategory.ABSTRACT_TEXTURES, Color(0xFF451A03), Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFF451A03), Color(0xFF1C1917))), "Polished mahogany grain suitable for intimate card sessions"),
        BackgroundTheme("TEX_LEATHER_DESK", "Leather Desk", TableCategory.ABSTRACT_TEXTURES, Color(0xFF3F1D11), Brush.verticalGradient(listOf(Color(0xFF572C19), Color(0xFF3F1D11), Color(0xFF1C0D08))), "Executive saddle-stitched vintage leather pad"),
        BackgroundTheme("TEX_MARBLE", "Marble Surface", TableCategory.ABSTRACT_TEXTURES, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))), "Noble nero marquina dark marble with subtle quartz veins"),

        // --- 6. CLASSIC SOLID FELTS (4) ---
        BackgroundTheme("THEME_CASINO_GREEN", "Casino Green Felt", TableCategory.CLASSIC_FELTS, Color(0xFF0C5A35), Brush.verticalGradient(listOf(Color(0xFF146C43), Color(0xFF0C5A35), Color(0xFF06331C))), "Traditional tournament baize woven for authentic gameplay"),
        BackgroundTheme("FELT_ROYAL_BLUE", "Royal Blue Felt", TableCategory.CLASSIC_FELTS, Color(0xFF1E3A8A), Brush.verticalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF1E3A8A), Color(0xFF172554))), "Deep midnight navy felt preferred by European gaming salons"),
        BackgroundTheme("FELT_BURGUNDY", "Burgundy Velvet", TableCategory.CLASSIC_FELTS, Color(0xFF7F1D1D), Brush.verticalGradient(listOf(Color(0xFF991B1B), Color(0xFF7F1D1D), Color(0xFF450A0A))), "Rich Bordeaux wine velvet with aristocratic warmth"),
        BackgroundTheme("FELT_CHARCOAL_BLACK", "Charcoal Black Felt", TableCategory.CLASSIC_FELTS, Color(0xFF18181B), Brush.verticalGradient(listOf(Color(0xFF27272A), Color(0xFF18181B), Color(0xFF09090B))), "Modern ultra-dark matte table reducing eye fatigue"),

        // Custom Photo Theme
        BackgroundTheme("THEME_CUSTOM_PHOTO", "My Custom Background Photo", TableCategory.ABSTRACT_TEXTURES, Color(0xFF1E293B), Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF0F172A))), "Personal gallery photo used as table wallpaper")
    )

    // =========================================================================
    // 6. PRESET COMBOS
    // =========================================================================
    val presetCombos = listOf(
        PresetCombo("COMBO_ROYAL_NIGHT", "Royal Night", "Gold indices, royal crest back & Paris twilight skyline", "FACE_ROYAL_GOLD", "SUIT_GOLD_FOIL", "SCHEME_STANDARD", "BACK_ORNATE_CREST", "CITY_PARIS"),
        PresetCombo("COMBO_TOKYO_NEON", "Tokyo Neon", "Cyberpunk face, neon glow suits & Tokyo night skyline", "FACE_CYBERPUNK", "SUIT_NEON_LINE", "SCHEME_FOUR_COLOUR", "BACK_CIRCUIT_BOARD", "CITY_TOKYO"),
        PresetCombo("COMBO_EMERALD_CLASSIC", "Emerald Classic", "Classic cards, traditional felt & diamond back", "FACE_CLASSIC_STANDARD", "SUIT_CLASSIC_FILLED", "SCHEME_STANDARD", "BACK_GEOMETRIC_DIAMONDS", "THEME_CASINO_GREEN"),
        PresetCombo("COMBO_DESERT_GOLD", "Desert Gold", "Vintage ivory face, 3D embossed suits & Rajasthan desert", "FACE_VINTAGE_IVORY", "SUIT_3D_EMBOSSED", "SCHEME_STANDARD", "BACK_ARABESQUE", "LAND_RAJASTHAN"),
        PresetCombo("COMBO_NORDIC_AURORA", "Nordic Aurora", "Frosted glass face, starry night back & Iceland Aurora", "FACE_FROSTED_GLASS", "SUIT_GRADIENT", "SCHEME_FOUR_COLOUR", "BACK_STARRY_NIGHT", "LAND_ICELAND_AURORA"),
        PresetCombo("COMBO_SENIOR_CLEAR", "High Visibility", "Extra large indices, accessible contrast & clean dark table", "FACE_LARGE_INDEX", "SUIT_ROUNDED_SOFT", "SCHEME_HIGH_CONTRAST", "BACK_CHECKERBOARD", "FELT_CHARCOAL_BLACK")
    )

    // Helper lookups
    fun getBackground(id: String): BackgroundTheme {
        if (id == "CLASSIC_FELT") return backgrounds.first { it.id == "THEME_CASINO_GREEN" }
        return backgrounds.firstOrNull { it.id == id } ?: backgrounds.first { it.id == "THEME_CASINO_GREEN" }
    }

    fun getCardBack(id: String): CardBackTheme {
        if (id == "BACK_CRIMSON_ANVIL") return cardBacks.first { it.id == "BACK_DRAGON" }
        return cardBacks.firstOrNull { it.id == id } ?: cardBacks.first()
    }

    fun getCardFace(id: String): CardFaceTheme {
        if (id == "FACE_SENIOR_CLASSIC") return cardFaces.first { it.id == "FACE_LARGE_INDEX" }
        return cardFaces.firstOrNull { it.id == id } ?: cardFaces.first()
    }

    fun getSuitStyle(id: String): SuitStyleTheme {
        return suitStyles.firstOrNull { it.id == id } ?: suitStyles.first()
    }

    fun getSuitColorScheme(id: String): SuitColorScheme {
        return suitColorSchemes.firstOrNull { it.id == id } ?: suitColorSchemes.first()
    }
}
