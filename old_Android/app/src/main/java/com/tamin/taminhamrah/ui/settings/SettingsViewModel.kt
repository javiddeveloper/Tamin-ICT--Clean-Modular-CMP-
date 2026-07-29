package com.tamin.taminhamrah.ui.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : BaseViewModel() {}

