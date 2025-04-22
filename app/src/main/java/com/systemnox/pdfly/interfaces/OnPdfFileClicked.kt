package com.systemnox.pdfly.interfaces

import com.systemnox.pdfly.dataClasses.PdfFile

interface OnPdfFileClicked {
    fun onPdfFileRenamed(pdfFile: PdfFile, newName: String)
    fun onPdfFileDeleted(pdfFile: PdfFile)
}