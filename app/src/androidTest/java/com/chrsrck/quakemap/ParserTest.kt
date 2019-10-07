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
 * Parser is instrumented test due to relying on
 * the json.org version of a json object, which
 * is bundled with the Android SDK.
 */
@RunWith(AndroidJUnit4::class)
class ParserTest {
//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().getTargetContext()
//        assertEquals("com.chrsrck.quakemap", appContext.packageName)
//    }
    val parser : jsonParserUSGS = jsonParserUSGS()

    @Test
    fun empty_parse() {
        val hashMap = parser.parseQuakes(JSONObject("{}"))
        assertTrue(hashMap.isEmpty())
    }

    @Test
    fun test_no_quakes() {
        val name = "sig_zero_quakes.json"
        val json = JSONObject(readTestDataFile(name))
        val hashMap = parser.parseQuakes(json)
        assertTrue(hashMap.isEmpty())
    }

    private fun readTestDataFile(filename : String) : String {
        val stream = this.javaClass.classLoader.getResourceAsStream(filename)
        return Scanner(stream).useDelimiter("\\A").next()
    }
}
