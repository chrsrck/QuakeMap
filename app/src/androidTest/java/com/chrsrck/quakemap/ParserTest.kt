package com.chrsrck.quakemap

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chrsrck.quakemap.data.network.JsonParserUSGS
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.collections.ArrayList

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
    private val parser : JsonParserUSGS = JsonParserUSGS()
    private val delta = 0.01 // double precision errors means cannot be direct equal


    @Test
    fun test_error_parse() {
        val hashMap = parser.parseQuakes(JSONObject("{abc: 123}"))
        assertTrue(hashMap.isEmpty())
    }

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

    @Test
    fun test_one_quake() {
        val name = "just_one_quake.json"
        val json = JSONObject(readTestDataFile(name))
        val hashMap = parser.parseQuakes(json)
        assertFalse(hashMap.isEmpty())
        assertEquals(1, hashMap.size)
        val l = ArrayList(hashMap.values)
        val quake = l[0]

        assertEquals("us70005rgt", quake.id)
        assertEquals(4.5999999999999996, quake.magnitude, delta)
        assertEquals("131km N of Road Town, British Virgin Islands", quake.place)
        assertEquals(1570410134234, quake.time)
        assertEquals("earthquake", quake.type)
        assertEquals(-64.521500000000003, quake.longitude, delta)
        assertEquals(19.597200000000001, quake.latitude, delta)
    }

    @Test
    fun test_multiple_quakes() {
        val name = "above_four_half_mag_eight_quakes.json"
        val json = JSONObject(readTestDataFile(name))
        val hashMap = parser.parseQuakes(json)
        assertFalse(hashMap.isEmpty())
        assertEquals(8, hashMap.size)

        val l = ArrayList(hashMap.values)
        val quake = l[5]

        assertEquals("us70005r81", quake.id)
        assertEquals(4.5999999999999996, quake.magnitude, delta)
        assertEquals("91km E of Namie, Japan", quake.place)
        assertEquals(1570341795505, quake.time)
        assertEquals("earthquake", quake.type)
        assertEquals(142.02889999999999, quake.longitude, delta)
        assertEquals(37.4039, quake.latitude, delta)
    }

    private fun readTestDataFile(filename : String) : String {
        val stream = this.javaClass.classLoader.getResourceAsStream(filename)
        return Scanner(stream).useDelimiter("\\A").next()
    }
}
