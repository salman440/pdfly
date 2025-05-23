package com.systemnox.pdfly.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.systemnox.pdfly.R
import com.systemnox.pdfly.activities.AppSettings
import com.systemnox.pdfly.adapter.PdfAdapter
import com.systemnox.pdfly.customView.CustomListGridSwitchView
import com.systemnox.pdfly.dataClasses.PdfFile
import com.systemnox.pdfly.database.FavoriteDBHelper
import com.systemnox.pdfly.database.RecentDBHelper
import com.systemnox.pdfly.databinding.BottomSheetSortingBinding
import com.systemnox.pdfly.databinding.FragmentFilesBinding
import com.systemnox.pdfly.interfaces.OnPdfFileClicked
import com.systemnox.pdfly.vms.PdfListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.activityViewModel

@SuppressLint("NotifyDataSetChanged")
class FilesFragment : Fragment() {


    private val binding: FragmentFilesBinding by lazy {
        FragmentFilesBinding.inflate(layoutInflater)
    }

    private val pdfListViewModel: PdfListViewModel by activityViewModel()
    private lateinit var pdfAdapter: PdfAdapter

    private lateinit var recentDBHelper: RecentDBHelper
    private lateinit var favoriteDBHelper: FavoriteDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsImageView.setOnClickListener {
            startActivity(Intent(requireContext(), AppSettings::class.java))
        }

        recentDBHelper = RecentDBHelper(requireContext())
        favoriteDBHelper = FavoriteDBHelper(requireContext())

        pdfAdapter =
            PdfAdapter(mutableListOf(), false, binding.recyclerView, object : OnPdfFileClicked {
                override fun onPdfFileRenamed(pdfFile: PdfFile, newName: String) {
                    pdfListViewModel.renamePdfFile(pdfFile, newName)

                    if (recentDBHelper.checkIfExists(pdfFile.path)){
                        recentDBHelper.deleteRecentItem(pdfFile.path)
                    }

                    if (favoriteDBHelper.checkIfExists(pdfFile.path)){
                        favoriteDBHelper.deleteFavorite(pdfFile.path)
                    }

                }

                override fun onPdfFileDeleted(pdfFile: PdfFile) {
                    pdfListViewModel.deletePdfFile(pdfFile)

                    if (recentDBHelper.checkIfExists(pdfFile.path)){
                        recentDBHelper.deleteRecentItem(pdfFile.path)
                    }

                    if (favoriteDBHelper.checkIfExists(pdfFile.path)){
                        favoriteDBHelper.deleteFavorite(pdfFile.path)
                    }

                }

            })

        if (binding.switchView.getCurrentMode() == CustomListGridSwitchView.SwitchMode.GRID) {
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            pdfAdapter.isGridView = true
        } else {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            pdfAdapter.isGridView = false
        }
        binding.recyclerView.adapter = pdfAdapter

        pdfListViewModel.pdfFiles.observe(viewLifecycleOwner) { pdfFiles ->

            val sortedList = pdfListViewModel.pdfFiles.value?.sortedWith(PdfFile.SortByName)
            sortedList?.let {
                pdfAdapter.updatePdfFiles(it)
            }

            if (pdfFiles.isEmpty()) {
                binding.noFileLayout.visibility = View.VISIBLE
            } else {
                binding.noFileLayout.visibility = View.GONE
            }

        }

        binding.switchView.setListener {
            when (it) {
                CustomListGridSwitchView.SwitchMode.GRID -> {
                    binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                    pdfAdapter.isGridView = true
                    pdfAdapter.notifyDataSetChanged()
                }

                CustomListGridSwitchView.SwitchMode.LIST -> {
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    pdfAdapter.isGridView = false
                    pdfAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.switchView.shouldRememberState(true)


        binding.sortBy.setOnClickListener {
            showSortingDialog()
        }


    }

    private fun showSortingDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sortingBinding: BottomSheetSortingBinding = BottomSheetSortingBinding.inflate(
            layoutInflater
        )
        bottomSheetDialog.setContentView(sortingBinding.root)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        sortingBinding.tvName.setOnClickListener {
            binding.sortBy.text = getString(R.string.name)
            sortList(PdfFile.SortByName)
            bottomSheetDialog.dismiss()
        }

        sortingBinding.tvDate.setOnClickListener {
            binding.sortBy.text = getString(R.string.date)
            sortList(PdfFile.SortByDate)
            bottomSheetDialog.dismiss()
        }

        sortingBinding.tvSize.setOnClickListener {
            binding.sortBy.text = getString(R.string.size)
            sortList(PdfFile.SortBySize)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }


    private fun sortList(comparator: Comparator<PdfFile>) {
        val sortedList = pdfListViewModel.pdfFiles.value?.sortedWith(comparator)
        sortedList?.let {
            pdfAdapter.updatePdfFiles(it)
        }
    }


    override fun onResume() {
        super.onResume()

        if (binding.switchView.getCurrentMode() != binding.switchView.getSavedMode()) {
            if (binding.switchView.getSavedMode() == CustomListGridSwitchView.SwitchMode.GRID) {
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                pdfAdapter.isGridView = true
                pdfAdapter.notifyDataSetChanged()
                binding.switchView.setMode(CustomListGridSwitchView.SwitchMode.GRID)

            } else {
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                pdfAdapter.isGridView = false
                pdfAdapter.notifyDataSetChanged()
                binding.switchView.setMode(CustomListGridSwitchView.SwitchMode.LIST)

            }
        }


    }

}