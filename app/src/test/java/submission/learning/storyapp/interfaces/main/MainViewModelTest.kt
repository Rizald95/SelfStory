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
import submission.learning.storyapp.interfaces.adapter.ListUserLocationAdapter

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXdBOEpwVmlVLUdxLWd1bXgiLCJpYXQiOjE3MTgyNzk1MTN9.ddX2TbUjOneVOMPT4h7lFt9gFvSsrxmdhlQP8l1RyOE"
    private val dummyStoryApp = DataDummy.generateDummyStoryAppResponse()

    @Test
    fun `when Get Story is Not Null and Return Data Story`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingDataSource.snapShot(dummyStoryApp)
        val expectedStoryApp = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStoryApp.value = data

        Mockito.`when`(userRepository.getStoriesLocation(token)).thenReturn(expectedStoryApp)
        val mainViewModel = MainViewModel(userRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.listStoryLocation(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListUserLocationAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStoryApp.size, differ.snapshot().size)
        assertEquals(dummyStoryApp[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStoryApp = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStoryApp.value = data

        Mockito.`when`(userRepository.getStoriesLocation(token)).thenReturn(expectedStoryApp)
        val mainViewModel = MainViewModel(userRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.listStoryLocation(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListUserLocationAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
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
        fun snapShot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
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
