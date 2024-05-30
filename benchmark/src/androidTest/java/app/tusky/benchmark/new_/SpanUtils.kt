package app.tusky.benchmark.new_

import android.text.Spannable
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import app.tusky.benchmark.MentionSpan
import app.tusky.benchmark.NoUnderlineURLSpan
import java.util.regex.Pattern

/**
 * @see <a href="https://github.com/tootsuite/mastodon/blob/master/app/models/tag.rb">
 *     Tag#HASHTAG_RE</a>.
 */
private const val HASHTAG_SEPARATORS = "_\\u00B7\\u30FB\\u200c"
private const val x = "(?:^|[^/)\\w])#(([\\w_][\\w$HASHTAG_SEPARATORS]*[\\p{Alpha}$HASHTAG_SEPARATORS][\\w$HASHTAG_SEPARATORS]*[\\w_])|([\\w_]*[\\p{Alpha}][\\w_]*))"

private const val TAG_REGEX = "(?<![=/)\\p{Alnum}])(#(([\\w_][\\w$HASHTAG_SEPARATORS]*[\\p{Alpha}$HASHTAG_SEPARATORS][\\w$HASHTAG_SEPARATORS]*[\\w_])|([\\w_]*[\\p{Alpha}][\\w_]*)))"

/**
 * @see <a href="https://github.com/tootsuite/mastodon/blob/master/app/models/account.rb">
 *     Account#MENTION_RE</a>
 */
private const val USERNAME_REGEX = "[a-z0-9_]+([a-z0-9_.-]+[a-z0-9_]+)?"
private const val MENTION_REGEX = "(?<![=/\\w])(@($USERNAME_REGEX)(?:@[\\w.-]+[\\w]+)?)"


private val spanClasses = listOf(ForegroundColorSpan::class.java, URLSpan::class.java)
private val finders = listOf(
    PatternFinder("http://", FoundMatchType.HTTP_URL, VALID_URL_PATTERN_STRING),
    PatternFinder("https://", FoundMatchType.HTTPS_URL, VALID_URL_PATTERN_STRING),
    PatternFinder("#", FoundMatchType.TAG, TAG_REGEX),
    PatternFinder("@", FoundMatchType.MENTION, MENTION_REGEX)
)

private enum class FoundMatchType {
    HTTP_URL,
    HTTPS_URL,
    TAG,
    MENTION
}

private class PatternFinder(
    val searchString: String,
    val type: FoundMatchType,
    regex: String
) {
    val pattern: Pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
}

/**
 * Takes text containing mentions and hashtags and urls and makes them the given colour.
 */
fun highlightSpans(text: Spannable, colour: Int) {
    // Strip all existing colour spans.
    for (spanClass in spanClasses) {
        clearSpans(text, spanClass)
    }

    var currentIndex = 0

    while (currentIndex < text.length) {
        for(finder in finders) {
            if (text.startsWith(finder.searchString, startIndex = currentIndex)) {

                val offset = if (currentIndex > 0) -1 else 0

                val matcher = finder.pattern.matcher(text.substring(currentIndex + offset))

                if (matcher.find()) {
                    val start = matcher.start(1) + currentIndex + offset

                    if (start != currentIndex) {
                        currentIndex += finder.searchString.length - 1
                        break
                    }

                    val end = matcher.end(1) + currentIndex + offset

                    text.setSpan(
                        getSpan(finder.type, text, colour, start, end),
                        start,
                        end,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    currentIndex = end - 1
                    break
                }
            }
        }
        currentIndex++
    }

}

private fun <T> clearSpans(text: Spannable, spanClass: Class<T>) {
    for (span in text.getSpans(0, text.length, spanClass)) {
        text.removeSpan(span)
    }
}

private fun getSpan(
    matchType: FoundMatchType,
    text: CharSequence,
    colour: Int,
    start: Int,
    end: Int
): CharacterStyle {
    return when (matchType) {
        FoundMatchType.HTTP_URL, FoundMatchType.HTTPS_URL -> NoUnderlineURLSpan(text.substring(start, end))
        FoundMatchType.MENTION -> MentionSpan(text.substring(start, end))
        else -> ForegroundColorSpan(colour)
    }
}
