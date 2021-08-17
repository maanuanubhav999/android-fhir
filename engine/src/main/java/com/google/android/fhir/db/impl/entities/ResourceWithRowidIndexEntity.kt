package com.google.android.fhir.db.impl.entities

import androidx.room.ColumnInfo


data class ResourceWithRowIdIndexEntity(
  @ColumnInfo(name = "id")val rowId: Long,  //if using rowId there if issue as room returns id
  @ColumnInfo(name = "serializedResource")val serializedResource: String
)
