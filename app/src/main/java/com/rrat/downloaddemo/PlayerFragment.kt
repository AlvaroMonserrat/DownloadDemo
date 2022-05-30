package com.rrat.downloaddemo

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.rrat.downloaddemo.databinding.FragmentPlayerBinding
import com.rrat.downloaddemo.utils.Constants
import java.io.File

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackDownload.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_downloadFragment)
        }

        binding.btnPlay.setOnClickListener { playSound() }
    }

    private fun playSound(){
        val contextWrapper = ContextWrapper(context)
        val file = File(contextWrapper.getDir(Constants.DIRECTORIO_AUDIO_DICCIONARIO, Context.MODE_PRIVATE), "ADN.mp3")
        val mediaPlayer = MediaPlayer.create(context, file.toUri())
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}