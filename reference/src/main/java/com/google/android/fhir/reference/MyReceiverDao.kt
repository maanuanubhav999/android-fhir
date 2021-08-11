/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.reference

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import java.io.File
import java.util.TreeSet
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.json.JSONArray
import org.smartregister.p2p.model.DataType
import org.smartregister.p2p.model.dao.ReceiverTransferDao
import timber.log.Timber

class MyReceiverDao(applicationContext: Context) : ReceiverTransferDao {
  private val lastReceived: HashMap<String, Long> = HashMap()
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(applicationContext)

  override fun getDataTypes(): TreeSet<DataType> {
    val dataTypes: TreeSet<DataType> = TreeSet()
    dataTypes.add(DataType("resourceType", DataType.Type.NON_MEDIA, 0))
    return dataTypes
  }

  override fun receiveJson(type: DataType, jsonArray: JSONArray): Long {
    Timber.e("Received records %s of type %s", jsonArray.length().toString(), type.getName())

    var lastId: Long? = lastReceived.get(type.getName())

    lastId = lastId ?: 0L

    val finalLastId: Long = lastId + jsonArray.length()

    lastReceived.put(type.getName(), finalLastId)

    Timber.e("Last record id of received records %s is %s", type.getName(), finalLastId.toString())
    insertPatientRecordToDatabase(jsonArray)
    return finalLastId
  }

  override fun receiveMultimedia(
    p0: DataType,
    p1: File,
    p2: HashMap<String, Any>?,
    p3: Long
  ): Long {
    TODO("Not yet implemented")
  }

  private fun insertPatientRecordToDatabase(jsonArray: JSONArray) {
    val iParser: IParser = FhirContext.forR4().newJsonParser()
    for (i in 0 until jsonArray.length()) {
      var temp: String = jsonArray[i].toString()
      val parsed: Patient = iParser.parseResource(Patient::class.java, temp)
      runBlocking { fhirEngine.save(parsed) }
    }
    return
  }
}
