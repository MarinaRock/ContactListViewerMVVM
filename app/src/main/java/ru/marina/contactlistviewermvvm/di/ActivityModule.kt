package ru.marina.contactlistviewermvvm.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.marina.contactlistviewermvvm.ui.activity.AppActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAppActivity(): AppActivity
}