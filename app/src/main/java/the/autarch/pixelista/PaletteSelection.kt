package the.autarch.pixelista

import androidx.compose.ui.graphics.Color

data class PaletteSelection(
    val name: String,
    val reference: String? = null,
    val topColors: List<Color>,
    val bottomColors: List<Color>
) {
    companion object {

        val database: List<PaletteSelection> = listOf(
            classic,
            dubbleGums16,
            vanillaMilkshake,
            fading16,
            na16,
            lostCentury,
            sugarCrush,
            general16,
            purrfect,
            endesga16
        )

        val default: PaletteSelection = classic
    }
}

private val PaletteSelection.Companion.classic: PaletteSelection
    get() = PaletteSelection(
        name = "Classic",
        topColors = listOf(
            Color.Black,
            Color(0xff273746),
            Color(0xff8e44ad),
            Color(0xff196f3d),
            Color(0xff935116),
            Color(0xff424949),
            Color(0xffe5e7e9),
            Color.White
        ),
        bottomColors = listOf(
            Color(0xffe74c3c),
            Color(0xfff39c12),
            Color(0xfff4d03f),
            Color(0xff2ecc71),
            Color(0xff3498db),
            Color(0xff512e5f),
            Color(0xffff9ad1),
            Color(0xffffe29a)
        )
    )

private val PaletteSelection.Companion.dubbleGums16: PaletteSelection
    get() = PaletteSelection(
        name = "Dubble Gums 16",
        reference = "https://lospec.com/palette-list/dubble-gums-16",
        topColors = listOf(
            Color(0xFFea6cb4),
            Color(0xFFffa8be),
            Color(0xFFe4fffe),
            Color(0xFF7be2de),
            Color(0xFF1d9ec8),
            Color(0xFF36a5a1),
            Color(0xFF56cf1e),
            Color(0xFFc2eb44),
        ),
        bottomColors = listOf(
            Color(0xFFfcd825),
            Color(0xFFf3b523),
            Color(0xFFeb883a),
            Color(0xFFec302c),
            Color(0xFFb21e4d),
            Color(0xFF5d2486),
            Color(0xFFae7eaf),
            Color(0xFFe0b6d3),
        )
    )

private val PaletteSelection.Companion.vanillaMilkshake: PaletteSelection
    get() = PaletteSelection(
        name = "Vanilla Milkshake",
        reference = "https://lospec.com/palette-list/vanilla-milkshake",
        topColors = listOf(
            Color(0xFF28282e),
            Color(0xFF6c5671),
            Color(0xFFd9c8bf),
            Color(0xFFf98284),
            Color(0xFFb0a9e4),
            Color(0xFFaccce4),
            Color(0xFFb3e3da),
            Color(0xFFfeaae4),
        ),
        bottomColors = listOf(
            Color(0xFF87a889),
            Color(0xFFb0eb93),
            Color(0xFFe9f59d),
            Color(0xFFffe6c6),
            Color(0xFFdea38b),
            Color(0xFFffc384),
            Color(0xFFfff7a0),
            Color(0xFFfff7e4),
        )
    )

private val PaletteSelection.Companion.fading16: PaletteSelection
    get() = PaletteSelection(
        name = "Fading 16",
        reference = "https://lospec.com/palette-list/fading-16",
        topColors = listOf(
            Color(0xFFddcf99),
            Color(0xFFcca87b),
            Color(0xFFb97a60),
            Color(0xFF9c524e),
            Color(0xFF774251),
            Color(0xFF4b3d44),
            Color(0xFF4e5463),
            Color(0xFF5b7d73),
        ),
        bottomColors = listOf(
            Color(0xFF8e9f7d),
            Color(0xFF645355),
            Color(0xFF8c7c79),
            Color(0xFFa99c8d),
            Color(0xFF7d7b62),
            Color(0xFFaaa25d),
            Color(0xFF846d59),
            Color(0xFFa88a5e),
        )
    )

private val PaletteSelection.Companion.na16: PaletteSelection
    get() = PaletteSelection(
        name = "NA 16",
        reference = "https://lospec.com/palette-list/na16",
        topColors = listOf(
            Color(0xFF8c8fae),
            Color(0xFF584563),
            Color(0xFF3e2137),
            Color(0xFF9a6348),
            Color(0xFFd79b7d),
            Color(0xFFf5edba),
            Color(0xFFc0c741),
            Color(0xFF647d34),
        ),
        bottomColors = listOf(
            Color(0xFFe4943a),
            Color(0xFF9d303b),
            Color(0xFFd26471),
            Color(0xFF70377f),
            Color(0xFF7ec4c1),
            Color(0xFF34859d),
            Color(0xFF17434b),
            Color(0xFF1f0e1c),
        )
    )

private val PaletteSelection.Companion.lostCentury: PaletteSelection
    get() = PaletteSelection(
        name = "Lost Century",
        reference = "https://lospec.com/palette-list/lost-century",
        topColors = listOf(
            Color(0xFFd1b187),
            Color(0xFFc77b58),
            Color(0xFFae5d40),
            Color(0xFF79444a),
            Color(0xFF4b3d44),
            Color(0xFFba9158),
            Color(0xFF927441),
            Color(0xFF4d4539),
        ),
        bottomColors = listOf(
            Color(0xFF77743b),
            Color(0xFFb3a555),
            Color(0xFFd2c9a5),
            Color(0xFF8caba1),
            Color(0xFF4b726e),
            Color(0xFF574852),
            Color(0xFF847875),
            Color(0xFFab9b8e),
        )
    )

private val PaletteSelection.Companion.sugarCrush: PaletteSelection
    get() = PaletteSelection(
        name = "5ugxrCrxsh",
        reference = "https://lospec.com/palette-list/5ugxrcrxsh",
        topColors = listOf(
            Color(0xFF220522),
            Color(0xFF411782),
            Color(0xFF384ec5),
            Color(0xFF0ca8a4),
            Color(0xFFa1df2a),
            Color(0xFFc5ffc9),
            Color(0xFFe5cc21),
            Color(0xFFfc7756),
        ),
        bottomColors = listOf(
            Color(0xFFc93267),
            Color(0xFF8a0c7b),
            Color(0xFFdc32a6),
            Color(0xFFe183cf),
            Color(0xFFac4cff),
            Color(0xFF5c43a9),
            Color(0xFF7c94f4),
            Color(0xFFc9c4e8),
        )
    )

private val PaletteSelection.Companion.general16: PaletteSelection
    get() = PaletteSelection(
        name = "General 16",
        reference = "https://lospec.com/palette-list/general-16",
        topColors = listOf(
            Color(0xFFd7193a),
            Color(0xFFa0243a),
            Color(0xFF50212a),
            Color(0xFFffbc29),
            Color(0xFFca8915),
            Color(0xFFb36615),
            Color(0xFF4bc417),
            Color(0xFF47a021),
        ),
        bottomColors = listOf(
            Color(0xFF0d6c27),
            Color(0xFF2575d3),
            Color(0xFF295ca4),
            Color(0xFF1d3170),
            Color(0xFFbd24eb),
            Color(0xFF992db9),
            Color(0xFF4f175f),
            Color(0xFF241f25),
        )
    )

private val PaletteSelection.Companion.purrfect: PaletteSelection
    get() = PaletteSelection(
        name = "Purr-fect Palette",
        reference = "https://lospec.com/palette-list/purr-fect-palette",
        topColors = listOf(
            Color(0xFFff5fab),
            Color(0xFF9d35d4),
            Color(0xFF0001b5),
            Color(0xFF28a8ff),
            Color(0xFF73ffd3),
            Color(0xFFfff4ce),
            Color(0xFFfff247),
            Color(0xFFff9352),
        ),
        bottomColors = listOf(
            Color(0xFFc70033),
            Color(0xFF780b35),
            Color(0xFFc26435),
            Color(0xFFffb9a9),
            Color(0xFFba97d6),
            Color(0xFF382752),
            Color(0xFF007587),
            Color(0xFF18cf3b),
        )
    )

private val PaletteSelection.Companion.endesga16: PaletteSelection
    get() = PaletteSelection(
        name = "ENDESGA 16",
        reference = "https://lospec.com/palette-list/purr-fect-palette",
        topColors = listOf(
            Color(0xFFe4a672),
            Color(0xFFb86f50),
            Color(0xFF743f39),
            Color(0xFF3f2832),
            Color(0xFF9e2835),
            Color(0xFFe53b44),
            Color(0xFFfb922b),
            Color(0xFFffe762),
        ),
        bottomColors = listOf(
            Color(0xFF63c64d),
            Color(0xFF327345),
            Color(0xFF193d3f),
            Color(0xFF4f6781),
            Color(0xFFafbfd2),
            Color(0xFFffffff),
            Color(0xFF2ce8f4),
            Color(0xFF0484d1),
        )
    )