package io.github.kotools.csv

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

private fun <T> assertIsValid(
    block: suspend CoroutineScope.() -> Collection<T>?
) {
    runBlocking {
        val result: Collection<T>? = block()
        result.assertNotNull()
        result?.let { it.size assertNotEquals 0 }
    }
}

private fun <C : Collection<T>?, T> awaitAndAssertIsValid(
    asyncBlock: CoroutineScope.() -> Deferred<C>
) {
    assertIsValid {
        asyncBlock()
            .await()
    }
}

@Suppress("unused")
class InvalidExample(val first: String, val second: Int, val third: Boolean)

class ReaderTest {
    private val validConfiguration: Reader.() -> Unit by lazy {
        {
            file = "test"
            folder = "folder"
            separator = comma
        }
    }

    @Nested
    inner class CsvReader {
        @Test
        fun `should pass`(): Unit =
            assertIsValid { csvReader(validConfiguration) }

        @Test
        fun `should fail with blank file name`(): Unit = runBlocking {
            assertFailsWith<CsvException> {
                csvReader { }
            }
        }

        @Test
        fun `should fail with unknown file`(): Unit = runBlocking {
            assertFailsWith<CsvException> {
                csvReader { file = "unknown" }
            }
        }
    }

    @Nested
    inner class CsvReaderAs {
        @Test
        fun `should pass`(): Unit =
            assertIsValid { csvReaderAs<Example>(validConfiguration) }

        @Test
        fun `should fail with blank file name`(): Unit = runBlocking {
            assertFailsWith<CsvException> {
                csvReaderAs<Example> {}
            }
        }

        @Test
        fun `should fail with invalid type`(): Unit = runBlocking {
            assertFailsWith<CsvException> {
                csvReaderAs<InvalidExample>(validConfiguration)
            }
            assertFailsWith<CsvException> {
                csvReaderAs<PrivateExample>(validConfiguration)
            }
        }

        @Test
        fun `should fail with unknown file`(): Unit = runBlocking {
            assertFailsWith<CsvException> {
                csvReaderAs<Example> { file = "unknown" }
            }
        }
    }

    @Nested
    inner class CsvReaderAsAsync {
        @Test
        fun `should pass`(): Unit = awaitAndAssertIsValid {
            csvReaderAsAsync<Example>(validConfiguration)
        }
    }

    @Nested
    inner class CsvReaderAsOrNull {
        @Test
        fun `should pass`(): Unit =
            assertIsValid { csvReaderAsOrNull<Example>(validConfiguration) }

        @Test
        fun `should fail with blank file name`(): Unit = runBlocking {
            assertNull {
                csvReaderAsOrNull<Example> {}
            }
        }

        @Test
        fun `should fail with invalid type`(): Unit = runBlocking {
            assertNull { csvReaderAsOrNull<InvalidExample>(validConfiguration) }
            assertNull { csvReaderAsOrNull<PrivateExample>(validConfiguration) }
        }

        @Test
        fun `should fail with unknown file`(): Unit = runBlocking {
            assertNull {
                csvReaderAsOrNull<Example> { file = "unknown" }
            }
        }
    }

    @Nested
    inner class CsvReaderAsOrNullAsync {
        @Test
        fun `should pass`(): Unit = awaitAndAssertIsValid {
            csvReaderAsOrNullAsync<Example>(validConfiguration)
        }
    }

    @Nested
    inner class CsvReaderAsync {
        @Test
        fun `should pass`(): Unit =
            awaitAndAssertIsValid { csvReaderAsync(validConfiguration) }
    }

    @Nested
    inner class CsvReaderOrNull {
        @Test
        fun `should pass`(): Unit =
            assertIsValid { csvReaderOrNull(validConfiguration) }

        @Test
        fun `should fail with blank file name`(): Unit = runBlocking {
            assertNull {
                csvReaderOrNull { }
            }
        }

        @Test
        fun `should fail with unknown file`(): Unit = runBlocking {
            assertNull {
                csvReaderOrNull { file = "unknown" }
            }
        }
    }

    @Nested
    inner class CsvReaderOrNullAsync {
        @Test
        fun `should pass`(): Unit =
            awaitAndAssertIsValid { csvReaderOrNullAsync(validConfiguration) }
    }
}

data class Example(val first: String, val second: Int, val third: Boolean)

private data class PrivateExample(
    val first: String,
    val second: Int,
    val third: Boolean
)
