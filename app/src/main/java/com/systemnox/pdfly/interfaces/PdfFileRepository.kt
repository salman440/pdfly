package com.systemnox.pdfly.interfaces

import com.systemnox.pdfly.dataClasses.PdfFile

interface PdfFileRepository {
    suspend fun getAllPdfFiles(): List<PdfFile>
    suspend fun deletePdfFile(pdfFile: PdfFile): Boolean
    suspend fun renamePdfFile(pdfFile: PdfFile, newName: String): PdfFile?

}