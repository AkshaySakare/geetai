package com.aps.geetai

/**
 * VIEW LAYER — Background shader selector.
 *
 * This is UI-only state (which shader to render), so it lives in
 * ChatScreen as local `remember` state — not in the ViewModel.
 * The enum itself can stay at the top-level package.
 */
enum class BackgroundMode(val display: String, val shaderRes: Int) {
    FIRE("Fire",         R.raw.fire_shader),
    STAR_STATIC("Star",  R.raw.star_field),
    RAINBOW(" Rainbow",   R.raw.rainbow_shader),
    SMOKE("Smoke",       R.raw.smoke),
    RIPPEL("Ripple",     R.raw.ripple),
}
