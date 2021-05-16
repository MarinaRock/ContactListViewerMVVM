package ru.marina.contactlistviewermvvm.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.marina.contactlistviewermvvm.R
import ru.marina.contactlistviewermvvm.databinding.ActivityAppBinding
import ru.marina.contactlistviewermvvm.vm.AppViewModel
import javax.inject.Inject

class AppActivity : AppCompatActivity(), HasAndroidInjector {

    private lateinit var binding: ActivityAppBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AppViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val navigator = AppNavigator(this, R.id.fragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            viewModel.startNavigation()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun androidInjector() = dispatchingAndroidInjector
}