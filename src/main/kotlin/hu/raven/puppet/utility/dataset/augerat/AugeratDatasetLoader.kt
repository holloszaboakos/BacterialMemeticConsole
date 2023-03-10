package hu.raven.puppet.utility.dataset.augerat

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import hu.raven.puppet.model.dataset.augerat.InstanceBean
import java.io.File

object AugeratDatasetLoader {
    fun loadDataFromFile(filePath: String): InstanceBean {
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(this.javaClass.getResource(filePath)!!.openStream(), InstanceBean::class.java)
    }
}