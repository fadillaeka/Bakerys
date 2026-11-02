data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val imageResId: Int, // ID gambar dari res/drawable
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false
)