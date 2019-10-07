package com.chrsrck.quakemap

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chrsrck.quakemap.data.jsonParserUSGS
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().getTargetContext()
        assertEquals("com.chrsrck.quakemap", appContext.packageName)
    }

    @Test
    fun empty_parse() {
        val parser = jsonParserUSGS()
        parser.parseQuakes(JSONObject(""))
    }

    @Test
    fun test_no_quakes() {
        val parser = jsonParserUSGS()
        val stream = this.javaClass.classLoader.getResourceAsStream("sig_zero_quakes.json")
        val scanner = Scanner(stream).useDelimiter("\\A")
        val hashMap = parser.parseQuakes(JSONObject(scanner.next()))
        assertTrue(hashMap.isEmpty())
    }
}
