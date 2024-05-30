package app.tusky.benchmark

import android.graphics.Color
import android.text.SpannableString
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.tusky.benchmark.new_.highlightSpans
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class Benchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    val test1 = "test of a status with #one hashtag http"
    val test2 = """
        test
        http:// #hashtag https://connyduck.at/
        http://example.org
        this is a #test
        and this is a @mention@test.com @test @test@test456@test.com
    """.trimIndent()

    val test3 = """
        @mention@test.social Just your ordinary mention with a hashtag
        #test
    """.trimIndent()

    val test4 = """
        @mention@test.social Just your ordinary mention with a url
        https://riot.im/app/#/room/#Tusky:matrix.org
    """.trimIndent()

    @Test
    fun old_1() {
        benchmarkRule.measureRepeated {
            old.highlightSpans(SpannableString(test1), Color.RED)
        }
    }

    @Test
    fun new_1() {
        benchmarkRule.measureRepeated {
            SpannableString(test1).highlightSpans(Color.RED)
        }
    }

    @Test
    fun old_2() {
        benchmarkRule.measureRepeated {
            old.highlightSpans(SpannableString(test2), Color.RED)
        }
    }

    @Test
    fun new_2() {
        benchmarkRule.measureRepeated {
            SpannableString(test2).highlightSpans(Color.RED)
        }
    }

    @Test
    fun old_3() {
        benchmarkRule.measureRepeated {
            old.highlightSpans(SpannableString(test3), Color.RED)
        }
    }

    @Test
    fun new_3() {
        benchmarkRule.measureRepeated {
            SpannableString(test3).highlightSpans(Color.RED)
        }
    }

    @Test
    fun old_4() {
        benchmarkRule.measureRepeated {
            old.highlightSpans(SpannableString(test4), Color.RED)
        }
    }

    @Test
    fun new_4() {
        benchmarkRule.measureRepeated {
            SpannableString(test4).highlightSpans(Color.RED)
        }
    }
}
