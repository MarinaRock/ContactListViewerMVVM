package ru.marina.contactlistviewermvvm.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

    private val cicerone = Cicerone.create()

    @Provides
    fun provideRouter(): Router = cicerone.router

    @Provides
    fun provideNavigatorHolder(): NavigatorHolder = cicerone.getNavigatorHolder()
}