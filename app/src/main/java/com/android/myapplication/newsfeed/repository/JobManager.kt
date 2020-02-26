package com.android.myapplication.newsfeed.repository

import android.util.Log
import kotlinx.coroutines.Job

/*
* Reference:  https://github.com/mitchtabian/Open-API-Android-App/blob/Job-Manager-and-Cancelling-Jobs/app/src/main/java/com/codingwithmitch/openapi/repository/JobManager.kt
* */
open class JobManager(
    private val className: String
) {

    private val TAG: String = "AppDebug"

    private val jobs: HashMap<String, Job> = HashMap()

    fun addJob(methodName: String, job: Job){
        cancelJob(methodName) //cancel previous job before adding a new one
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String){
        getJob(methodName)?.cancel()
    }

    fun getJob(methodName: String): Job? {
        if(jobs.containsKey(methodName)){
            jobs[methodName]?.let {
                return it
            }
        }
        return null
    }

    fun cancelActiveJobs(){
        for((methodName, job) in jobs){
            if(job.isActive){
                Log.e(TAG, "$className: cancelling job in method: '$methodName'")
                job.cancel()
            }
        }
    }
}
