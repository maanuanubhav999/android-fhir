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
import com.google.android.fhir.db.impl.entities.ResourceWithRowIdIndexEntity
import com.google.gson.Gson
import java.util.TreeSet
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.smartregister.p2p.model.DataType
import org.smartregister.p2p.model.dao.SenderTransferDao
import org.smartregister.p2p.sync.data.JsonData
import org.smartregister.p2p.sync.data.MultiMediaData

class MySenderDao(applicationContext: Context) : SenderTransferDao {
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(applicationContext)
  private val iParser: IParser = FhirContext.forR4().newJsonParser()

  override fun getDataTypes(): TreeSet<DataType>? {
    val dataTypes: TreeSet<DataType> = TreeSet()
    dataTypes.add(DataType("resourceEntityType", DataType.Type.NON_MEDIA, 0))
    return dataTypes
  }

  override fun getJsonData(dataType: DataType, lastRecordId: Long, batchSize: Int): JsonData? {
    val resourceFromDataBase = runBlocking {
      fhirEngine.getRecordsLastRecordId(lastRecordId, batchSize)
    }
    val size = resourceFromDataBase.size
    Log.d("testing", lastRecordId.toString())

    return if (dataType.name.equals("resourceEntityType")) {
      if (size == 0) {
        null
      } else {
        val jsonArray = JSONArray()
        // just get the resource and convert them to json array
        for (i in 0 until size) {
          val singleData: ResourceWithRowIdIndexEntity = resourceFromDataBase[(i).toInt()]
          val jsonData = Gson().toJson(singleData)
          jsonArray.put(jsonData)
        }
        JsonData(jsonArray, resourceFromDataBase[size-1].rowId)
      }
    } else {
      null
    }
  }

  override fun getMultiMediaData(p0: DataType, p1: Long): MultiMediaData? {
    TODO("Not yet implemented")
  }
}
