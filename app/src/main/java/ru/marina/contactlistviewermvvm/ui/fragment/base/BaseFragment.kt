package ru.marina.contactlistviewermvvm.ui.fragment.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import ru.marina.contactlistviewermvvm.R
import ru.marina.contactlistviewermvvm.ui.extensions.findFirstViewByClass
import ru.marina.contactlistviewermvvm.ui.extensions.hideChildrenViews
import ru.marina.contactlistviewermvvm.ui.extensions.showChildrenViews

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    private var progressBarContainer: FrameLayout? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var contentLayout: FrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = (view as ViewGroup?)?.findFirstViewByClass(SwipeRefreshLayout::class)
        contentLayout = view.findViewById(R.id.contentLayout)
    }

    protected fun startProgress() {
        if (swipeRefreshLayout?.isRefreshing == false) {
            contentLayout?.hideChildrenViews()
            swipeRefreshLayout?.isEnabled = false

            val context = context
            if (progressBarContainer != null || context == null) {
                return
            }
            val progressBar = ProgressBar(context)
            progressBar.isIndeterminate = true
            progressBarContainer = FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER
            progressBarContainer?.addView(progressBar, layoutParams)
            contentLayout?.addView(progressBarContainer)
        }
    }

    protected fun stopProgress() {
        if (progressBarContainer != null) {
            contentLayout?.removeView(progressBarContainer)
            progressBarContainer = null

            contentLayout?.showChildrenViews()
            swipeRefreshLayout?.isEnabled = true
        }
        swipeRefreshLayout?.isRefreshing = false
    }

    open fun onBackPressed() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}