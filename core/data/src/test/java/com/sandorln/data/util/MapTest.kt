package com.sandorln.data.util

import com.sandorln.database.model.ItemEntity
import com.sandorln.model.data.map.MapType
import org.junit.Assert.assertEquals
import org.junit.Test

class MapTest {

    @Test
    fun modern_and_aram_item_maps_to_ALL() {
        // 무한의 대검, 라바돈 등 협곡(11)과 칼바람(12) 모두에서 구매 가능한 아이템
        val maps = mapOf("11" to true, "12" to true)
        val entity = maps.asMapTypeEntity()
        assertEquals(ItemEntity.MapTypeEntity.ALL, entity)
        assertEquals(MapType.ALL, entity.asData())
    }

    @Test
    fun modern_only_item_maps_to_SUMMONER_RIFT() {
        // 소환사의 협곡(11) 전용 아이템
        val maps = mapOf("11" to true, "12" to false)
        val entity = maps.asMapTypeEntity()
        assertEquals(ItemEntity.MapTypeEntity.SUMMONER_RIFT, entity)
        assertEquals(MapType.SUMMONER_RIFT, entity.asData())
    }

    @Test
    fun aram_only_item_maps_to_ARAM() {
        // 칼바람(12/14) 전용 아이템
        val maps = mapOf("11" to false, "12" to true)
        val entity = maps.asMapTypeEntity()
        assertEquals(ItemEntity.MapTypeEntity.ARAM, entity)
        assertEquals(MapType.ARAM, entity.asData())
    }

    @Test
    fun classic_only_item_maps_to_CLASSIC_SUMMONER_RIFT() {
        // 구 클래식 맵(1, 2) 전용 아이템 (황금의 심장, 현자의 돌 등)
        val maps = mapOf("1" to true, "2" to true, "11" to false, "12" to false)
        val entity = maps.asMapTypeEntity()
        assertEquals(ItemEntity.MapTypeEntity.CLASSIC_SUMMONER_RIFT, entity)
        assertEquals(MapType.CLASSIC_SUMMONER_RIFT, entity.asData())
    }

    @Test
    fun arena_or_special_mode_item_maps_to_NONE() {
        // 아레나(30) 등 특수 모드 전용 아이템 (밴들파이프, 원형질 안전장치 등)
        val maps = mapOf("11" to false, "12" to false, "30" to true)
        val entity = maps.asMapTypeEntity()
        assertEquals(ItemEntity.MapTypeEntity.NONE, entity)
        assertEquals(MapType.NONE, entity.asData())
    }
}
