package com.amw.datawave.data;

import lombok.Data;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Setter
@Data
@XmlRootElement(name = "List")
public class DataModelList {
    private List<DataModel> items;

    @XmlElement(name = "item")
    public List<DataModel> getItems() {
        return items;
    }

}