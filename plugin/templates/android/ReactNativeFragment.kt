package ${{packageId}}

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

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