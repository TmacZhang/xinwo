package com.xinwo.feed.hot

class DataRepository(private val mock: Mock) {
    fun getStoriesData(): ArrayList<StoriesDataModel>? {
        return mock.loadMockData()
    }
}