package com.example.weather.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class Check(private val dispatcher: CoroutineDispatcher){

    suspend fun checkedLoadData(list: List<WeatherDescription>): VerifyResult = withContext(dispatcher){
        when{
            list.isEmpty()->VerifyResult.Error
            else -> VerifyResult.Success
        }
    }

    suspend fun checkedDataForDB(list: List<ExpandableDateModel>): VerifyResult = withContext(dispatcher){
        when{
            list.isEmpty() -> VerifyResult.Error
            else -> VerifyResult.Success
        }
    }

    sealed class VerifyResult {
        object Success : VerifyResult()
        object Error : VerifyResult()
    }
}

