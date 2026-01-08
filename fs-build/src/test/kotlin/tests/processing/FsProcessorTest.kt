package tests.processing

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import space.sunqian.fs.build.processing.FsProcessor

@DisplayName("FsProcessor Unit Tests")
class FsProcessorTest {

    private lateinit var processor: FsProcessor

    @BeforeEach
    fun setUp() {
        processor = FsProcessor()
    }

    @Test
    fun `should create FsProcessor instance`() {
        assertNotNull(processor)
        assertTrue(processor is FsProcessor)
    }
}