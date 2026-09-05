package com.example.rail_aeroline.report

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.rail_aeroline.data.model.OheData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun generatePdfReport(data: List<OheData>): File? {
        val fileName = "TDMS_REPORT_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
        val reportFile = File(context.getExternalFilesDir(null), fileName)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        paint.textSize = 20f
        paint.color = Color.BLACK
        canvas.drawText("Rail AeroLine - OHE Parameter Report", 50f, 50f, paint)

        paint.textSize = 12f
        var yPos = 100f
        canvas.drawText("Timestamp | Height | Stagger | Implantation | Cant | GPS", 50f, yPos, paint)
        yPos += 20f

        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        data.take(30).forEach { item -> // Limit to 30 for one page demo
            val line = "${sdf.format(Date(item.timestamp))} | ${item.height}m | ${item.stagger}mm | ${item.implantation}m | ${item.cant}mm"
            canvas.drawText(line, 50f, yPos, paint)
            yPos += 15f
        }

        pdfDocument.finishPage(page)

        return try {
            pdfDocument.writeTo(FileOutputStream(reportFile))
            pdfDocument.close()
            reportFile
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    fun generateExcelReport(data: List<OheData>): File? {
        val fileName = "TDMS_REPORT_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val reportFile = File(context.getExternalFilesDir(null), fileName)

        return try {
            val writer = FileWriter(reportFile)
            writer.append("Timestamp,Height(m),Stagger(mm),Implantation(m),Cant(mm),Latitude,Longitude\n")
            
            data.forEach { item ->
                writer.append("${item.timestamp},${item.height},${item.stagger},${item.implantation},${item.cant},${item.latitude},${item.longitude}\n")
            }
            
            writer.flush()
            writer.close()
            reportFile
        } catch (e: Exception) {
            null
        }
    }
}
