package com.example.wanderly

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

object CloudinaryUploader {

    private const val CLOUD_NAME = "dzo0vdcwk"
    private const val API_KEY = "756986145449156"
    private const val API_SECRET = "LpJtZYya7Ko-te4sMm9xHbQ-4AY"

    fun uploadFile(file: File, callback: (Boolean, String?) -> Unit) {
        val timestamp = (System.currentTimeMillis() / 1000).toString()

        val toSign = "timestamp=$timestamp$API_SECRET"
        val signature = sha1(toSign)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .addFormDataPart("api_key", API_KEY)
            .addFormDataPart("timestamp", timestamp)
            .addFormDataPart("signature", signature)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/auto/upload")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    val url = Regex("\"secure_url\":\"(.*?)\"").find(responseBody ?: "")?.groups?.get(1)?.value
                    callback(true, url)
                } else {
                    callback(false, null)
                }
            }
        })
    }

    private fun sha1(input: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-1")
        val result = digest.digest(input.toByteArray())
        return result.joinToString("") { "%02x".format(it) }
    }
}
