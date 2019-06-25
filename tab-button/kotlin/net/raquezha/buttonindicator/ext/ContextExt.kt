package net.raquezha.buttonindicator.ext

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun Context.getString(id: Int, arg: String = ""): String {
    return this.getString(id, arg)
}


fun Context.getDrawableById(id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}


fun Context.getColorInt(id: Int): Int {
    return ContextCompat.getColor(this, id)
}
