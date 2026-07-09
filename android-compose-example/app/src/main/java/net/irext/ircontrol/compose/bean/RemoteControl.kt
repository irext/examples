package net.irext.ircontrol.compose.bean

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * Filename:       RemoteControl.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Defines the persisted remote control database model.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

@Table(name = "RemoteControl")
class RemoteControl : Model {

    @Column(name = "CategoryID")
    var categoryId: Int = 0

    @Column(name = "CategoryName")
    var categoryName: String? = null

    @Column(name = "BrandID")
    var brandId: Int = 0

    @Column(name = "BrandName")
    var brandName: String? = null

    @Column(name = "CityCode")
    var cityCode: String? = null

    @Column(name = "CityName")
    var cityName: String? = null

    @Column(name = "OperatorID")
    var operatorId: String? = null

    @Column(name = "OperatorName")
    var operatorName: String? = null

    @Column(name = "Remote")
    var remote: String? = null

    @Column(name = "Protocol")
    var protocol: String? = null

    @Column(name = "RemoteMap")
    var remoteMap: String? = null

    @Column(name = "SubCategory")
    var subCategory: Int = 0

    @Column(name = "OrderIndex")
    var orderIndex: Long = 0L

    constructor() : super()

    constructor(
        categoryId: Int,
        categoryName: String?,
        brandId: Int,
        brandName: String?,
        cityCode: String?,
        cityName: String?,
        operatorId: String?,
        operatorName: String?,
        remote: String?,
        protocol: String?,
        remoteMap: String?,
        subCategory: Int,
    ) : super() {
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.brandId = brandId
        this.brandName = brandName
        this.cityCode = cityCode
        this.cityName = cityName
        this.operatorId = operatorId
        this.operatorName = operatorName
        this.remote = remote
        this.protocol = protocol
        this.remoteMap = remoteMap
        this.subCategory = subCategory
    }
}
