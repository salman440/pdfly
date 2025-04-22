package com.systemnox.pdfly.appClasses

import com.systemnox.pdfly.interfaces.PdfFileRepository
import com.systemnox.pdfly.repoModels.PdfFileRepositoryImpl
import com.systemnox.pdfly.repoModels.PermissionRepository
import com.systemnox.pdfly.vms.PdfListViewModel
import com.systemnox.pdfly.vms.PermissionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Singleton for PermissionRepository
    single { PermissionRepository(androidContext()) }

    // ViewModel for Permission
    viewModel { PermissionViewModel(get()) }

    single<PdfFileRepository> { PdfFileRepositoryImpl(androidContext()) }

    viewModel { PdfListViewModel(get()) }
}

