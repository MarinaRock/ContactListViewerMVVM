package ru.marina.contactlistviewermvvm.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import ru.marina.contactlistviewermvvm.App
import ru.marina.contactlistviewermvvm.data.di.DataModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        ActivityModule::class,
        NavigationModule::class,
        DataModule::class,
        AndroidInjectionModule::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}