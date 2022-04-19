package com.srcbox.file.model

import com.srcbox.file.contract.ContractShitSmooth
import com.srcbox.file.ui.util.BlindWork
import java.io.InputStream

class ShitSmoothModel : ContractShitSmooth.Model {
    override fun getShitSmooth(wordFileIn: InputStream, title: String, len: Long): String {
        return BlindWork().speak(
            wordFileIn,
            title,
            len
        )
    }
}