package com.rrat.downloaddemo

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rrat.downloaddemo.databinding.FragmentDownloadBinding
import com.rrat.downloaddemo.utils.Constants
import com.rrat.downloaddemo.viewmodel.DownLoadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadFragment : Fragment() {

    private lateinit var binding: FragmentDownloadBinding
    private lateinit var downLoadViewModel: DownLoadViewModel
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FRAGMENT", "ON CREATE")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("FRAGMENT", "ON CREATEVIEW")

        binding = FragmentDownloadBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("FRAGMENT", "ON VIEW CREATED")
        downLoadViewModel = ViewModelProvider(this).get(DownLoadViewModel::class.java)
        downLoadViewModel.addForDownload(Constants.URL_RECURSOS, Constants.DIRECTORIO_RECURSOS)
        downLoadViewModel.addForDownload(Constants.URL_IMAGENES_DICCIONARIO, Constants.DIRECTORIO_IMAGENES_DICCIONARIO)
        downLoadViewModel.addForDownload(Constants.URL_IMAGENES_ASESOR, Constants.DIRECTORIO_IMAGENES_ASESOR)
        downLoadViewModel.addForDownload(Constants.URL_RECURSOS_AUDIO, Constants.DIRECTORIO_RECURSOS_AUDIO)
        downLoadViewModel.addForDownload(Constants.URL_AUDIO_DICCIONARIO, Constants.DIRECTORIO_AUDIO_DICCIONARIO)
        downLoadViewModel.addForDownload(Constants.URL_AUDIO_ASESOR, Constants.DIRECTORIO_AUDIO_ASESOR)

        binding.tvAudioFiles.text = resources.getString(R.string.no_data)
        binding.btnDownload.setOnClickListener { v -> downloadMultiplesFiles(v) }

        progressDialog = ProgressDialog(activity)
        observeDownloadUrl()

        binding.btnPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_downloadFragment_to_playerFragment)
        }

    }

    private fun downloadMultiplesFiles(view: View)
    {
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            downLoadViewModel.startDownload()
        }
        //downLoadViewModel.getResponse("https://agroecology.cl/audio_diccionario/")
        Snackbar.make(view, "Downloading...", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()

    }

    private fun observeDownloadUrl()
    {

        //SUCCESSFUL
        downLoadViewModel.isUrlLoaded.observe(viewLifecycleOwner)
        {
            isUrlLoaded->
            if(isUrlLoaded)
            {

                //progressDialog.dismiss()
                //binding.tvAudioFiles.text = downLoadViewModel.listName.toString()

                //DOWNLOAD FILES
                CoroutineScope(Dispatchers.IO).launch {
                    context?.let { context-> downLoadViewModel.downloadBatch(context) }
                }

            }
        }

        //ERROR
        downLoadViewModel.isUrlFailed.observe(viewLifecycleOwner)
        {
                isUrlFailed->
            if(isUrlFailed)
            {
                Log.i("DOWNLOAD", "DISMISS FAILED")
                progressDialog.dismiss()
                binding.tvAudioFiles.text = getString(R.string.error_http)
            }
        }

        downLoadViewModel.isBatchLoaded.observe(viewLifecycleOwner)
        {
                isBatchLoaded->
            if(isBatchLoaded)
            {
                Log.i("DOWNLOAD", "DISMISS SUCCESS")
                progressDialog.dismiss()
            }
        }

    }

}