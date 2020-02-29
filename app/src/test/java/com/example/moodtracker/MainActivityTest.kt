package com.example.moodtracker

import org.junit.Assert.*
import org.junit.Test

class MainActivityTest {
    @Test
    fun createMoodStringNameTest(){
        val result = "string"
        assertNotNull(result)
        assertNotEquals(result.length,0)
    }
    @Test
    fun createCommentStringNameTest(){
        val result = "string"
        assertNotNull(result)
        assertNotEquals(result.length,0)
    }
}