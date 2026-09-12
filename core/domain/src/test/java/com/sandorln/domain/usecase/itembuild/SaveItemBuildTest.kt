package com.sandorln.domain.usecase.itembuild

import com.sandorln.data.repository.item.ItemBuildRepository
import com.sandorln.model.data.item.ItemBuild
import com.sandorln.model.type.ChampionTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveItemBuildTest {

    private class FakeItemBuildRepository : ItemBuildRepository {
        val storage = mutableListOf<ItemBuild>()

        override fun getItemBuildListByVersion(version: String): Flow<List<ItemBuild>> =
            flowOf(storage.filter { it.version == version })

        override fun getItemBuildById(id: Long): Flow<ItemBuild?> =
            flowOf(storage.find { it.id == id })

        override suspend fun getItemBuildCountByVersion(version: String): Int =
            storage.count { it.version == version }

        override suspend fun saveItemBuild(itemBuild: ItemBuild): Long {
            val id = if (itemBuild.id == 0L) (storage.size + 1).toLong() else itemBuild.id
            storage.removeAll { it.id == id }
            storage.add(itemBuild.copy(id = id))
            return id
        }

        override suspend fun deleteItemBuild(id: Long) {
            storage.removeAll { it.id == id }
        }
    }

    @Test
    fun saveNewBuild_under10_success() = runBlocking {
        val repo = FakeItemBuildRepository()
        val useCase = SaveItemBuild(repo)

        val build = ItemBuild(
            id = 0L,
            version = "16.17.1",
            title = "Test Build",
            positionList = listOf(ChampionTag.Tank),
            itemIdList = listOf("1001", "1002")
        )

        val result = useCase(build)
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrThrow())
        assertEquals(1, repo.storage.size)
    }

    @Test
    fun saveNewBuild_reaches10_failsWithMaxCountError() = runBlocking {
        val repo = FakeItemBuildRepository()
        val useCase = SaveItemBuild(repo)

        // Prepopulate 10 builds for version 16.17.1
        repeat(10) { index ->
            repo.storage.add(
                ItemBuild(
                    id = (index + 1).toLong(),
                    version = "16.17.1",
                    title = "Build $index",
                    positionList = listOf(ChampionTag.Fighter),
                    itemIdList = listOf("1001")
                )
            )
        }

        val eleventhBuild = ItemBuild(
            id = 0L,
            version = "16.17.1",
            title = "11th Build",
            positionList = listOf(ChampionTag.Mage),
            itemIdList = listOf("1004")
        )

        val result = useCase(eleventhBuild)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals(10, repo.storage.size)
    }

    @Test
    fun updateExistingBuild_when10BuildsExist_succeeds() = runBlocking {
        val repo = FakeItemBuildRepository()
        val useCase = SaveItemBuild(repo)

        // Prepopulate 10 builds
        repeat(10) { index ->
            repo.storage.add(
                ItemBuild(
                    id = (index + 1).toLong(),
                    version = "16.17.1",
                    title = "Build $index",
                    positionList = listOf(ChampionTag.Fighter),
                    itemIdList = listOf("1001")
                )
            )
        }

        // Updating build with id = 5L
        val updateBuild = ItemBuild(
            id = 5L,
            version = "16.17.1",
            title = "Updated Title",
            positionList = listOf(ChampionTag.Tank),
            itemIdList = listOf("1001", "1002", "1003")
        )

        val result = useCase(updateBuild)
        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrThrow())
        assertEquals("Updated Title", repo.storage.find { it.id == 5L }?.title)
    }
}
