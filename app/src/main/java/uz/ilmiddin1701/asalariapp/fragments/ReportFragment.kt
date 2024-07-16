package uz.ilmiddin1701.asalariapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.ilmiddin1701.asalariapp.databinding.FragmentReportBinding

class ReportFragment : Fragment() {
    private val binding by lazy { FragmentReportBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.apply {

        }
        return binding.root
    }
}