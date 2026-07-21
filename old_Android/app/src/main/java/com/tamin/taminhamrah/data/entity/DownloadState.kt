package com.tamin.taminhamrah.data.entity

sealed class DownloadState {
    abstract val value: Int

    object PENDING : DownloadState() {
        override val value: Int = 1
        override fun toString(): String = "Pending"
    }

    object DOWNLOADING : DownloadState() {
        override val value: Int = 2
        override fun toString(): String = "Downloading"
    }

    object PAUSED : DownloadState() {
        override val value: Int = 3
        override fun toString(): String = "Paused"
    }

    object COMPLETED : DownloadState() {
        override val value: Int = 4
        override fun toString(): String = "Completed"
    }
}