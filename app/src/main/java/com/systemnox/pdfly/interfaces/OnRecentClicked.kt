package com.systemnox.pdfly.interfaces

import com.systemnox.pdfly.dataClasses.RecentModel

interface OnRecentClicked {
    fun onFavorite(recentModel: RecentModel)
    fun onRename(recentModel: RecentModel, newName: String)
    fun onRemoveFromRecent(recentModel: RecentModel)
    fun onDeleted(recentModel: RecentModel)
}