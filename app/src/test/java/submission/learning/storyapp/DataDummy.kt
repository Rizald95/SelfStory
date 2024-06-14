package submission.learning.storyapp

import submission.learning.storyapp.data.response.ListStoryItem

object DataDummy {
    fun  generateDummyStoryAppResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyApp = ListStoryItem(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "rizal",
                description = "halo yak guys",
                photoUrl = "this photo",
                createdAt = "2024-06-13T11:04:54",
                lat = 34.0522,
                lon = -118.2437
            )
            items.add(storyApp)
        }

        return items
    }

}