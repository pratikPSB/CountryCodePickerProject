package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
class CustomSizeFragment : Fragment() {

    private lateinit var buttonNext: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_size, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonNext = requireView().findViewById(R.id.button_next)
        buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem += 1 }

        val ccp1: CountryCodePicker = requireView().findViewById(R.id.ccp1)
        val ccp2: CountryCodePicker = requireView().findViewById(R.id.ccp2)
        val ccp3: CountryCodePicker = requireView().findViewById(R.id.ccp3)
        val ccp4: CountryCodePicker = requireView().findViewById(R.id.ccp4)
        val ccp5: CountryCodePicker = requireView().findViewById(R.id.ccp5)
        val ccp6: CountryCodePicker = requireView().findViewById(R.id.ccp6)

        ccp1.setFragmentManager(childFragmentManager)
        ccp2.setFragmentManager(childFragmentManager)
        ccp3.setFragmentManager(childFragmentManager)
        ccp4.setFragmentManager(childFragmentManager)
        ccp5.setFragmentManager(childFragmentManager)
        ccp6.setFragmentManager(childFragmentManager)

    }
}