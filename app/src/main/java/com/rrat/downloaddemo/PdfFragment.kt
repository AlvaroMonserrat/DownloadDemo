package com.rrat.downloaddemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rrat.downloaddemo.databinding.FragmentDownloadBinding
import com.rrat.downloaddemo.databinding.FragmentPdfBinding


class PdfFragment : Fragment() {

    private lateinit var binding: FragmentPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackDownload.setOnClickListener {
            findNavController().navigate(R.id.action_pdfFragment_to_downloadFragment)
        }

        setupPdfReader()

    }

    private fun setupPdfReader()
    {
        binding.pdfView.fromAsset("sample.pdf")
            .load()
    }
}