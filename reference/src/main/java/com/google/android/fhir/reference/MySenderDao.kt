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
import android.util.Log
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import java.util.TreeSet
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.json.JSONArray
import org.smartregister.p2p.model.DataType
import org.smartregister.p2p.model.dao.SenderTransferDao
import org.smartregister.p2p.sync.data.JsonData
import org.smartregister.p2p.sync.data.MultiMediaData

class MySenderDao(applicationContext: Context) : SenderTransferDao {
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(applicationContext)

  private fun searchresult(): List<Patient> = runBlocking { fhirEngine.search<Patient> {} }
  private val allPatientsList: MutableList<String> = mutableListOf()
  private val iParser: IParser = FhirContext.forR4().newJsonParser()
  val somedummyresult =
    searchresult().forEach { allPatientsList.add(iParser.encodeResourceToString(it)) }

  override fun getDataTypes(): TreeSet<DataType>? {
    val dataTypes: TreeSet<DataType> = TreeSet()
    dataTypes.add(DataType("resourceType", DataType.Type.NON_MEDIA, 0))
    return dataTypes
  }

  override fun getJsonData(dataType: DataType, lastRecordId: Long, batchSize: Int): JsonData? {

    return if (dataType.name.equals("resourceType")) {
      val jsonArray = JSONArray()
      if (lastRecordId >= allPatientsList.size) {
        null
      } else {
        var recordsAdded = 0
        for (i in 0 until batchSize) {
          if (lastRecordId + i >= allPatientsList.size) {
            break
          }
          val allPatientData: String = allPatientsList[((lastRecordId + i).toInt())]
          jsonArray.put(allPatientData)
          recordsAdded++
          Log.d("testing", "some record added ")
        }
        JsonData(jsonArray, lastRecordId + recordsAdded)
      }
    } else {
      null
    }
  }

  override fun getMultiMediaData(p0: DataType, p1: Long): MultiMediaData? {
    TODO("Not yet implemented")
  }
}
