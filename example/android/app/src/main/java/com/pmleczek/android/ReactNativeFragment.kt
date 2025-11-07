package com.pmleczek.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.pmleczek.expobrownfieldtargetexample.brownfield.ReactNativeViewFactory
import com.pmleczek.expobrownfieldtargetexample.brownfield.RootComponent

class ReactNativeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FrameLayout {
        return ReactNativeViewFactory.createFrameLayout(
            requireContext(),
            requireActivity(),
            RootComponent.Main
        )
    }
}