package com.rrat.downloaddemo

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rrat.downloaddemo.databinding.FragmentDownloadBinding
import com.rrat.downloaddemo.utils.Constants
import com.rrat.downloaddemo.viewmodel.DownLoadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadFragment : Fragment() {

    private lateinit var binding: FragmentDownloadBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var downLoadViewModel: DownLoadViewModel
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FRAGMENT", "ON CREATE")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("FRAGMENT", "ON CREATEVIEW")
        binding = FragmentDownloadBinding.inflate(inflater, container, false)

        downLoadViewModel = ViewModelProvider(this).get(DownLoadViewModel::class.java)

        binding.tvAudioFiles.text = resources.getString(R.string.no_data)
        binding.btnDownload.setOnClickListener { view -> downloadMultiplesFiles(view) }


        downLoadViewModel.addForDownload(Constants.URL_RECURSOS, Constants.DIRECTORIO_RECURSOS)
        downLoadViewModel.addForDownload(Constants.URL_IMAGENES_DICCIONARIO, Constants.DIRECTORIO_IMAGENES_DICCIONARIO)
        downLoadViewModel.addForDownload(Constants.URL_IMAGENES_ASESOR, Constants.DIRECTORIO_IMAGENES_ASESOR)
        downLoadViewModel.addForDownload(Constants.URL_RECURSOS_AUDIO, Constants.DIRECTORIO_RECURSOS_AUDIO)
        downLoadViewModel.addForDownload(Constants.URL_AUDIO_DICCIONARIO, Constants.DIRECTORIO_AUDIO_DICCIONARIO)
        downLoadViewModel.addForDownload(Constants.URL_AUDIO_ASESOR, Constants.DIRECTORIO_AUDIO_ASESOR)

        progressDialog = ProgressDialog(activity)
        observeDownloadUrl()

        return binding.root
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DownloadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DownloadFragment().apply {
                arguments = Bundle().apply {
                    Log.i("FRAGMENT", "FRAGMENT INSTANCE")
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}