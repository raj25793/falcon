package org.test.falcon.mongo.document;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.test.falcon.mongo.Enum.DeviceType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class ChildDevice implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    @Field(value = "dev_id")
    private String            deviceId;

    @Field(value = "type")
    private DeviceType        deviceType;

    public ChildDevice() {
        this.id = ObjectId.get().toHexString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

}
