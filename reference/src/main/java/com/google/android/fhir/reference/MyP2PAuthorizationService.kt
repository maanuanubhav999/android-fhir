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

import org.smartregister.p2p.authorizer.P2PAuthorizationService

class MyP2PAuthorizationService : P2PAuthorizationService {
  override fun authorizeConnection(
    authorizationDetails: MutableMap<String, Any>,
    authorizationCallback: P2PAuthorizationService.AuthorizationCallback
  ) {
    val appVersion = authorizationDetails.get("app-version")
    val appType = authorizationDetails.get("app-type")

    // Check if appVersion is an int
    if (appVersion != null &&
        appVersion is Double &&
        appVersion as Double >= 9.0 &&
        appType != null &&
        appType is String &&
        appType == "normal-user"
    ) {
      authorizationCallback.onConnectionAuthorized()
    } else {
      authorizationCallback.onConnectionAuthorizationRejected(
        "App version or app type is incorrect"
      )
    }
  }

  override fun getAuthorizationDetails(
    onAuthorizationDetailsProvidedCallback:
      P2PAuthorizationService.OnAuthorizationDetailsProvidedCallback
  ) {
    val authorizationDetails: HashMap<String, Any> = HashMap()
    authorizationDetails["app-version"] = 9
    authorizationDetails["app-type"] = "normal-user"

    onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails)
  }
}
