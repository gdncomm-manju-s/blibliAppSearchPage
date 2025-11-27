//package com.example.bliblihomepage.network
//
//import android.content.Context
//import com.example.bliblihomepage.util.AppConfig
//import dagger.hilt.android.qualifiers.ApplicationContext
//import okhttp3.Interceptor
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.Protocol
//import okhttp3.Response
//import okhttp3.ResponseBody.Companion.toResponseBody
//import javax.inject.Inject
//
//class MockJsonInterceptor @Inject constructor(
//    @ApplicationContext private val context: Context
//) : Interceptor {
//
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val req = chain.request()
//        val url = req.url.toString()
//
//        if (url.contains("products")) {
//            val json = context.assets
//                .open("mock_products.json")
//                .bufferedReader()
//                .readText()
//
//            return Response.Builder()
//                .request(req)
//                .protocol(Protocol.HTTP_1_1)
//                .code(200)
//                .message("OK")
//                .body(json.toResponseBody("application/json".toMediaType()))
//                .build()
//        }
//
//        return chain.proceed(req)
//    }
//}
//
