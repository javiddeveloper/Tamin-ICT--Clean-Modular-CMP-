package com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class DeceasedInfoResponse(val data: List<String> = emptyList()) : BaseResponseNew() {

    fun getDeceasedInfo(): List<KeyValueModel> {
        if (data.size >= 5) {
            /*index 4 => deceased's Full name */
            return arrayListOf(KeyValueModel(_keyStringResId = R.string.full_name_deceased, _value = data[4]),
                /*index 5 => deceasedRelationShip*/
                KeyValueModel(_keyStringResId = R.string.deceased_relative, _value = data[5]))
        }
        return emptyList()
    }


}
