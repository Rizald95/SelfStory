package submission.learning.storyapp.interfaces.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import submission.learning.storyapp.data.response.ListStoryItem
import submission.learning.storyapp.repository.UserRepository
import submission.learning.storyapp.DataDummy
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import submission.learning.storyapp.LiveDataTestUtil.getOrAwaitValue
import submission.learning.storyapp.LogUtil
import submission.learning.storyapp.MainDispatcherRules
import submission.learning.storyapp.interfaces.adapter.ListUserLocationAdapter

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRules()

    @Mock
    private lateinit var userRepository: UserRepository
    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXdBOEpwVmlVLUdxLWd1bXgiLCJpYXQiOjE3MTgyNzk1MTN9.ddX2TbUjOneVOMPT4h7lFt9gFvSsrxmdhlQP8l1RyOE"


    @Test
    fun `when Get Story is Not Null and Return Data Story`() = runTest {
        LogUtil.mockLog()
       val quote = DataDummy.generateDummyStoryAppResponse()
        val data: PagingData<ListStoryItem> = StoryPagingDataSource.snapshot(quote)
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito. `when`(userRepository.getStoriesLocation(token)).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(userRepository)
        val storyApp: PagingData<ListStoryItem> = mainViewModel.listStoryLocation(token).getOrAwaitValue()

        val differData = AsyncPagingDataDiffer(
            diffCallback = ListUserLocationAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differData.submitData(storyApp)

        assertNotNull(differData.snapshot())
        assertEquals(quote.size, differData.snapshot().size)
        assertEquals(quote[0], differData.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        LogUtil.mockLog()
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito. `when`(userRepository.getStoriesLocation(token)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(userRepository)
        val realStory: PagingData<ListStoryItem> = mainViewModel.listStoryLocation(token).getOrAwaitValue()

        val differData = AsyncPagingDataDiffer(
            diffCallback = ListUserLocationAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differData.submitData(realStory)

        assertEquals(0, differData.snapshot().size)

    }
}

class StoryPagingDataSource : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
    }

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
