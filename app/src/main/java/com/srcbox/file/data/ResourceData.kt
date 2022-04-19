package com.srcbox.file.data

import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import okhttp3.MediaType

data class ResourceData(val title: String, val url: String,val mediaType: MediaType)