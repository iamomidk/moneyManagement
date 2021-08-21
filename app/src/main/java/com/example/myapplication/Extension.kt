@file:Suppress("DEPRECATION", "EXTENSION_SHADOWED_BY_MEMBER")

package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.CycleInterpolator
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.math.RoundingMode
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "Extension_TAG"

fun File.getRealPathFromURI(context: Context): String {
	val contentURI: Uri = Uri.fromFile(this)
	val result: String
	val cursor: Cursor? = context.contentResolver.query(contentURI, null, null, null, null)
	if (cursor == null) { // Source is Dropbox or other similar local file path
		result = contentURI.path.toString()
	} else {
		cursor.moveToFirst()
		val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
		result = cursor.getString(idx)
		cursor.close()
	}
	return result
}

fun String.shorten(consideredWordsCount: Int): String {
	if (isEmpty) return ""
	val words = split(" ")
	var wordsCount: Int = consideredWordsCount
	if (consideredWordsCount > words.size || consideredWordsCount <= 0) wordsCount = words.size
	return buildString {
		for (x in 0 until wordsCount) {
			append(words[x])
			append(" ")
		}
		append("...")
	}
}

/**
 * BottomNavigationView change typeface
 * @param typeface: Typeface
 */
fun BottomNavigationView.changeNavTypeface(typeface: Typeface) = checker(this, typeface)

private fun checker(view: View, typeface: Typeface) {
	when (view) {
		is ViewGroup -> for (i in 0 until view.childCount) checker(view.getChildAt(i), typeface)
		is TextView -> view.typeface = typeface
	}
}

/**
 * Set Divider to RecyclerView Items
 */
fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
	DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL).apply {
		ContextCompat.getDrawable(context, drawableRes)?.let {
			setDrawable(it)
			addItemDecoration(this)
		}
	}
}


fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

fun Context.dp(@DimenRes dimen: Int): Float = px(dimen) / resources.displayMetrics.density

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) =
	addTextChangedListener(
		object : TextWatcher {
			override fun afterTextChanged(editable: Editable?) = afterTextChanged.invoke(editable.toString())
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
		}
	)

val String.encodeStringToUTF8: String
	get() = URLEncoder.encode(this, "UTF-8").replace("%2", ",")

val String.decodeStringToUTF8: String
	get() = String(toByteArray(), Charset.forName("UTF8"))

val String.toASCII: String
	get() = StringBuilder().apply {
		for (i in toByteArray()) {
			if (this.isNotEmpty()) append(",")
			append(i.toInt())
		}
	}.toString()

fun String.toByteArrayFromStringArray(separator: String): String =
	if (isNotEmpty() && contains(separator)) {
		buildString {
			split(separator).forEach { separated -> if (separator.isNotEmpty()) append((separated.toInt()).toChar()) }
		}
	} else ""

fun Number.withDigits(digits: Int): String = DecimalFormat().apply {
	maximumFractionDigits = digits
	roundingMode = RoundingMode.UP
}.format(this)

fun Double.withDigits(digits: Int, integerFraction: Int): String = DecimalFormat().apply {
	maximumIntegerDigits = integerFraction
	maximumFractionDigits = digits
}.format(this)

val Double.oneDigit: String
	get() = DecimalFormat().apply { maximumFractionDigits = 1 }.format(this)

/**
 * Extension function to get snapHelper position
 */
fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
	(recyclerView.layoutManager ?: return NO_POSITION).apply {
		val snapView = findSnapView(this) ?: return NO_POSITION
		return getPosition(snapView)
	}
}

/** Extension function to changing textSize in snackbar*/
fun Snackbar.textSize(txtSize: Float) {
	(view.findViewById<TextView>(R.id.snackbar_text)).apply { textSize = txtSize }
}

/** Extension function to changing gravity in snackbar*/
fun Snackbar.gravity(gravity: Int) {
	(view.findViewById<TextView>(R.id.snackbar_text)).apply { this.gravity = gravity }
}

val CharSequence.isEmpty: Boolean get() = isEmpty()
val CharSequence.isNotEmpty: Boolean get() = isNotEmpty()
val CharSequence.isNullOrEmpty: Boolean get() = isNullOrEmpty()
val CharSequence.isNullOrBlank: Boolean get() = isNullOrBlank()
val CharSequence.isBlank: Boolean get() = isBlank()
val CharSequence.isNotBlank: Boolean get() = isNotBlank()
val CharSequence.isDigitsOnly: Boolean get() = isDigitsOnly()

val Collection<*>.isEmpty: Boolean get() = isEmpty()
val Collection<*>.isNotEmpty: Boolean get() = isNotEmpty()
val Collection<*>.isNullOrEmpty: Boolean get() = isNullOrEmpty()

val View.makeGone get() = true.also { isGone = it }
val View.makeInvisible get() = true.also { isInvisible = it }
val View.makeVisible get() = true.also { isVisible = it }

val View.makeVisibleWithFadeAnimation
	get() = apply {
		alpha = 0f
		translationX = width.toFloat()
		makeVisible
		animate().apply {
			duration = 500
			translationX = 0f
			setListener(
				object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator?) {
						super.onAnimationEnd(animation)
						clearAnimation()
					}
				}
			)
			alpha(1f)
			start()
		}
	}

val View.makeVisibleWithSlideAnimation
	get() = apply {
		alpha = 0f
		translationY = height.toFloat()
		makeVisible
		animate().apply {
			duration = 500
			translationY = 0f
			setListener(
				object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator?) {
						super.onAnimationEnd(animation)
						clearAnimation()
					}
				}
			)
			alpha(1f)
			start()
		}
	}

val View.makeGoneWithAnimation
	get() = apply {
		alpha = 1f
		translationX = 0f
		animate().apply {
			duration = 500
			translationX = width.toFloat()
			setListener(
				object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator?) {
						super.onAnimationEnd(animation)
						makeGone
						clearAnimation()
					}
				}
			)
			alpha(0f)
			start()
		}
	}

val String.toPriceAmount: String
	get() = DecimalFormat("###,###,###.00").format(toDouble())

val Double.toPriceAmount: String
	get() = DecimalFormat("###,###,###.00").format(this)

val String.toLocaleLang: String
	get() {
		val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
		if (isEmpty()) return ""
		return buildString {
			for (element in this@toLocaleLang) when (element) {
				in '0'..'9' -> (element.toString().toInt()).apply { append(persianNumbers[this]) }
				else -> append(element)
			}
		}
	}

val String.toEnglish: String
	get() {
		val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
		val englishNumbers = arrayOf("0".."9")
		if (isEmpty()) return ""
		return buildString {
			for (element in this@toEnglish) {
				element.apply {
					when {
						toString() == "،" -> append("٫")
						toString() in persianNumbers -> persianNumbers.forEachIndexed { index, item ->
							if (item == toString()) append(englishNumbers[index])
						}
						else -> append(this)
					}
				}
			}
		}
	}

val Number.toLocalLang: String
	get() {
		val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
		return buildString {
			for (element in this@toLocalLang.toString()) when (element) {
				in '0'..'9' -> (element.toString().toInt()).apply { append(persianNumbers[this]) }
				else -> append(element)
			}
		}
	}

val Number.toEnglish: String
	get() {
		val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
		val englishNumbers = arrayOf("0".."9")
		return buildString {
			for (element in this@toEnglish.toString()) {
				element.apply {
					when {
						toString() == "،" -> append("٫")
						toString() in persianNumbers -> persianNumbers.forEachIndexed { index, item ->
							if (item == toString()) append(englishNumbers[index])
						}
						else -> append(this)
					}
				}
			}
		}
	}

val Long.persianDateTimeInPersian: String
	@SuppressLint("SimpleDateFormat")
	get() {
		var jDate = ""
		var time = ""
		try {
			SimpleDateFormat("HH:mm:ss").apply {
				timeZone = TimeZone.getDefault()
				time = format(Date(this@persianDateTimeInPersian * 1000))
			}
			jDate = dayOfWeek(this, isPersian = true)
		} catch (e: Exception) {
			println("tag = $TAG, msg = error message : ${e.message}, throwable = e")
		}
		return "${
			jDate.toLocaleLang
		} - ${
			time.toLocaleLang
		}"
	}

val Long.persianDateTimeInEnglish: String
	@SuppressLint("SimpleDateFormat")
	get() {
		var jDate = ""
		var time = ""
		try {
			SimpleDateFormat("HH:mm:ss").apply {
				timeZone = TimeZone.getDefault()
				time = format(Date(this@persianDateTimeInEnglish * 1000))
			}
			jDate = dayOfWeek(this, isPersian = true)
		} catch (e: Exception) {
			println("tag = $TAG, msg = error message : ${e.message}, throwable = e")
		}
		return "${
			jDate.toEnglish
		} - ${
			time.toEnglish
		}"
	}


private fun dayOfWeek(ts: Long, isPersian: Boolean): String {
	val today = Calendar.getInstance(TimeZone.getDefault()).apply {
		this.time = Date(System.currentTimeMillis())
	}.get(Calendar.DAY_OF_YEAR)
	val tsDay = Calendar.getInstance(TimeZone.getDefault()).apply {
		this.time = Date(ts * 1000)
	}.get(Calendar.DAY_OF_YEAR)
	return if (isPersian)
		if (today - tsDay == 0) "امروز" else if (today - tsDay == 1) "دیروز" else "${today - tsDay} روز گذشته"
	else
		if (today - tsDay == 0) "today" else if (today - tsDay == 1) "yesterday" else "${today - tsDay} day's ago"
}

fun Number?.convertNumbersOnly(digits: Int = 2): String {
	this ?: return "0"
	var strNumber = withDigits(digits)
	if (strNumber.endsWith(".00")) strNumber = strNumber.replace(".00", "")
	return strNumber
}

fun Context.getColor(@ColorRes resId: Int): Int = resources.getColor(resId, null)

inline fun View.showIf(crossinline condition: (View) -> Boolean): Int {
	if (condition(this)) makeVisible else makeGone
	return visibility
}

inline fun View.hideIf(crossinline condition: (View) -> Boolean): Int {
	if (condition(this)) makeGone else makeVisible
	return visibility
}

inline fun intAnimator(
	vararg values: Int,
	dur: Long = 1000,
	crossinline updateCallback: (ValueAnimator, animatedValue: Int) -> Unit,
) {
	if (values.size == 2) ValueAnimator.ofInt(values[0], values[1]).apply {
		duration = dur
		addUpdateListener { animation -> animation?.let { updateCallback.invoke(it, it.animatedValue as Int) } }
		start()
	} else ValueAnimator.ofInt(values[0]).apply {
		duration = dur
		addUpdateListener { animation -> animation?.let { updateCallback.invoke(it, it.animatedValue as Int) } }
		start()
	}
}

inline fun floatAnimator(
	vararg values: Float,
	dur: Long = 500,
	crossinline updateCallback: (ValueAnimator, animatedValue: Float) -> Unit,
) {
	if (values.size == 2) ValueAnimator.ofFloat(values[0], values[1]).apply {
		duration = dur
		addUpdateListener { animation -> animation?.let { updateCallback.invoke(it, it.animatedValue as Float) } }
		start()
	} else ValueAnimator.ofFloat(values[0]).apply {
		duration = dur
		addUpdateListener { animation -> animation?.let { updateCallback.invoke(it, it.animatedValue as Float) } }
		start()
	}
}

fun View.fadeAnim(endAction: () -> Unit): ViewPropertyAnimator = run {
	makeVisible
	alpha = 0f
	animate().apply {
		alpha(1f)
		duration = 1500
		withEndAction { endAction.invoke() }
	}
}

fun View.moveToBottomAnim(endAction: () -> Unit) {
	makeVisible
	alpha = 0f
	translationY = 50f
	animate().apply {
		alpha(1f)
		translationYBy(-50f)
		duration = 1500
		withEndAction {
			clearAnimation()
			endAction.invoke()
		}
	}
}

fun View.scaleVerticalAnim(endAction: () -> Unit): ViewPropertyAnimator = run {
	makeVisible
	alpha = 0f
	animate().apply {
		alpha(1f)
		scaleYBy(1f)
		duration = 1500
		withEndAction { endAction.invoke() }
	}
}

fun View.scaleHorizontalAnim(endAction: () -> Unit): ViewPropertyAnimator = run {
	makeVisible
	alpha = 0f
	animate().apply {
		alpha(1f)
		scaleXBy(1f)
		duration = 3000
		interpolator = CycleInterpolator(10f)
		withEndAction { endAction.invoke() }
	}
}

val Long.toDateTime: String
	@SuppressLint("SimpleDateFormat")
	get() {
		var jDate = ""
		var time = ""
		try {
			SimpleDateFormat("HH:mm:ss").apply {
				timeZone = TimeZone.getDefault()
				time = format(Date(this@toDateTime))
			}
			SimpleDateFormat("yyyy/MM/dd").apply {
				jDate = format(Date(this@toDateTime))
			}
		} catch (e: Exception) {
			e.toString()
		}
		return "$jDate - $time".toLocaleLang
	}

val Long.toTime: String
	@SuppressLint("SimpleDateFormat")
	get() {
		var time = ""
		try {
			SimpleDateFormat("HH:mm:ss").apply {
				timeZone = TimeZone.getDefault()
				time = format(Date(this@toTime))
			}
		} catch (e: Exception) {
			e.toString()
		}
		return time.toLocaleLang
	}

val Long.toDate: String
	@SuppressLint("SimpleDateFormat")
	get() {
		var jDate = ""
		try {
			SimpleDateFormat("yyyy/MM/dd").apply {
				jDate = format(Date(this@toDate))
			}
		} catch (e: Exception) {
			e.toString()
		}
		return jDate.toLocaleLang
	}

@SuppressLint("SimpleDateFormat")
fun Long.getDateWithMonthName(isPersian: Boolean = true): String {
	var jDate = ""
	var year: String
	var month: String
	var day: String
	var todayYear: String
	try {
		SimpleDateFormat("yyyy/MM/dd").apply {
			jDate = format(Date(this@getDateWithMonthName * 1000))
			year = jDate.split("/")[0]
			month = jDate.split("/")[1]
			day = jDate.split("/")[2]
			jDate = when (month) {
				"01" -> "$day January"
				"02" -> "$day February"
				"03" -> "$day March"
				"04" -> "$day April"
				"05" -> "$day May"
				"06" -> "$day June"
				"07" -> "$day July"
				"08" -> "$day August"
				"09" -> "$day September"
				"10" -> "$day October"
				"11" -> "$day November"
				"12" -> "$day December"
				else -> ""
			}
			todayYear = SimpleDateFormat("yyyy/MM/dd").format(Date(System.currentTimeMillis())).split("/")[0]
			if (year != todayYear) jDate += year
		}
	} catch (e: Exception) {
		e.toString()
	}
	return jDate
}

val AppCompatEditText.textString: String get() = if (text == null) "" else text.toString()
val AppCompatEditText.textDouble: Double get() = if (text == null) 0.0 else text.toString().toDouble()
val AppCompatEditText.isEmpty: Boolean get() = textString.isEmpty()
val AppCompatEditText.isNotEmpty: Boolean get() = textString.isNotEmpty()
val AppCompatEditText.makeEmpty: String get() = "".also { setText(it) }