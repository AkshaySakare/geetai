package com.aps.geetai.model

/**
 * MODEL LAYER
 *
 * Mood enum — moved from util into model since it is core domain data,
 * not a utility. The displayName is shown in the UI chip.
 */
enum class Mood(val displayName: String, val emoji: String) {
    HAPPY("Happy",     "😊"),
    SAD("Sad",         "😢"),
    ANGRY("Angry",     "😡"),
    ANXIOUS("Anxious", "😰"),
    CONFUSED("Confused","😕"),
    PEACEFUL("Peaceful","😌"),
    MOTIVATED("Motivated","💪"),
    NEUTRAL("Neutral", "🙂");

    /** Label shown on the mood chip button */
    val chipLabel: String get() = "$emoji $displayName"
}
