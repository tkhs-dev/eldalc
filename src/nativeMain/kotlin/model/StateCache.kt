package model

import kotlinx.serialization.Serializable

@Serializable
data class StateCache(
    val courseString: String,
    val courseId: String,
    val unit: String,
    val step: String
)
