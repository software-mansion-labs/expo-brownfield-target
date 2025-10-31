package com.pmleczek.expobrownfieldtargetexample.brownfield

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * A Fragment that hosts a React Native view.
 */
class ReactNativeFragment : Fragment() {
    
    private var moduleName: String = "main"
    private var initialProps: Bundle? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get module name and props from arguments if provided
        arguments?.let {
            moduleName = it.getString(ARG_MODULE_NAME, "main")
            initialProps = it.getBundle(ARG_INITIAL_PROPS)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return context?.let { ctx ->
            ReactNativeViewFactory.createFrameLayout(
                context = ctx,
                activity = requireActivity(),
                moduleName = moduleName,
                initialProps = initialProps
            )
        }
    }
    
    companion object {
        private const val ARG_MODULE_NAME = "moduleName"
        private const val ARG_INITIAL_PROPS = "initialProps"
        
        /**
         * Create a new instance of ReactNativeFragment.
         * 
         * @param moduleName The React Native component to render
         * @param initialProps Optional properties to pass to the component
         */
        fun newInstance(moduleName: String = "main", initialProps: Bundle? = null): ReactNativeFragment {
            return ReactNativeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODULE_NAME, moduleName)
                    initialProps?.let { putBundle(ARG_INITIAL_PROPS, it) }
                }
            }
        }
    }
}
