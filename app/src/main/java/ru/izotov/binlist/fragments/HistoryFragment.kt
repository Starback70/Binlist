package ru.izotov.binlist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.izotov.binlist.adapters.BinAdapter
import ru.izotov.binlist.databinding.FragmentHistoryBinding
import ru.izotov.binlist.models.BinModel
import ru.izotov.binlist.models.MainViewModel

class HistoryFragment : Fragment(), BinAdapter.Listener {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: BinAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.binDataList.observe(viewLifecycleOwner) {
            initRcView(it)
        }
    }

    private fun initRcView(list: List<BinModel>) = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = BinAdapter(this@HistoryFragment)
        rcView.adapter = adapter
        adapter.submitList(list)
    }

    override fun onClick(item: BinModel) {
        model.liveBinData.value = item
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}