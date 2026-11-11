package net.irext.ircontrol.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import net.irext.ircontrol.IRApplication;

import java.util.List;

/**
 * Filename:       RemoteControl.java
 * Revised:        Date: 2017-04-06
 * Revision:       Revision: 1.0
 * <p>
 * Description:    RemoteControl java bean
 * <p>
 * Revision log:
 * 2017-04-06: created by strawmanbobi
 */
@Entity(tableName = "RemoteControl")
@SuppressWarnings("unused")
public class RemoteControl {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "CategoryID")
    private int categoryId;

    @ColumnInfo(name = "CategoryName")
    private String categoryName;

    @ColumnInfo(name = "BrandID")
    private int brandId;

    @ColumnInfo(name = "BrandName")
    private String brandName;

    @ColumnInfo(name = "CityCode")
    private String cityCode;

    @ColumnInfo(name = "CityName")
    private String cityName;

    @ColumnInfo(name = "OperatorID")
    private String operatorId;

    @ColumnInfo(name = "OperatorName")
    private String operatorName;

    @ColumnInfo(name = "Remote")
    private String remote;

    @ColumnInfo(name = "Protocol")
    private String protocol;

    @ColumnInfo(name = "RemoteMap")
    private String remoteMap;

    @ColumnInfo(name = "SubCategory")
    private int subCategory;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRemoteMap() {
        return remoteMap;
    }

    public void setRemoteMap(String remoteMap) {
        this.remoteMap = remoteMap;
    }

    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    @Ignore
    public RemoteControl(int categoryId, String categoryName, int brandId, String brandName,
                         String cityCode, String cityName, String operatorId, String operatorName,
                         String remote, String protocol, String remoteMap, int subCategory) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.remote = remote;
        this.protocol = protocol;
        this.remoteMap = remoteMap;
        this.subCategory = subCategory;
    }

    public RemoteControl() {

    }

    public static List<RemoteControl> listRemoteControls(int from, int count) {
        return IRApplication.getDatabase().remoteControlDao().list(from, count);
    }

    public static long createRemoteControl(RemoteControl remoteControl) {
        return IRApplication.getDatabase().remoteControlDao().insert(remoteControl);
    }

    public static RemoteControl getRemoteControl(long remoteID) {
        return IRApplication.getDatabase().remoteControlDao().getById(remoteID);
    }

    public static void deleteRemoteControl(long remoteID) {
        IRApplication.getDatabase().remoteControlDao().deleteById(remoteID);
    }
}
