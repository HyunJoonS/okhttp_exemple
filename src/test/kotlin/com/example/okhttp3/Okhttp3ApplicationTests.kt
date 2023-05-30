package com.example.okhttp3

import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Okhttp3ApplicationTests {

    @Value("\${openAPI-admin-key}")
    private val openAPI_admin_key: String? = null

    @Test
    fun openAPI_admin_key_print(){
        println("key:$openAPI_admin_key")
    }

    @Test
    fun okHttpForOpenAPI() {
        val client = OkHttpClient()

		val API_KEY = openAPI_admin_key // 국세청 공공기관 데이터 open API 키 발급 받아서 입력
		val apiRequestUrl = "https://api.odcloud.kr/api/nts-businessman/v1/status"
        val bizNum = "2768801550"
        val requestData = mapOf("b_no" to listOf(bizNum))
		val params = mapOf("serviceKey" to API_KEY)

        //요청 body 생성
        val mediaType = "application/json".toMediaType()
		val requestBody = Gson().toJson(requestData).toRequestBody(mediaType)

        //url 생성
		val urlBuilder = apiRequestUrl.toHttpUrlOrNull()?.newBuilder()
		params.forEach { (key, value) ->
            urlBuilder?.addEncodedQueryParameter(key, value) //이 URL의 쿼리 문자열에 미리 인코딩된 쿼리 매개변수를 추가합니다.
            //urlBuilder?.addQueryParameter(key, value)
		}
		val requestUrl = urlBuilder?.build().toString()

        //request 생성
        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .addHeader("Content-Type", mediaType.toString())
            .build()

        //response 생성
        val response = client.newCall(request).execute()

        //응답 값 string으로 가져오기
        val responseBody = response.body?.string()
        println("Response Data: $responseBody")

        //응답 값 객체로 변환
        val gson = Gson()
        val responseData = gson.fromJson(responseBody, ApiResponse::class.java)
        val requestCnt = responseData.request_cnt
        println("request_cnt: $requestCnt")
    }

    data class ApiResponse(
        val request_cnt: Int,
        val match_cnt: Int,
        val status_code: String,
        val data: List<Data>
    )

    data class Data(
        val b_no: String,
        val b_stt: String,
        val b_stt_cd: String,
        val tax_type: String,
        val tax_type_cd: String,
        val end_dt: String,
        val utcc_yn: String,
        val tax_type_change_dt: String,
        val invoice_apply_dt: String
    )
}
