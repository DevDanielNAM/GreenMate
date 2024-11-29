package com.android.greenmate.presentation.ui.components

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast


fun deleteImage(context: Context, contentUri: String) {
    val uri = Uri.parse(contentUri)
    val contentResolver: ContentResolver = context.contentResolver

    try {
        // 이미지 삭제
        val rowsDeleted = contentResolver.delete(uri, null, null)
        if (rowsDeleted > 0) {
            // 삭제 성공
            println("이미지가 성공적으로 삭제되었습니다.")
            Toast.makeText(context, "사진이 삭제되었어요", Toast.LENGTH_SHORT).show()
        } else {
            // 삭제 실패
            println("이미지 삭제에 실패했습니다.")
            Toast.makeText(context, "사진 삭제를 실패했어요", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        // 예외 처리
        Toast.makeText(context, "사진이 삭제 중 오류가발생했어요", Toast.LENGTH_SHORT).show()
        println("삭제 중 오류 발생: ${e.message}")
    }
}